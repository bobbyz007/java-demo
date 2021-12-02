package com.example.ddd.repository;

public interface OrderRepository extends Repository<Order, OrderId>{
    long count(OrderQueryDto queryDto);

    Page<Order> query(OrderQueryDto queryDto);

    Order findInStore(OrderId id, StoreId storeId);
}


