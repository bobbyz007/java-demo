package com.example.concurrent.util.activeObject;

import java.util.Map;

public class OrderMessage extends MethodMessage{
    public OrderMessage(Map<String, Object> params, OrderService orderService) {
        super(params, orderService);
    }

    @Override
    public void execute() {
        String account = (String) params.get("account");
        long orderId = (long) params.get("orderId");

        orderService.order(account, orderId);
    }
}
