package com.example.concurrent.util.activeObject;

public class OrderServiceFactory {
    // 启动了后台线程
    private final static ActiveMessageQueue messageQueue = new ActiveMessageQueue();

    private OrderServiceFactory() {
    }

    public static OrderService toActiveObject(OrderService orderService) {
        return new OrderServiceProxy(orderService, messageQueue);
    }
}
