package com.example.netty.async;

import io.netty.util.concurrent.*;

/**
 * 演示用法：
 * <p>{@link io.netty.util.concurrent.Promise},
 * <p>{@link io.netty.util.concurrent.Future},
 * <p>{@link io.netty.channel.ChannelFuture},
 * <p>{@link io.netty.channel.ChannelPromise}
 */
public class AsyncExample {
    public static void main(String[] args) {
        // 构造线程池
        EventExecutor executor = new DefaultEventExecutor();

        // 创建promise实例
        Promise<Integer> promise = new DefaultPromise(executor);

        // 添加两个listener
        promise.addListener(future -> {
            if (future.isSuccess()) {
                System.out.println("任务结束，结果：" + future.get());
            } else {
                System.out.println("任务失败，异常：" + future.cause());
            }
        }).addListener(future -> System.out.println("任务结束，balabala..."));

        // 提交任务到线程池，五秒后执行结束，设置执行 promise 的结果
        executor.submit(() -> {
            try {
                Thread.sleep(5000);
                System.out.println("线程执行......");
                System.out.println(1/0);
                // 注意：如果代码中存在catch不了的异常，会导致promise无法设置成功或失败结果，导致promise.sync一直在等待。
                // 建议，无论是否能捕获异常，都需要设置promise
            } catch (InterruptedException | ArithmeticException e) {
                e.printStackTrace();
                promise.setFailure(e);
            }

            // 设置 promise 的结果
            // promise.setFailure(new RuntimeException("Failed deliberately."));
            promise.setSuccess(123456);
        });

        // main线程堵塞等待执行结果
        try {
            promise.sync();
            // promise.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executor.shutdownGracefully();
        }
    }
}
