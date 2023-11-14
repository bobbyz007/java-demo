package com.example.resillience4j;

import io.github.resilience4j.bulkhead.*;
import io.github.resilience4j.core.functions.CheckedSupplier;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

public class BulkheadExample {
    public static void main(String[] args) throws InterruptedException {
        BulkheadConfig config = BulkheadConfig.custom()
                .maxConcurrentCalls(5)
                .maxWaitDuration(Duration.ofMillis(5000))
                .build();
        BulkheadRegistry bulkheadRegistry = BulkheadRegistry.of(config);
        Bulkhead bulkhead = bulkheadRegistry.bulkhead("name1");
        final AtomicInteger count = new AtomicInteger(0);

        CheckedSupplier<String> decoratedSupplier = Bulkhead
                .decorateCheckedSupplier(bulkhead, () -> {
                    // 限定在concurrentCalls 的范围
                    System.out.println(count.addAndGet(1));
                    Thread.sleep(100);
                    count.decrementAndGet();
                    return Thread.currentThread().getName() + ": This can be any method which returns: 'Hello";});

        for (int i = 0; i < 20; i++) {
            try {
                new Thread(() -> {
                    try {
                        decoratedSupplier.get();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }).start();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        /*ThreadPoolBulkheadConfig threadPoolBulkheadConfig = ThreadPoolBulkheadConfig.custom()
                .maxThreadPoolSize(10)
                .coreThreadPoolSize(2)
                .queueCapacity(20)
                .build();

        // Create a BulkheadRegistry with a custom global configuration
        ThreadPoolBulkheadRegistry threadPoolBulkheadRegistry  = ThreadPoolBulkheadRegistry.of(threadPoolBulkheadConfig);
        ThreadPoolBulkhead bulkheadWithDefaultConfig = threadPoolBulkheadRegistry .bulkhead("name2");*/
    }
}
