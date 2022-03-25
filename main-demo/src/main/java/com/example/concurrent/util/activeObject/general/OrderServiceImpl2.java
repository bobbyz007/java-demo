package com.example.concurrent.util.activeObject.general;

import com.example.concurrent.util.activeObject.OrderService;
import com.google.common.util.concurrent.Futures;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class OrderServiceImpl2 implements OrderService {
    @Override
    public Future<String> findOrderDetails(long orderId) {
        return Futures.submit(() -> {
            try {
                TimeUnit.SECONDS.sleep(10);
                System.out.println("Find order details: " + orderId);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Order Details";
        }, Executors.newCachedThreadPool());
    }

    @Override
    public void order(String account, long orderId) {
        try {
            TimeUnit.SECONDS.sleep(10);
            System.out.println("Process the order for account: " + account + ", order id: " + orderId);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
