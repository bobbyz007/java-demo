package com.example.concurrent.util.activeObject;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FindOrderDetailsMessage extends MethodMessage{
    public FindOrderDetailsMessage(Map<String, Object> params, OrderService orderService) {
        super(params, orderService);
    }

    @Override
    public void execute() {
        Future<String> realFuture = orderService.findOrderDetails((long) params.get("orderId"));

        ActiveFuture<String> activeFuture = (ActiveFuture<String>) params.get("activeFuture");
        try {
            String result = realFuture.get();
            activeFuture.set(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
