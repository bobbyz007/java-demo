package com.example.concurrent.async.callable;

/**
 * 基于回调接口获取异步任务的执行结果。
 * 其实java提供了更简单的方式，如Future/FutureTask
 */
public class TaskCallableApp {
    public static void main(String[] args) {
        // 负责线程结果处理
        TaskCallable<TaskResult> taskCallable = new TaskHandler();
        // 启动线程，当然也可以用线程池方式
        TaskExecutor taskExecutor = new TaskExecutor(taskCallable, "测试回调任务");
        new Thread(taskExecutor).start();
    }
}
