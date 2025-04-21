package com.nhom4.nhtsstore.ui.navigation;

import java.util.HashMap;
import java.util.Map;

public class RouteParams {
    private final Map<String, Object> params = new HashMap<>();

    public void set(String key, Object value) {
        params.put(key, value);
    }

    public Object get(String key) {
        return params.get(key);
    }

    public <T> T get(String key, Class<T> type) {
        Object value = params.get(key);
        if (value == null) {
            return null;
        }
        if (!type.isInstance(value)) {
            throw new IllegalArgumentException("Value for key '" + key + "' is not of type " + type.getName());
        }
        return (T) value;
    }

    @Override
    public String toString() {
        StringBuilder url = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            url.append(first ? "?" : "&");
            url.append(entry.getKey()).append("=").append(entry.getValue());
            first = false;
        }
        return url.toString();
    }
}