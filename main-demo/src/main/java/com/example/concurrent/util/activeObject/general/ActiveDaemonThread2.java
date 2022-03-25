package com.example.concurrent.util.activeObject.general;

class ActiveDaemonThread2 extends Thread{
    private final ActiveMessageQueue2 messageQueue;

    public ActiveDaemonThread2(ActiveMessageQueue2 messageQueue) {
        super("ActiveDaemonThread");
        this.messageQueue = messageQueue;
        // 不会堵塞jvm main线程结束
        setDaemon(true);
    }

    @Override
    public void run() {
        for (; ; ) {
            ActiveMessage message = messageQueue.take();
            message.execute();
        }
    }
}
