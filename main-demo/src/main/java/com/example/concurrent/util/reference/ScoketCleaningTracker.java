package com.example.concurrent.util.reference;


import java.io.IOException;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.net.Socket;

/**
 * <p> 可以参考其他实现: </p>
 *
 * <p> 比如 msyql数据库驱动的 清理无用连接的 线程实现： <pre>com.mysql.cj.jdbc.AbandonedConnectionCleanupThread</pre></p>
 *
 * <p> 另外，google的guava实现： <pre>com.google.common.base.FinalizableReferenceQueue</pre></p>
 */
public class ScoketCleaningTracker {
    private static final ReferenceQueue<Object> queue = new ReferenceQueue<>();
    static {
        new Cleaner().start();
    }

    // 跟踪socket的垃圾回收
    static void track(Socket socket) {
        Tracker tracker = new Tracker(socket, queue);
        tracker.enqueue();
    }

    private static class Cleaner extends Thread {
        private Cleaner() {
            super("SocketCleaningTracker");
            setDaemon(false);
        }

        @Override
        public void run() {
            System.out.println("Start closing before being reclaimed");
            for (; ; ) {
                try {
                    Tracker tracker = (Tracker) queue.remove();
                    tracker.close();
                    System.out.println("tracker closed");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class Tracker extends PhantomReference<Object> {
        private final Socket socket;

        Tracker(Socket socket, ReferenceQueue<Object> queue) {
            // 回收对象 与 queue关联
            super(socket, queue);
            this.socket = socket;
        }

        public void close() {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

