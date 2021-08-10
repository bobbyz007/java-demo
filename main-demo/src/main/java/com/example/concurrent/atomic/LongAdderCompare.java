package com.example.concurrent.atomic;

import com.google.common.base.Stopwatch;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 比较LongAdder 和 AtomicLong的性能
 */
public class LongAdderCompare {
    //请求总数
    public static int clientTotal = 50000;
    //同时并发执行的线程数
    public static int threadTotal = 1000;
    private static final LongAdder adder = new LongAdder();
    // private static final AtomicLong adder = new AtomicLong();

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        final Semaphore semaphore = new Semaphore(threadTotal);
        final CountDownLatch countDownLatch = new CountDownLatch(clientTotal);

        Stopwatch stopwatch = Stopwatch.createStarted();
        for(int i = 0; i < clientTotal; i++){
            executorService.execute(() -> {
                try{
                    semaphore.acquire();
                    add();
                    semaphore.release();
                }catch (Exception e){
                    System.err.println("exception");
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();

        stopwatch.stop();
        executorService.shutdown();
        System.out.println("count:" + adder.intValue() + " with time elapsed(ms): " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }
    private static void add(){
        // 每个线程执行N次累加, N取值为100,1000,10000, 50000, 100000
        final int N = 50000;
        for (int i = 0; i < N; i++) {
            adder.add(1);
            // adder.addAndGet(1);
        }
    }
}
