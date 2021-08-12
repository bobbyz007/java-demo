package com.example.netty.util;

import com.google.common.base.Strings;
import io.netty.buffer.*;
import io.netty.channel.ChannelId;
import io.netty.channel.DefaultChannelId;
import io.netty.util.ByteProcessor;
import io.netty.util.internal.ObjectPool;
import io.netty.util.internal.shaded.org.jctools.util.Pow2;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class NettyUtil {
    public static void main(String[] args) throws InterruptedException {
        // channelId();

        // recycle();

        // heapBuffer();
        // directBuffer();
        // compositeBuffer();

        // byteIndex();
        // bufView();
        // bufCopy();

        // referenceCounted();
        // alignment();

        sizeClasses();
    }

    // 理解SizeClasses原理
    static void sizeClasses() {
        PooledByteBufAllocatorMetric allocatorMetric = PooledByteBufAllocator.DEFAULT.metric();
        final int SIZE_IDX_MAX = 75;
        final int PAGE_IDX_MAX = 39;
        final int SMALL_SIZE_IDX_MAX = 38;
        if (allocatorMetric.numDirectArenas() > 0) {
            // any pool arena
            PoolArenaMetric arenaMetric = allocatorMetric.directArenas().get(0);

            System.out.println("--------------------------------------------------------------------------");
            // sizeidx -> size
            for (int i = 0; i <= SIZE_IDX_MAX; i++) {
                int size = arenaMetric.sizeIdx2size(i);
                int sizeCompute = arenaMetric.sizeIdx2sizeCompute(i);
                System.out.println(Strings.lenientFormat("sizeIdx: %s, size: %s, sizeCompute: %s, equal: %s",
                        i, size, sizeCompute, size == sizeCompute));
            }

            System.out.println("--------------------------------------------------------------------------");
            // pageidx -> size
            for (int i = 0; i <= PAGE_IDX_MAX; i++) {
                long size = arenaMetric.pageIdx2size(i);
                long sizeCompute = arenaMetric.pageIdx2sizeCompute(i);
                System.out.println(Strings.lenientFormat("pageIdx: %s, size: %s, sizeCompute: %s (KB), equal: %s",
                        i, size, sizeCompute / 8 / 1024, size == sizeCompute));
            }

            System.out.println("--------------------------------------------------------------------------");
            // size -> sizeIdx
            // 大于size的最小sizeIdx，即申请到的大小要大于 请求的大小。
            for (int size : new int[]{8, 640, 642}) {
                System.out.println(Strings.lenientFormat("size: %s -> sizeIdx: %s", size, arenaMetric.size2SizeIdx(size)));;
            }

            System.out.println("--------------------------------------------------------------------------");
            // page -> pageIdx
            // 此处的pages参数是申请的页面个数，每个页面大小默认为1KB。默认支持2048个page大小申请，即最大2M，这也是normal支持申请的最大内存。
            for (int pages : new int[]{1, 2, 5, 320, 321, 2048, 2049, 2050}) {
                System.out.println(Strings.lenientFormat("pages: %s -> pageIdx: %s, pageIdxFloor: %s",
                        pages, arenaMetric.pages2pageIdx(pages), arenaMetric.pages2pageIdxFloor(pages)));;
            }

            // 即把size规格化为size classes表格中的size大小。 比如8规划化到最小的16，640本身已经是size classes中的大小，直接返回；
            // 642则规格化为大于它的 768
            System.out.println("--------------------------------------------------------------------------");
            for (int size : new int[]{8, 640, 642}) {
                System.out.println(Strings.lenientFormat("size: %s -> normalizedSize: %s", size, arenaMetric.normalizeSize(size)));;
            }
        }
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

    static void heapBuffer() {
        ByteBuf buf = Unpooled.buffer();

        // another way to create buffer
        // ByteBuf buf2 = ByteBufAllocator.DEFAULT.buffer();

        if (buf.hasArray()) {
            byte[] array = buf.array();
            int offset = buf.arrayOffset() + buf.readerIndex();
            int length = buf.readableBytes();

            System.out.println(Strings.lenientFormat("offset: %s, length: %s, readerIndex: %s, writIndex: %s",
                    offset, length, buf.readerIndex(), buf.writerIndex()));
            System.out.println(Strings.lenientFormat("capacity: %s, maxCapacity: %s",
                    buf.capacity(), buf.maxCapacity()));
        }
    }

    static void directBuffer() {
        // 默认是 big-endian模式
        ByteBuf directBuf = Unpooled.directBuffer();
        directBuf.writeInt(17);
        if (!directBuf.hasArray()) {
            int length = directBuf.readableBytes();
            byte[] array = new byte[length];

            // 读取缓存数据到数组中
            directBuf.getBytes(directBuf.readerIndex(), array);

            System.out.println("get first int: " + directBuf.getInt(0));
            System.out.println("hex dump: 0x" + ByteBufUtil.hexDump(array));
        }
    }

    static void compositeBuffer() {
        CompositeByteBuf messageBuf = Unpooled.compositeBuffer();
        ByteBuf headBuff = Unpooled.buffer();
        ByteBuf bodyBuf = Unpooled.directBuffer();

        messageBuf.addComponents(headBuff, bodyBuf);
        messageBuf.removeComponent(0);

        // CompositeByteBuf实现了Iterable接口
        for (ByteBuf buf : messageBuf) {
            // 打印ByteBuf相关元数据信息
            System.out.println(buf.toString());
        }
    }

    static void byteIndex() {
        ByteBuf buffer = Unpooled.buffer(); //get reference form somewhere
        // big endian，字节码为： 0X 00 00 00 11
        buffer.writeInt(17);

        // 使用indexOf()方法来查找
        int index1 = buffer.indexOf(buffer.readerIndex(), buffer.writerIndex(), (byte) 17);

        // 使用ByteProcessor查找给定的值
        int index2 = buffer.forEachByte(new ByteProcessor.IndexOfProcessor((byte) 17));

        System.out.println(Strings.lenientFormat("index1: %s, index2: %s", index1, index2));
    }

    static void bufView() {
        Charset utf8 = StandardCharsets.UTF_8;
        ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!", utf8);
        ByteBuf sliced = buf.slice(0, 15);
        System.out.println("sliced: " + sliced.toString(utf8));

        buf.setByte(0, (byte)'J');
        System.out.println("same?: " + (buf.getByte(0) == sliced.getByte(0))); // return true
    }

    static void bufCopy() {
        Charset utf8 = StandardCharsets.UTF_8;
        ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!", utf8);
        ByteBuf copy = buf.copy(0, 15);
        System.out.println("copied: " + copy.toString(utf8));

        buf.setByte(0, (byte)'J');
        System.out.println("different?: " + (buf.getByte(0) != copy.getByte(0))); // return true
    }

    static void referenceCounted() {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        System.out.println("refCnt initialized: " + buf.refCnt());

        buf.retain();

        System.out.println("refCnt retained: " + buf.refCnt());

        buf.release();

        System.out.println("refCnt released: " + buf.refCnt());
    }

    static void alignment() {
        // 求出最小的 (n * alignment) >= value，其中 n是大于等于1的整数
        System.out.println(Pow2.align(33, 32));
    }
}
