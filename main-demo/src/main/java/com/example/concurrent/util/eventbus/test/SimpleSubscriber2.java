package com.example.concurrent.util.eventbus.test;

import com.example.concurrent.util.eventbus.Subscribe;

public class SimpleSubscriber2 {
    @Subscribe
    public void method1(String event) {
        System.out.println("==SimpleSubscribe2==method1==" + event);
    }

    @Subscribe(topic = "Test")
    public void method2(String event) {
        System.out.println("==SimpleSubscribe2==method2==" + event);
    }
}
