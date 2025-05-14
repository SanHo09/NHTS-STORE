package com.nhom4.nhtsstore.ui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.nhom4.nhtsstore.entities.Customer;
import com.nhom4.nhtsstore.entities.Order;
import com.nhom4.nhtsstore.entities.OrderDetail;
import com.nhom4.nhtsstore.entities.Product;
import com.nhom4.nhtsstore.services.ZaloPayService;
import org.springframework.stereotype.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

@Controller
public class TestZaloPayForm extends JPanel {
    private final ZaloPayService zalopayService;
    private final JButton createPaymentButton;
    private final JTextArea responseArea;
    private final ObjectMapper objectMapper;
    private final JLabel qrCodeLabel;
    private javax.swing.Timer statusCheckTimer;
    private String currentAppTransId;

    public TestZaloPayForm(ZaloPayService zalopayService) {
        this.zalopayService = zalopayService;
        this.objectMapper = new ObjectMapper();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        qrCodeLabel = new JLabel();
        qrCodeLabel.setPreferredSize(new Dimension(300, 300));
        qrCodeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        createPaymentButton = new JButton("Create ZaloPay Payment");
        responseArea = new JTextArea(10, 40);
        responseArea.setEditable(false);
        
        createPaymentButton.addActionListener(e -> createPayment());
        
        add(createPaymentButton);
        add(new JScrollPane(responseArea));
        add(qrCodeLabel);
    }

    private void createPayment(Order order) {
        try {
            // Create items array for ZaloPay
            List<Map<String, Object>> items = new ArrayList<>();
            for (OrderDetail detail : order.getOrderDetails()) {
                Map<String, Object> item = new HashMap<>();
                item.put("itemid", detail.getProduct().getId());
                item.put("itemname", detail.getProduct().getName());
                item.put("itemprice", (long) (detail.getProduct().getSalePrice()));
                item.put("itemquantity", detail.getQuantity());
                items.add(item);
            }

            String itemsJson = objectMapper.writeValueAsString(items);
            String embedData = "{\"promotioninfo\":\"\",\"merchantinfo\":\"embeddata123\"}";
            Random rand = new Random();
            int randomId = rand.nextInt(1000000);
            currentAppTransId = zalopayService.getCurrentTimeString("yyMMdd") + "_" + randomId;

            Map<String, Object> orderRequest = new HashMap<>();
            orderRequest.put("app_trans_id", currentAppTransId);
            orderRequest.put("amount", (long) (order.getTotalAmount())*100);
            orderRequest.put("description", "NHTS Store - Payment for order #" + order.getId());
            orderRequest.put("item", itemsJson);
            orderRequest.put("embed_data", embedData);
            orderRequest.put("currency", "USD");
            orderRequest.put("app_user", order.getCustomer().getEmail());

            String response = zalopayService.createOrder(orderRequest);

            Map<String, Object> jsonResponse = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {
            });

            if (jsonResponse.containsKey("order_url")) {
                String orderUrl = jsonResponse.get("order_url").toString();
                displayQRCode(orderUrl);
                startStatusChecking();
            }

            responseArea.setText("Payment created successfully!\n" + response);

        } catch (Exception ex) {
            responseArea.setText("Error creating payment: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    private void startStatusChecking() {
        // Stop existing timer if any
        if (statusCheckTimer != null && statusCheckTimer.isRunning()) {
            statusCheckTimer.stop();
        }

        // Create new timer to check status every 5 seconds
        statusCheckTimer = new javax.swing.Timer(5000, e -> checkOrderStatus());
        statusCheckTimer.start();
    }

    private void checkOrderStatus() {
        if (currentAppTransId == null) {
            return;
        }

        try {
            String statusResponse = zalopayService.getOrderStatus(currentAppTransId);

            // Parse JSON response string thành Map
            Map<String, Object> statusJson = objectMapper.readValue(statusResponse, new TypeReference<Map<String, Object>>() {
            });

            // Update status in response area
            String currentText = responseArea.getText();
            String statusInfo = "\nOrder Status: " + statusJson.toString();
            responseArea.setText(currentText + statusInfo);

            // Stop checking if payment is completed or failed
            Object returnCodeObj = statusJson.get("return_code");
            if (returnCodeObj instanceof Number) {
                int returnCode = ((Number) returnCodeObj).intValue();

                if (returnCode == 1) {
                    statusCheckTimer.stop();
                    responseArea.append("\nPayment completed!");
                } else if (returnCode == -1) {
                    statusCheckTimer.stop();
                    responseArea.append("\nPayment failed!");
                }
            }

        } catch (Exception ex) {
            responseArea.append("\nError checking status: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    private void displayQRCode(String url) {
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(
                url,
                BarcodeFormat.QR_CODE,
                300, // width
                300  // height
            );

            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            
            qrCodeLabel.setIcon(new ImageIcon(qrImage));
            
            JLabel urlLabel = new JLabel(url);
            urlLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(urlLabel);
            
            revalidate();
            repaint();
            
        } catch (WriterException e) {
            responseArea.setText("Error generating QR code: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createPayment() {
        // Create test order with details
        Order testOrder = new Order();
        testOrder.setId(1L);

        
        // Create test customer
        Customer testCustomer = new Customer();
        testCustomer.setEmail("test@example.com");
        testOrder.setCustomer(testCustomer);
        
        // Create test products
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setSalePrice(5.0);
        
        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setSalePrice(1.0);
        
        // Create order details
        List<OrderDetail> orderDetails = new ArrayList<>();
        
        OrderDetail detail1 = new OrderDetail();
        detail1.setProduct(product1);
        detail1.setQuantity(1);
        orderDetails.add(detail1);
        
        OrderDetail detail2 = new OrderDetail();
        detail2.setProduct(product2);
        detail2.setQuantity(1);
        orderDetails.add(detail2);
        
        testOrder.setOrderDetails(orderDetails);
        testOrder.setTotalAmount(testOrder.getOrderDetails().stream().reduce(0.0, (sum, detail) -> sum + (detail.getProduct().getSalePrice() * detail.getQuantity()), Double::sum));
        System.out.println("Total Amount: " + testOrder.getTotalAmount());
        createPayment(testOrder);
    }
}
