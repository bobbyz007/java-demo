package com.example.concurrent.limit.app;

import com.google.common.util.concurrent.RateLimiter;

import java.util.concurrent.TimeUnit;

/**
 * 类似漏桶型算法实现
 *
 * 因为令牌桶算法允许突发，比如突发10个请求: limiter.acquire(10)，所以提供了另外一种机制，从冷启动到稳定速率有个warmup过渡
 */
public class RateLimiterExample2 {
    // 在warmup period时间内慢慢趋向稳定速率
    private static RateLimiter rateLimiter = RateLimiter.create(5, 1000, TimeUnit.MILLISECONDS);

    public static void main(String[] args) throws InterruptedException {
        // 等待时间从大到小，也即速率慢慢增大
        for (int i = 0; i < 5; i++) {
            System.out.println(rateLimiter.acquire());
        }
        Thread.sleep(1000);
        for (int i = 0; i < 5; i++) {
            System.out.println(rateLimiter.acquire());
        }
    }


}
