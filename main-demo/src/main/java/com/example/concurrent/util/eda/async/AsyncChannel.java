package com.example.concurrent.util.eda.async;

import com.example.concurrent.util.eda.Channel;
import com.example.concurrent.util.eda.Event;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AsyncChannel<T extends Event> implements Channel<T> {
    private final ExecutorService executorService;

    public AsyncChannel() {
        this(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2));
    }

    public AsyncChannel(ExecutorService executorService) {
        this.executorService = executorService;
    }

    // 防止子类重写该方法
    @Override
    public void dispatch(T message) {
        executorService.submit(() -> handle(message));
    }

    protected abstract void handle(T message);

    void stop() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
