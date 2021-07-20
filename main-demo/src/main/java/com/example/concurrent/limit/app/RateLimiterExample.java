package com.example.concurrent.limit.app;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.google.common.util.concurrent.RateLimiter;

import java.util.concurrent.TimeUnit;

/**
 * 令桶牌算法实现
 */
public class RateLimiterExample {
    private static RateLimiter rateLimiter = RateLimiter.create(5);

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            /*if (rateLimiter.tryAcquire(190, TimeUnit.MILLISECONDS)) {
                handle(i);
            }*/
            rateLimiter.acquire();
            handle(i);
        }
    }

    private static void handle(int i) {
        System.out.println(i);
    }

    private static void burst() {
        RateLimiter limiter = RateLimiter.create(5);
        // 令牌桶算法允许突发
        System.out.println(limiter.acquire(10));
        System.out.println(limiter.acquire(1));
        System.out.println(limiter.acquire(1));
    }
}
