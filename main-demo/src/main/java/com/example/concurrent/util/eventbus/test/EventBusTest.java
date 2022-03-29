package com.example.concurrent.util.eventbus.test;

import com.example.concurrent.util.eventbus.AsyncEventBus;
import com.example.concurrent.util.eventbus.Bus;
import com.example.concurrent.util.eventbus.EventBus;

public class EventBusTest {
    public static void main(String[] args) {
        // 也可以尝试 AsyncEventBus
        // Bus bus = new EventBus("TestBus");
        Bus bus = new AsyncEventBus("TestBus");

        bus.register(new SimpleSubscriber1());
        bus.register(new SimpleSubscriber2());

        bus.post("Hello");

        bus.post("World", "Test");
    }
}
