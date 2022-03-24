package com.example.concurrent.util.reference;

import java.io.IOException;
import java.net.Socket;

/**
 * 演示资源的关闭。
 * 如果暂时关闭不了，则利用PhantomReference 在垃圾回收时再次尝试进行关闭。
 */
public class Resource {
    // 或文件流等资源
    private Socket socket;

    // 释放资源
    private void release() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            if (socket != null) {
                // 垃圾回收时再次尝试进行关闭
                ScoketCleaningTracker.track(socket);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Socket socket = new Socket();
        ScoketCleaningTracker.track(socket);
    }
}
