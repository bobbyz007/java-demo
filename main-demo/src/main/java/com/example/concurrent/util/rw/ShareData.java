package com.example.concurrent.util.rw;

import org.apache.commons.lang3.ThreadUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ShareData {
    // 定义共享资源
    private final List<Character> container = new ArrayList<>();

    private final ReadWriteLock readWriteLock = ReadWriteLock.readWriteLock();

    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    private final int length;

    public ShareData(int length) {
        this.length = length;

        for (int i = 0; i < length; i++) {
            container.add(i, 'c');
        }
    }

    public char[] read() throws InterruptedException {
        try {
            readLock.lock();
            char[] newBuffer = new char[length];
            for (int i = 0; i < length; i++) {
                newBuffer[i] = container.get(i);
            }

            // 模拟耗时操作
            slowly();

            return newBuffer;
        } finally {
            readLock.unlock();
        }
    }

    public void write(char c) throws InterruptedException {
        try {
            writeLock.lock();
            for (int i = 0; i < length; i++) {
                container.add(i, c);
            }

            // 模拟耗时操作
            slowly();
        } finally {
            writeLock.unlock();
        }
    }

    private void slowly() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



}
