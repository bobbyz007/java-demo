package com.example.concurrent.util.activeObject;

class ActiveDaemonThread extends Thread{
    private final ActiveMessageQueue messageQueue;

    public ActiveDaemonThread(ActiveMessageQueue messageQueue) {
        super("ActiveDaemonThread");
        this.messageQueue = messageQueue;
        // 不会堵塞jvm main线程结束
        setDaemon(true);
    }

    @Override
    public void run() {
        for (; ; ) {
            MethodMessage message = messageQueue.take();
            message.execute();
        }
    }
}
