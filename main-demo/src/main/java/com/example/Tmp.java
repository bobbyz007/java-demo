package com.example;

import java.net.UnknownHostException;

/**
 * 临时测试类：随时可能删除
 */
public class Tmp {
    public static void main(String[] args) throws InterruptedException, UnknownHostException {
        //Thread.currentThread().join();
        System.out.println(Tmp.class.getName());
        System.out.println(java.net.InetAddress.getLocalHost().getCanonicalHostName());
    }
}

