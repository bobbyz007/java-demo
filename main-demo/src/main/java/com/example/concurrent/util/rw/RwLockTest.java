package com.example.concurrent.util.rw;

/**
 * 读写锁适用于 读明显多于写的情况。 否则写太多导致竞争太激烈，会导致读的性能急剧降低
 */
public class RwLockTest {
    public static final String TEXT = "Thisistheexampleforreadwritelock";
    public static void main(String[] args) {
        final ShareData shareData = new ShareData(50);

        // 写线程
        for (int i = 0; i < 2; i++) {
            new Thread(() -> {
                for (int index = 0; index < TEXT.length(); index++) {
                    char c = TEXT.charAt(index);
                    try {
                        shareData.write(c);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread() + " write " + c);
                }
            }).start();
        }

        // 读线程
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                while (true) {
                    try {
                        System.out.println(Thread.currentThread() + " read " + new String(shareData.read()));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
