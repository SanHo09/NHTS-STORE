package com.nhom4.nhtsstore.services.payment.impl.zalopay;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhom4.nhtsstore.configuration.ZalopayConfig;
import com.nhom4.nhtsstore.entities.Order;
import com.nhom4.nhtsstore.entities.OrderDetail;
import com.nhom4.nhtsstore.enums.PaymentStatus;
import com.nhom4.nhtsstore.services.payment.PaymentStrategy;
import com.nhom4.nhtsstore.services.payment.impl.zalopay.util.HMACUtil;
import com.nhom4.nhtsstore.ui.ApplicationState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
@Slf4j
public class ZaloPayPaymentStrategy implements PaymentStrategy {
    private final ObjectMapper objectMapper;
    private final ApplicationState applicationState;
    private final RestClient restClient;

    public ZaloPayPaymentStrategy(RestClient.Builder restClientBuilder, ApplicationState applicationState) {
        this.restClient = restClientBuilder.build();
        this.applicationState = applicationState;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Gets the current time string in the specified format
     * @param format The format to use
     * @return The current time string
     */
    public String getCurrentTimeString(String format) {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
        SimpleDateFormat fmt = new SimpleDateFormat(format);
        fmt.setCalendar(cal);
        return fmt.format(cal.getTimeInMillis());
    }

    /**
     * Creates a ZaloPay order
     * @param orderRequest The order request parameters
     * @return The response from ZaloPay
     */
    public String createOrder(Map<String, Object> orderRequest) {
        Object amount = orderRequest.get("amount");
        if (amount == null) {
            return "{\"error\": \"Amount is required\"}";
        }

        Map<String, Object> order = new HashMap<>();
        order.put("app_id", ZalopayConfig.config.get("app_id"));
        order.put("app_trans_id", orderRequest.get("app_trans_id"));
        order.put("app_time", System.currentTimeMillis());
        order.put("app_user", orderRequest.get("app_user"));
        order.put("amount", amount);
        order.put("description", orderRequest.get("description"));
        order.put("bank_code", "");
        order.put("item", orderRequest.get("item"));
        order.put("embed_data", "{}");
        order.put("currency", orderRequest.get("currency"));
        order.put("title", orderRequest.get("title"));

        String data = order.get("app_id") + "|" + order.get("app_trans_id") + "|" + order.get("app_user") + "|"
                + order.get("amount") + "|" + order.get("app_time") + "|" + order.get("embed_data") + "|"
                + order.get("item");

        String mac = HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, ZalopayConfig.config.get("key1"), data);
        order.put("mac", mac);

        log.debug("Generated MAC: {}", mac);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        for (Map.Entry<String, Object> entry : order.entrySet()) {
            params.add(entry.getKey(), entry.getValue().toString());
        }

        try {
            String response = restClient.post()
                    .uri(ZalopayConfig.config.get("endpoint"))
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(params)
                    .retrieve()
                    .body(String.class);

            log.debug("Zalopay Response: {}", response);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"Failed to create order: " + e.getMessage() + "\"}";
        }
    }

    /**
     * Gets the status of a ZaloPay order
     * @param appTransId The transaction ID
     * @return The response from ZaloPay
     */
    public String getOrderStatus(String appTransId) {
        String data = ZalopayConfig.config.get("app_id") + "|" + appTransId + "|" + ZalopayConfig.config.get("key1");
        String mac = HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, ZalopayConfig.config.get("key1"), data);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("app_id", ZalopayConfig.config.get("app_id"));
        params.add("app_trans_id", appTransId);
        params.add("mac", mac);

