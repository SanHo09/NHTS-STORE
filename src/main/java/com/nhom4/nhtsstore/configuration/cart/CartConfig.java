package com.nhom4.nhtsstore.configuration.cart;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nhom4.nhtsstore.viewmodel.cart.CartVm;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class CartConfig {
    private final String CART_KEY = "user.cart";
    private Map<String, CartVm> userCarts = new HashMap<>();
    private final ObjectMapper objectMapper;

    public CartConfig() {
        this.objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        // Add JavaTimeModule to handle Date serialization
        objectMapper.registerModule(new JavaTimeModule());
        loadUserCarts();
    }

    public void saveCart(Long userId, CartVm cart) {
        userCarts.put(String.valueOf(userId), cart);
        saveUserCarts();
    }

    public CartVm getCart(Long userId) {
        CartVm cart = userCarts.get(String.valueOf(userId));
        return cart != null ? cart : createNewCart(userId);
    }

    private CartVm createNewCart(Long userId) {
        CartVm cart = new CartVm();
        cart.setUserId(userId);
        cart.setCreatedDate(new Date());
        cart.setLastModifiedDate(new Date());
        cart.setItems(new ArrayList<>());
        cart.setTotalAmount(BigDecimal.ZERO);
        return cart;
    }

    private File getUserCartsFile() {
        String userHome = System.getProperty("user.home");
        String appFolder = userHome + File.separator + ".nhtsstore";
        File folder = new File(appFolder);

        if (!folder.exists()) {
            folder.mkdirs();
        }

        return new File(folder, "user_carts.json");
    }

    private void loadUserCarts() {
        try {
            File file = getUserCartsFile();
            if (file.exists()) {
                // Use TypeReference to properly deserialize the Map with CartVm values
                userCarts = objectMapper.readValue(file,
                        new TypeReference<Map<String, CartVm>>() {});
            } else {
                userCarts = new HashMap<>();
                saveUserCarts();
            }
        } catch (IOException e) {
            System.err.println("Failed to load user carts: " + e.getMessage());
            userCarts = new HashMap<>();
        }
    }

    private void saveUserCarts() {
        try {
            objectMapper.writeValue(getUserCartsFile(), userCarts);
        } catch (IOException e) {
            System.err.println("Failed to save user carts: " + e.getMessage());
        }
    }

}