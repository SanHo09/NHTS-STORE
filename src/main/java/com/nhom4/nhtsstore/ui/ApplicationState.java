
package com.nhom4.nhtsstore.ui;
import com.nhom4.nhtsstore.viewmodel.user.UserSessionVm;
import javafx.beans.property.*;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import com.nhom4.nhtsstore.entities.User;

import javax.swing.*;

@Service
public class ApplicationState {
    // Authentication state
    private final BooleanProperty authenticated = new SimpleBooleanProperty(false);
    private final ObjectProperty<UserSessionVm> currentUser = new SimpleObjectProperty<>();

    // UI state
//    private final ObjectProperty<ViewName> currentView = new SimpleObjectProperty<>(ViewName.LOGIN);
    private final BooleanProperty loading = new SimpleBooleanProperty(false);

    // Application data state
    private final MapProperty<String, Object> cachedData = new SimpleMapProperty<>();
    // Spring application context
    @Getter
    private final ApplicationContext applicationContext;

    public ApplicationState(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    // Getters for properties
    public BooleanProperty authenticatedProperty() {
        return authenticated;
    }
    public boolean isAuthenticated() {
        return authenticated.getValue();
    }

    public ObjectProperty<UserSessionVm> currentUserProperty() {
        return currentUser;
    }
    public UserSessionVm getCurrentUser() {
        return currentUser.getValue();
    }

//    public ObjectProperty<ViewName> currentViewProperty() {
//        return currentView;
//    }

    public BooleanProperty loadingProperty() {
        return loading;
    }

    // Methods to modify state
    public void login(UserSessionVm user) {
        currentUser.set(user);
        authenticated.set(true);
    }

    public void logout() {
        currentUser.set(null);
        authenticated.set(false);
        cachedData.clear();
    }

    // Cache management
    public void cacheData(String key, Object data) {
        cachedData.put(key, data);
    }

    public Object getCachedData(String key) {
        return cachedData.get(key);
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