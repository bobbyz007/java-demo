package com.example.netty.util;

import io.netty.channel.ChannelId;
import io.netty.channel.DefaultChannelId;
import io.netty.util.internal.ObjectPool;

public class NettyUtil {
    public static void main(String[] args) throws InterruptedException {
        // channelId();

        recycle();
    }

    /**
     * 生成channel id唯一的方式
     */
    static void channelId() {
        ChannelId id = DefaultChannelId.newInstance();

        /**
         * long text格式如下： [machine id]-[process id]-[sequence id]-[timestamp]-[random]
         * 调用 {@link io.netty.buffer.ByteBufUtil#hexDump(byte[], int, int)}字节码转换成十六进制表示，类似：
         * 84fdd1fffe28c490-0000130c-00000000-0202cbe1115aa556-95d236c4
         */
        System.out.println("channel id long text: " + id.asLongText());

        /**
         * short text格式如下：[random]
         * 字节码转换成十六进制表示，类似：
         * 95d236c4
         */
        System.out.println("channel id short text: " + id.asShortText());
    }

    /**
     * 对象的回收与重新利用
     */
    static void recycle() throws InterruptedException {
        Entry entry1 = Entry.newInstance("entry1");
        Entry entry2 = Entry.newInstance("entry1");
        System.out.println("entry1 == entry2: " + (entry1 == entry2)); // print false

        // 回收entry1，则重新申请时entry3应该指向原来的entry1
        // entry1.recycle();
        // entry2.recycle();
        // 异步回收
        Thread recycThread = new Thread(() -> {
            entry1.recycle();
            entry2.recycle();
        });
        recycThread.start();
        recycThread.join();
        Entry entry3 = Entry.newInstance("entry3");
        System.out.println("entry1 == entry3: " + (entry1 == entry3)); // print true
    }

    static class Entry {
        String msg;
        private final ObjectPool.Handle<Entry> handle;

        private static final ObjectPool<Entry> RECYCLER = ObjectPool.newPool(new ObjectPool.ObjectCreator<Entry>() {
            @Override
            public Entry newObject(ObjectPool.Handle<Entry> handle) {
                return new Entry(handle);
            }
        });

        private Entry(ObjectPool.Handle<Entry> handle) {
            this.handle = handle;
        }

        static Entry newInstance(String msg) {
            Entry entry = RECYCLER.get();
            entry.msg = msg;
            return entry;
        }

        void recycle() {
            handle.recycle(this);
        }
    }
}
