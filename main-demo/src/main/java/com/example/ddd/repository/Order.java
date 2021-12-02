package com.example.ddd.repository;

public class Order implements Aggregate<OrderId>{
    @Override
    public OrderId getId() {
        return new OrderId("orderId");
    }
}
