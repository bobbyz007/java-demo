package com.example.concurrent.util.activeObject;

import com.example.concurrent.util.activeObject.general.ActiveMethod;

import java.util.concurrent.Future;

public interface OrderService {
    @ActiveMethod
    Future<String> findOrderDetails(long orderId);

    @ActiveMethod
    void order(String account, long orderId);
}
