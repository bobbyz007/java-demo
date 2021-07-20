package com.example.concurrent.limit.app;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 简单粗暴的方式限制某个接口的每秒的并发或请求数，没有平滑处理
 */
public class LimitConcurrentCountPerSecond {
    private static LoadingCache<Long, AtomicLong> counter = CacheBuilder.newBuilder()
            .expireAfterWrite(2, TimeUnit.SECONDS)
            .build(new CacheLoader<Long, AtomicLong>() {
                @Override
                public AtomicLong load(Long key) throws Exception {
                    return new AtomicLong(0);
                }
            });

    public static void main(String[] args) throws ExecutionException {
        long limit = 5;
        for (int i = 0; i < 20; i++) {
            // 以当前秒数来计算并发数
            long currentSecond = System.currentTimeMillis() / 1000;
            if (counter.get(currentSecond).incrementAndGet() > limit) {
                System.out.println("refused request: " + i);
                continue;
            }
            System.out.println("process: " + i);
            
        }
    }
}
