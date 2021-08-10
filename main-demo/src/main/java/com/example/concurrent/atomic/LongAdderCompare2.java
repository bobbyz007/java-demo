package com.example.concurrent.atomic;

import com.example.concurrent.util.RealConcurrentTemplate;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * 比较LongAdder 和 AtomicLong的性能
 */
public class LongAdderCompare2 {
    private static final LongAdder adder = new LongAdder();
    // private static final AtomicLong adder = new AtomicLong();

    public static void main(String[] args) throws InterruptedException {
        // 并发线程数
        final int count = 5000;

        CyclicBarrier cyclicBarrier = new CyclicBarrier(count);
        final CountDownLatch countDownLatch = new CountDownLatch(count);
        ExecutorService executorService = Executors.newFixedThreadPool(count);

        Stopwatch stopwatch = Stopwatch.createStarted();
        for (int i = 0; i < count; i++) {
            executorService.execute(new LongAdderCompare2().new Task(i, cyclicBarrier, countDownLatch));
        }

        countDownLatch.await();

        stopwatch.stop();
        executorService.shutdown();
        System.out.println("count:" + adder.longValue() + " with time elapsed(ms): " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    public class Task implements Runnable {
        private int id;
        private CyclicBarrier cyclicBarrier;
        private CountDownLatch countDownLatch;

        public Task(int id, CyclicBarrier cyclicBarrier, CountDownLatch countDownLatch) {
            this.id = id;
            this.cyclicBarrier = cyclicBarrier;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            try {
                // 等待所有任务准备就绪
                cyclicBarrier.await();
                // 测试内容
                doRun();
            } catch (Exception e) {
                e.printStackTrace();
            }
            countDownLatch.countDown();
        }

        // 并发测试逻辑代码
        private void doRun() {
            // 每个线程执行N次累加, N取值为100,1000,10000, 50000, 100000
            final int N = 50000;
            for (int i = 0; i < N; i++) {
                adder.add(1);
                // adder.addAndGet(1);
            }
        }
    }
}
