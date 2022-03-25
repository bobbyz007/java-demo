package com.example.concurrent.util.activeObject.general;

import java.util.LinkedList;

// 修改为 ActiveMessage
public class ActiveMessageQueue2 {
    private final LinkedList<ActiveMessage> messages = new LinkedList<>();

    public ActiveMessageQueue2() {
        new ActiveDaemonThread2(this).start();
    }

    // 允许提交无限个message，没有limit限制
    public void offer(ActiveMessage message) {
        synchronized (this) {
            messages.addLast(message);
            this.notifyAll();
        }
    }

    public ActiveMessage take() {
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
