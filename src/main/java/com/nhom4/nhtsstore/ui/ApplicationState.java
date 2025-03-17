
package com.nhom4.nhtsstore.ui;
import com.nhom4.nhtsstore.viewmodel.user.UserSessionVm;
import javafx.beans.property.*;
import org.springframework.stereotype.Service;
import com.nhom4.nhtsstore.entities.User;

@Service
public class ApplicationState {
    // Authentication state
    private final BooleanProperty authenticated = new SimpleBooleanProperty(false);
    private final ObjectProperty<UserSessionVm> currentUser = new SimpleObjectProperty<>();

    // UI state
    private final StringProperty currentView = new SimpleStringProperty("login");
    private final BooleanProperty loading = new SimpleBooleanProperty(false);

    // Application data state
    private final MapProperty<String, Object> cachedData = new SimpleMapProperty<>();

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

    public StringProperty currentViewProperty() {
        return currentView;
    }

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
        currentView.set("login");
        cachedData.clear();
    }

    // Cache management
    public void cacheData(String key, Object data) {
        cachedData.put(key, data);
    }

    public Object getCachedData(String key) {
        return cachedData.get(key);
    }
}