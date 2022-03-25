package com.example.concurrent.util.activeObject;

import java.util.Map;

public abstract class MethodMessage {
    protected final Map<String, Object> params;

    protected final OrderService orderService;

    public MethodMessage(Map<String, Object> params, OrderService orderService) {
        this.params = params;
        this.orderService = orderService;
    }

    public abstract void execute();
}
