package com.example.concurrent.util.activeObject;

import java.util.LinkedList;

public class ActiveMessageQueue {
    private final LinkedList<MethodMessage> messages = new LinkedList<>();

    public ActiveMessageQueue() {
        new ActiveDaemonThread(this).start();
    }

    // 允许提交无限个message，没有limit限制
    public void offer(MethodMessage message) {
        synchronized (this) {
            messages.addLast(message);
            this.notifyAll();
        }
    }

    public MethodMessage take() {
        synchronized (this) {
            while (messages.isEmpty()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return messages.removeFirst();
        }
    }
}
