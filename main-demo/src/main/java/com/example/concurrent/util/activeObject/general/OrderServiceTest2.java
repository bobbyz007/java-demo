package com.example.concurrent.util.activeObject.general;

import com.example.concurrent.util.activeObject.OrderService;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static java.lang.Thread.currentThread;

/**
 * 修改为更通用的实现方式： 注解 + 动态代理
 *
 */
public class OrderServiceTest2 {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        OrderService orderService = ActiveServiceFactory.active(new OrderServiceImpl2());

        orderService.order("hello", 123);
        System.out.println("order return immediately");


        Future<String> future = orderService.findOrderDetails(123);
        System.out.println("find return immediately");
        System.out.println("order detail: " + future.get());

        // 堵塞主线程
        currentThread().join();
    }
}
