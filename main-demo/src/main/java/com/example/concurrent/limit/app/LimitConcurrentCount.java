package com.example.concurrent.limit.app;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 简单粗暴的方式限制某个接口的总并发或请求数，没有平滑处理
 */
public class LimitConcurrentCount {
    private static AtomicLong count = new AtomicLong(0);

    public static void main(String[] args) {
        for (int i = 0; i < 10; ++i) {
            final int index = i;
            new Thread(() -> {
                limit(index);
            }).start();
        }
    }

    private static void limit(int i) {
        try {
            if (count.incrementAndGet() > 5) {
                System.out.println("refused request: " + i);
                return;
            }
            System.out.println("process: " + i);
        } finally {
            count.decrementAndGet();
        }
    }
}
