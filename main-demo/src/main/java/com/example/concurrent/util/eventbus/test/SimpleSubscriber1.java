package com.example.concurrent.util.eventbus.test;

import com.example.concurrent.util.eventbus.Subscribe;

public class SimpleSubscriber1 {
    @Subscribe
    public void method1(String event) {
        System.out.println("==SimpleSubscribe1==method1==" + event);
    }

    @Subscribe(topic = "Test")
    public void method2(String event) {
        System.out.println("==SimpleSubscribe1==method2==" + event);
    }
}
