package com.example.concurrent.atomic;

import com.google.common.base.Stopwatch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * 比较LongAdder 和 AtomicLong的性能，当并发量足够大时，可能存在10倍或几十倍的性能差异
 */
public class LongAdderCompare3 {
    private static final LongAdder adder = new LongAdder();
    // private static final AtomicLong adder = new AtomicLong();

    /**
     * 线程池内线程数
     */
    final static int POOL_SIZE = 1000;

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(POOL_SIZE);

        Stopwatch stopwatch = Stopwatch.createStarted();
        List<Future> futures = new ArrayList<>(POOL_SIZE);
        // 提交100倍线程池容量的任务
        for (int i = 0; i < POOL_SIZE * 100; i++) {
            futures.add(executorService.submit(new LongAdderCompare3().new Task(i)));
        }

        // 等待所有线程执行完
        for (Future future : futures) {
            try {
                future.get();
            }
            catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        stopwatch.stop();
        executorService.shutdown();
        System.out.println("count:" + adder.longValue() + " with time elapsed(ms): " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    public class Task implements Runnable {
        private int id;

        public Task(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                // 测试内容
                doRun();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 并发测试逻辑代码
        private void doRun() {
            // 每个线程执行N次累加, N取值为100,1000,10000, 50000, 100000
            final int N = 5000;
            for (int i = 0; i < N; i++) {
                adder.add(1);
                // adder.addAndGet(1);
            }
        }
    }
}
