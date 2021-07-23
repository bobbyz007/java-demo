package com.example.concurrent.limit.app;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.google.common.util.concurrent.RateLimiter;

import java.util.concurrent.TimeUnit;

/**
 * 令桶牌算法实现，guava提供的RateLimiteer有如下特点:
 * <p>RateLimiter由于会累积令牌，所以可以应对突发流量。也就是说如果同时请求5个令牌，由于此时令牌桶中有累积的令牌，能
 * 够快速响应请求。
 * <p>RateLimiter在没有足够的令牌发放时，采用的是滞后的方式进行处理，也就是前一个请求获取令牌所需要等待的时间由下一次
 * 请求来承受和弥补，也就是代替前一个请求进行等待。
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

        burst();
    }

    private static void handle(int i) {
        System.out.println(i);
    }

    private static void burst() {
        RateLimiter limiter = RateLimiter.create(5);

        /**
         * 令牌桶算法允许突发, 输出结果类似(支持第一个请求的突发流量，但第二个请求几乎等了2秒，等待令牌桶中放入10个令牌)：
         * 0.0
         * 1.999351
         * 0.199012
         */
        System.out.println(limiter.acquire(10));
        System.out.println(limiter.acquire(1));
        System.out.println(limiter.acquire(1));
    }
}
