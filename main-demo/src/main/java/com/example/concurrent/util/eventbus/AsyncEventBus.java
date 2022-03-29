package com.example.concurrent.util.eventbus;

import java.util.concurrent.Executor;

public class AsyncEventBus extends EventBus{
    private final static String DEFAULT_BUS_NAME = "default-async";

    public AsyncEventBus() {
        this(DEFAULT_BUS_NAME, null, Dispatcher.POOL_EXECUTOR);
    }

    public AsyncEventBus(String busName) {
        this(busName, null, Dispatcher.POOL_EXECUTOR);
    }

    public AsyncEventBus(String busName, EventExceptionHandler eventExceptionHandler, Executor executor) {
        super(busName, eventExceptionHandler, executor);
    }

    public AsyncEventBus(EventExceptionHandler eventExceptionHandler) {
        this(DEFAULT_BUS_NAME, eventExceptionHandler, Dispatcher.POOL_EXECUTOR);
    }
}
