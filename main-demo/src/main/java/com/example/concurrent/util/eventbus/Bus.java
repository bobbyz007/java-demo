package com.example.concurrent.util.eventbus;

public interface Bus {
    void register(Object subscriber);

    void unregister(Object subscriber);

    void post(Object event);

    void post(Object event, String topic);

    void close();

    String getBusName();
}