        try {

            return restClient.post()
                    .uri(ZalopayConfig.config.get("orderstatus"))
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(params)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"Failed to get order status: " + e.getMessage() + "\"}";
        }
    }

    @Override
    public boolean processPayment(Order order, BigDecimal amount) {
        try {
            // Create items array for ZaloPay
            List<Map<String, Object>> items = new ArrayList<>();
            for (OrderDetail detail : order.getOrderDetails()) {
                Map<String, Object> zaloItem = new HashMap<>();
                zaloItem.put("itemid", detail.getProduct().getId());
                zaloItem.put("itemname", detail.getProduct().getName());
                zaloItem.put("itemprice", detail.getUnitPrice());
                zaloItem.put("itemquantity", detail.getQuantity());
                items.add(zaloItem);
            }

            // Generate a unique transaction ID
            Random rand = new Random();
            int randomId = rand.nextInt(1000000);
            String appTransId = getCurrentTimeString("yyMMdd") + "_" + randomId;

            // Prepare the order request
            Map<String, Object> orderRequest = new HashMap<>();
            orderRequest.put("app_trans_id", appTransId);
            orderRequest.put("amount", (long) (amount.doubleValue() * 100));
            orderRequest.put("title", "Payment for order #" + order.getId());
            orderRequest.put("description", "NHTS Store - Order #" + order.getId());
            orderRequest.put("item", objectMapper.writeValueAsString(items));
            orderRequest.put("embed_data", "");
            orderRequest.put("currency", "USD");
            orderRequest.put("app_user", applicationState.getCurrentUser().getFullName());

            // Create order and process response
            String response = createOrder(orderRequest);
            Map<String, Object> jsonResponse = objectMapper.readValue(response,
                    new TypeReference<Map<String, Object>>() {});
            if (jsonResponse.containsKey(getQRCodeFieldName())) {
                String orderUrl = jsonResponse.get(getQRCodeFieldName()).toString();
                // Add prefix to transaction ID
                String prefixedTransId = "ZL_" + appTransId;
                order.setPaymentTransactionId(prefixedTransId);
                log.debug("Set payment transaction ID: {}", prefixedTransId);
                order.setPaymentStatus(PaymentStatus.PENDING);

                try {
                    applicationState.setOrderQrCodeByTransactionId(prefixedTransId, orderUrl);
                    log.debug("Successfully stored order_url in ApplicationState");
                } catch (Exception e) {
                    log.error("Error storing order_url in ApplicationState: {}", e.getMessage());
                    e.printStackTrace();
                }
                return true;
            } else {
                order.setPaymentStatus(PaymentStatus.FAILED);
                return false;
            }
        } catch (Exception e) {
            order.setPaymentStatus(PaymentStatus.FAILED);
            return false;
        }
    }

    @Override
    public PaymentStatus checkPaymentStatus(String transactionId) {
        if (transactionId == null || transactionId.isEmpty()) {
            return PaymentStatus.CANCELLED;
        }

        try {
            // Remove prefix if present
            String actualTransId = transactionId;
            if (transactionId.startsWith("ZL_")) {
                actualTransId = transactionId.substring(3);
            }

            String statusResponse = getOrderStatus(actualTransId);
            Map<String, Object> jsonResponse = objectMapper.readValue(statusResponse,
                    new TypeReference<Map<String, Object>>() {});

            if (jsonResponse.containsKey("return_code")) {
                int returnCode = Integer.parseInt(jsonResponse.get("return_code").toString());

                if (returnCode == 1) {
                    return PaymentStatus.COMPLETED;
                } else if (returnCode == 3) {
                    return PaymentStatus.PENDING;
                } else {
                    return PaymentStatus.FAILED;
                }
            }
            return PaymentStatus.PENDING;
        } catch (Exception e) {
            return PaymentStatus.FAILED;
        }
    }

    public Map<String, Object> getPaymentDetails(String transactionId) {
        try {
            String response = getOrderStatus(transactionId);
            return objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }
    @Override
    public Optional<String> getQRCodeUrl(Order order) {
        if (order == null || order.getPaymentTransactionId() == null) {
            return Optional.empty();
        }

        String transactionId = order.getPaymentTransactionId();
        // First try to get the order_url from ApplicationState
        if(applicationState.getOrderQrCodeByTransactionId(transactionId) != null) {
            String orderUrl = applicationState.getOrderQrCodeByTransactionId(transactionId);
            log.debug("Retrieved order_url from ApplicationState for transaction {}: {}", transactionId, orderUrl);
            return Optional.of(orderUrl);
        }
        // If not found in ApplicationState, try to get it from the order status as a fallback
        try {
            Map<String, Object> details = getPaymentDetails(transactionId);
            log.debug("is contains order_url: {}", details.containsKey("order_url"));
            if (details.containsKey("order_url")) {
                String orderUrl = details.get("order_url").toString();
                // Store it in ApplicationState for future use
                applicationState.setOrderQrCodeByTransactionId(transactionId, orderUrl);
                log.debug("Retrieved order_url from API for transaction {}: {}", transactionId, orderUrl);
                return Optional.of(orderUrl);
            }
            log.debug("No order_url found for transaction {}", transactionId);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error getting order_url for transaction {}: {}", transactionId, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public String getQRCodeFieldName() {
        return "order_url";
    }

}
