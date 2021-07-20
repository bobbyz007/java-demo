package com.example.concurrent.limit.app;

import com.google.common.util.concurrent.RateLimiter;

/**
 * 演示令牌桶算法应用： 平滑请求 requests/second
 */
public class SmoothBurstyExample {
    public static void main(String[] args) {
        // 每秒新增5个令牌
        RateLimiter limiter = RateLimiter.create(5);

        // 获取到一个令牌需要等待多久
        System.out.println(limiter.acquire());
        System.out.println(limiter.acquire());
        System.out.println(limiter.acquire());
        System.out.println(limiter.acquire());
        System.out.println(limiter.acquire());
        System.out.println(limiter.acquire());
    }
}
