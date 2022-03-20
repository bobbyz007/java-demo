package com.example.concurrent.thread.state.observe;

import java.util.concurrent.TimeUnit;

/**
 * 演示监听任务的生命周期
 */
public class Test {
    public static void main(String[] args) {
        // 任务状态监听者
        final LifecycleObserver<String> observer = new EmptyLifecycleObserver<>() {
            @Override
            public void onFinish(Thread thread, String result) {
                System.out.println("The result is: " + result);
            }
        };

        // 封装执行的task 以及 监听者
        Observable observableThread = new ObservableThread<>(() -> {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("finished done");
            return "Hello Observer";
        }, observer);

        observableThread.start();
    }
}
