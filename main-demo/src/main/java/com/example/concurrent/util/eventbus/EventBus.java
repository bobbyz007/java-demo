package com.example.concurrent.util.eventbus;

import java.util.concurrent.Executor;

public class EventBus implements Bus {
    private final Registry registry = new Registry();

    private String busName;

    private final static String DEFAULT_BUS_NAME = "default";

    private final static String DEFAULT_TOPIC = "default-topic";

    private final Dispatcher dispatcher;

    public EventBus() {
        this(DEFAULT_BUS_NAME, null, Dispatcher.SEQ_EXECUTOR);
    }

    public EventBus(String busName) {
        this(busName, null, Dispatcher.SEQ_EXECUTOR);
    }

    public EventBus(String busName, EventExceptionHandler eventExceptionHandler, Executor executor) {
        this.busName = busName;
        this.dispatcher = Dispatcher.newDispatcher(eventExceptionHandler, executor);
    }

    public EventBus(EventExceptionHandler eventExceptionHandler) {
        this(DEFAULT_BUS_NAME, eventExceptionHandler, Dispatcher.SEQ_EXECUTOR);
    }

    @Override
    public void register(Object subscriber) {
        registry.bind(subscriber);
    }

    @Override
    public void unregister(Object subscriber) {
        registry.unbind(subscriber);
    }

    @Override
    public void post(Object event) {
        post(event, DEFAULT_TOPIC);
    }

    @Override
    public void post(Object event, String topic) {
        dispatcher.dispatch(this, registry, event, topic);
    }

    @Override
    public void close() {
        dispatcher.close();
    }

    @Override
    public String getBusName() {
        return busName;
    }
}
