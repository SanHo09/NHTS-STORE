
package com.nhom4.nhtsstore.ui;

import com.nhom4.nhtsstore.configuration.cart.CartConfig;
import com.nhom4.nhtsstore.ui.shared.ThemeManager;
import com.nhom4.nhtsstore.viewmodel.cart.CartVm;
import com.nhom4.nhtsstore.viewmodel.user.UserSessionVm;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Date;

@Service
@Getter
@Slf4j
public class ApplicationState {
    // Authentication state
    private final BooleanProperty authenticated = new SimpleBooleanProperty(false);
    private final ObjectProperty<UserSessionVm> currentUser = new SimpleObjectProperty<>();
    // Cart state
    private final ObjectProperty<CartVm> cart = new SimpleObjectProperty<>(new CartVm());

    //Order state
    private final SimpleMapProperty<String,String> orderQrCodeByTransactionId = new SimpleMapProperty<>(FXCollections.observableHashMap());
    //Ui State
    @Getter
    private final ObjectProperty<AppView> currentView = new SimpleObjectProperty<>();
    public final ApplicationContext applicationContext;
    private final ThemeManager themeManager;
    private final CartConfig cartConfig;
    public ApplicationState(ApplicationContext applicationContext, ThemeManager themeManager, CartConfig cartConfig) {
        this.applicationContext = applicationContext;
        this.themeManager = themeManager;
        this.cartConfig = cartConfig;
//        setCartListener();
    }

    public BooleanProperty authenticatedProperty() {
        return authenticated;
    }
    public boolean isAuthenticated() {
        return authenticated.getValue();
    }
    public ObjectProperty<UserSessionVm> currentUserProperty() {
        return currentUser;
    }
    public ObjectProperty<CartVm> cartProperty() {
        return cart;
    }
    public ObjectProperty<AppView> currentViewProperty() {
        return currentView;
    }
    public ObjectProperty<CartVm> getCartProperty() {
        return cart;
    }
    public CartVm getCart() {
        return cart.getValue();
    }
    public void setOrderQrCodeByTransactionId(String transactionId, String qrCode) {
        if (transactionId != null && qrCode != null) {
            log.debug("Putting order_url in map: {}", qrCode);
            orderQrCodeByTransactionId.put(transactionId, qrCode);
            log.debug("Successfully put order_url in map. Map size: {}", orderQrCodeByTransactionId.size());
        } else {
            log.debug("Not putting order_url in map because transactionId or qrCode is null");
        }
    }
    public String getOrderQrCodeByTransactionId(String transactionId) {
        log.debug("ApplicationState.getOrderQrCodeByTransactionId called with transactionId: {}", transactionId);
        String qrCode = orderQrCodeByTransactionId.get(transactionId);
        log.debug("Retrieved order_url from map: {}", qrCode);
        return qrCode;
    }

    public UserSessionVm getCurrentUser() {
        return currentUser.getValue();
    }



    public void setCart(CartVm cart) {
        CartVm newCart = new CartVm();
        newCart.setUserId(cart.getUserId());
        newCart.setCreatedDate(cart.getCreatedDate());
        newCart.setLastModifiedDate(new Date());
        newCart.setTotalAmount(cart.getTotalAmount());
        newCart.setItems(new ArrayList<>(cart.getItems())); // Copy all items

        this.cart.set(newCart);

        // Save to cart config
        cartConfig.saveCart(currentUser.getValue().getUserId(), newCart);
    }
    public void login(UserSessionVm user) {
        currentUser.set(user);
        authenticated.set(true);

        try {
            CartVm cartResult = cartConfig.getCart(user.getUserId());
            if (cartResult == null) {
                cartResult = new CartVm();
                cartResult.setUserId(user.getUserId());
                cartResult.setCreatedDate(new Date());
                cartResult.setLastModifiedDate(new Date());
                cartResult.setItems(new ArrayList<>());
            }
            this.cart.set(cartResult);
        } catch (Exception e) {
            log.error("Error loading cart during login: ", e);
            // Create new cart if loading fails
            CartVm newCart = new CartVm();
            newCart.setUserId(user.getUserId());
            newCart.setCreatedDate(new Date());
            newCart.setLastModifiedDate(new Date());
            newCart.setItems(new ArrayList<>());
            this.cart.set(newCart);
        }
    }

    public void updateUserSession(UserSessionVm user) {
        if (user != null) {
            currentUser.set(user);
        }
    }
    public void logout() {
        currentUser.set(null);
        authenticated.set(false);
        cart.set(new CartVm());
        SecurityContextHolder.clearContext();

    }


    public JPanel getViewPanelByBean(Class<? extends JPanel> panelClass) {
        if (panelClass == null) {
            return null;
        }
        Object bean = applicationContext.getBean(panelClass);
        if (bean instanceof JPanel) {
            return (JPanel) bean;
        } else {
            throw new IllegalArgumentException("Bean is not a JPanel: " + panelClass);
        }
    }
}
