
package com.nhom4.nhtsstore.ui;
import com.nhom4.nhtsstore.entities.rbac.User;
import com.nhom4.nhtsstore.viewmodel.user.UserSessionVm;
import javafx.beans.property.*;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.swing.*;

@Service
public class ApplicationState {
    // Authentication state
    private final BooleanProperty authenticated = new SimpleBooleanProperty(false);
    private final ObjectProperty<UserSessionVm> currentUser = new SimpleObjectProperty<>();
    private final MapProperty<String, Object> cachedData = new SimpleMapProperty<>();
    @Getter
    private final ApplicationContext applicationContext;

    public ApplicationState(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
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
    public UserSessionVm getCurrentUser() {
        return currentUser.getValue();
    }


    public void login(UserSessionVm user) {
        currentUser.set(user);
        authenticated.set(true);

    }

    public void logout() {
        currentUser.set(null);
        authenticated.set(false);
        cachedData.clear();
    }
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