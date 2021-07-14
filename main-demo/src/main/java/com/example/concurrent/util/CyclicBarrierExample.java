package com.example.concurrent.util;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CyclicBarrierExample {
    static CyclicBarrier cyclicBarrier = new CyclicBarrier(5, () -> {
        System.out.println("callback is running.");
    });

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 10; i++){
            final int threadNum = i;
            Thread.sleep(1000);
            executorService.execute(() -> {
                try {
                    race(threadNum);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        executorService.shutdown();
    }

    private static void race(int threadNum) throws Exception{
        Thread.sleep(1000);
        System.out.println(threadNum + " is ready");
        cyclicBarrier.await();
        System.out.println(threadNum + " continue");
    }
}
