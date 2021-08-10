package com.example.concurrent.util;

import com.google.common.base.Strings;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 一般情况下， 基于for循坏提交到线程池执行，并不接近于真实的并发场景。
 *
 * 一种优化是：提交到线程池中，基于CyclicBarrier等所有线程准备好后一起启动执行，而不是串行化地提交一个执行一个。
 */
public class RealConcurrentTemplate {
    public static void main(String[] args) {
        // 并发线程数
        final int count = 1000;

        CyclicBarrier cyclicBarrier = new CyclicBarrier(count);
        ExecutorService executorService = Executors.newFixedThreadPool(count);
        for (int i = 0; i < count; i++) {
            executorService.execute(new RealConcurrentTemplate().new Task(i, cyclicBarrier));
        }

        executorService.shutdown();
        while (!executorService.isTerminated()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public class Task implements Runnable {
        private int id;
        private CyclicBarrier cyclicBarrier;

        public Task(int id, CyclicBarrier cyclicBarrier) {
            this.id = id;
            this.cyclicBarrier = cyclicBarrier;
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
        }

        // 并发测试逻辑代码
        private void doRun() {
            System.out.println(Strings.lenientFormat("thread %s is running", id));
        }
    }
}
