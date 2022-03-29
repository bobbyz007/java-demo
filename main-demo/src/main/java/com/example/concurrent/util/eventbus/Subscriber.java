package com.example.concurrent.util.eventbus;

import java.lang.reflect.Method;

public class Subscriber {
    private final Object object;
    private final Method method;

    private boolean disabled;

    public Subscriber(Object object, Method method) {
        this.object = object;
        this.method = method;
    }

    public Object getObject() {
        return object;
    }

    public Method getMethod() {
        return method;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
