package com.nhom4.nhtsstore.services;

import com.nhom4.nhtsstore.configuration.ZalopayConfig;
import com.nhom4.nhtsstore.utils.HMACUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ZaloPayService {


    private final RestClient restClient;
    public ZaloPayService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .build();
    }
    public static String getCurrentTimeString(String format) {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
        SimpleDateFormat fmt = new SimpleDateFormat(format);
        fmt.setCalendar(cal);
        return fmt.format(cal.getTimeInMillis());
    }

    public String createOrder(Map<String, Object> orderRequest) {
        Object amount = orderRequest.get("amount");
        if (amount == null) {
            return "{\"error\": \"Amount is required\"}";
        }

        Map<String, Object> order = new HashMap<>();
        order.put("app_id", ZalopayConfig.config.get("app_id"));
        order.put("app_trans_id", orderRequest.get("app_trans_id"));
        order.put("app_time", System.currentTimeMillis());
        order.put("app_user", "user123");
        order.put("amount", amount);
        order.put("description", orderRequest.get("description"));
        order.put("bank_code", "");
        order.put("item", orderRequest.get("item"));
        order.put("embed_data", "{}");
        order.put("currency", orderRequest.get("currency"));

        String data = order.get("app_id") + "|" + order.get("app_trans_id") + "|" + order.get("app_user") + "|"
                + order.get("amount") + "|" + order.get("app_time") + "|" + order.get("embed_data") + "|"
                + order.get("item");

        String mac = HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, ZalopayConfig.config.get("key1"), data);
        order.put("mac", mac);

        System.out.println("Generated MAC: " + mac);

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

            System.out.println("Zalopay Response: " + response);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"Failed to create order: " + e.getMessage() + "\"}";
        }
    }

    public String getOrderStatus(String appTransId) {
        String data = ZalopayConfig.config.get("app_id") + "|" + appTransId + "|" + ZalopayConfig.config.get("key1");
        String mac = HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, ZalopayConfig.config.get("key1"), data);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("app_id", ZalopayConfig.config.get("app_id"));
        params.add("app_trans_id", appTransId);
        params.add("mac", mac);

        try {
            String response = restClient.post()
                    .uri(ZalopayConfig.config.get("orderstatus"))
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(params)
                    .retrieve()
                    .body(String.class);

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"Failed to get order status: " + e.getMessage() + "\"}";
        }
    }
}
