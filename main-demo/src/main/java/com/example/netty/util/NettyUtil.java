package com.example.netty.util;

import com.google.common.base.Strings;
import io.netty.buffer.*;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.resolver.AbstractAddressResolver;
import io.netty.resolver.AddressResolver;
import io.netty.resolver.DefaultNameResolver;
import io.netty.util.*;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.internal.ObjectPool;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.TypeParameterMatcher;
import io.netty.util.internal.shaded.org.jctools.util.Pow2;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class NettyUtil {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
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

        // sizeClasses();
        // pooledBuf();
        // freePooledBuf();

        // resourceLeakTracker();

        // attributeMap();

        // eventLoopGroup();

        // nameResolver();

        // globalEventExecutor();

        // channelOutboundBuffer();

        hashedWheelTimer();

        // misc();
    }

    static void hashedWheelTimer() {
        Timer timer = new HashedWheelTimer();

        Timeout timeout1 = timer.newTimeout(timeout -> {
            System.out.println("5s 后执行该任务");
        }, 5, TimeUnit.SECONDS);

        Timeout timeout2 = timer.newTimeout(timeout -> {
            System.out.println("10s 后执行该任务");
        }, 10, TimeUnit.SECONDS);

        // 取消掉那个 5s 后执行的任务
        if (!timeout1.isExpired()) {
            timeout1.cancel();
        }

        // 原来那个 5s 后执行的任务，已经取消了。这里我们反悔了，我们要让这个任务在 3s 后执行
        // 我们说过 timeout 持有上、下层的实例，所以下面的 timer 也可以写成 timeout1.timer()
        timer.newTimeout(timeout1.task(), 3, TimeUnit.SECONDS);
    }

    /**
     * GlobalEventExecutor
     */
    static void globalEventExecutor() {

    }

    /**
     * ChannelOutboundBuffer： write的缓存
     */
    static void channelOutboundBuffer() {

    }

    /**
     * 主要演示 TypeParameterMatcher 用法： 主要是用来确定一个对象的 具体泛型类型参数的Class，比如
     *  class B extends A<String>
     *  class C extends A<Integer>
     *  则B b对象的具体类型是 String.class，C c对象的具体类型是 Integer.class
     * 一般用于父类的统一逻辑处理：判断具体子类要处理的数据 是否与 泛型类型参数匹配。 比如上例的对象b 只能处理 String类型数据，而对象c只能处理Integer类型数据
     * @throws ExecutionException
     * @throws InterruptedException
     */
    static void nameResolver() throws ExecutionException, InterruptedException {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        // 创建name resolver， 其实就是解析主机名或ip字符串 -> InetAddress
        DefaultNameResolver resolver = new DefaultNameResolver(eventLoopGroup.next());

        Future<InetAddress> future = resolver.resolve("127.0.0.1");
        System.out.println("inet address(ip): " + future.get());

        // address resolver，其实就是ip+port， ip的解析依靠name resolver， 再加上port即可创建合法的 new InetSocketAddress
        AddressResolver<InetSocketAddress> addressResolver = resolver.asAddressResolver();
        Future<InetSocketAddress> inetSocketFuture = addressResolver.resolve(InetSocketAddress.createUnresolved("127.0.0.1", 8080));
        InetSocketAddress inetSocketAddress = inetSocketFuture.get();
        System.out.println("inet socket address: " + inetSocketAddress);

        // 匹配器： 验证某个类型是否匹配某个泛型参数。
        // 比如 addressResolver的类型是 AddressResolver<InetSocketAddress> 继承-> AbstractAddressResolver<T extends SocketAddress>
        // 此时泛型参数T的实际类型是 InetSocketAddress，那么match时只能匹配InetSocketAddress或其子类型。
        // 换句话说，就是找到addressResolver对象 对应泛型参数T的实际Class类型
        TypeParameterMatcher matcher = TypeParameterMatcher.find(addressResolver, AbstractAddressResolver.class, "T");
        System.out.println("type parameter match: " + matcher.match(inetSocketAddress));

        C c = new C();
        // 找到对象c，在祖先类AC1.class下的 泛型S的具体Class类型
        TypeParameterMatcher matcher1 = TypeParameterMatcher.find(c, AC1.class, "S");
        System.out.println("type parameter match: " + matcher1.match(new N1()));
        System.out.println("type parameter match: " + matcher1.match(new N2()));

        // 对于数组类型参数，不支持T[]， 只支持 List<T>[]
        TypeParameterMatcher matcher2 = TypeParameterMatcher.find(c, AC1.class, "T");
        System.out.println("type parameter match: " + matcher2.match(new List[0]));
    }

    interface I<A extends AutoCloseable, S, T> {
    }
    static abstract class AC1<S extends Number, T> implements I<InputStream, S, T> {
    }
    // 继承类似下面会有问题：extends AC1<S, T[]> ，这样得到的T不是Class类型，netty会报异常。
    static abstract class AC2<S extends AtomicInteger, T extends Exception> extends AC1<S, List<T>[]> {
    }

    static class C extends AC2<N1, FileNotFoundException> {
    }
    static class N1 extends AtomicInteger {
    }
    static class N2 extends N1 {
    }

    static void eventLoopGroup() throws InterruptedException, ExecutionException {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        // 固定在某一个event loop中执行
        eventLoopGroup.schedule(() -> {
            System.out.println("schedule running one-shot: " + Thread.currentThread().getName());
        }, 2, TimeUnit.SECONDS);

        // 固定在一个event loop中执行
        eventLoopGroup.scheduleAtFixedRate(() -> {
            System.out.println("schedule running at fixed rate： " + Thread.currentThread().getName());
        }, 1, 1, TimeUnit.SECONDS);

        // 模拟event loop group工作10s
        Thread.sleep(10000);

        // 优雅关闭loop group
        // 默认2s安静时间（即线程继续监控队列中是否有任务处理），15s的超时时间
        eventLoopGroup.shutdownGracefully();

        // 结果为null，表示无异常
        System.out.println("terminated: " + eventLoopGroup.terminationFuture().get());
        System.out.println("terminated: " + eventLoopGroup.awaitTermination(3, TimeUnit.SECONDS));
    }

    static void misc() {
        AdaptiveRecvByteBufAllocator adaptiveRecvByteBufAllocator = new AdaptiveRecvByteBufAllocator();
        AdaptiveRecvByteBufAllocator allocator = new AdaptiveRecvByteBufAllocator(63, 127, 65536);

        RecvByteBufAllocator.ExtendedHandle handle = (RecvByteBufAllocator.ExtendedHandle)
                allocator.newHandle();
        // 主要是判断 预估读取的数据 和 上次实际读取的数据比较， 来判断是否还需要读数据：
        // 比如： 如果两者相等，表示可能还有数据，因为预估读取的数据填满了； 否则就是没有数据可读
        // handle.attemptedBytesRead();
        // handle.lastBytesRead();
        ByteBuf buf = handle.allocate(PooledByteBufAllocator.DEFAULT);
    }

    static void attributeMap() {
        AttributeMap attributeMap = new DefaultAttributeMap();
        // AttributeKey 属性键 可以共享
        attributeMap.attr(AttributeKey.valueOf("attr1"));
        attributeMap.attr(AttributeKey.newInstance("attr2"));

        // Attribute 保存了属性值
        Attribute<String> attr1 = attributeMap.attr(AttributeKey.valueOf("attr1"));
        Attribute<String> attr2 = attributeMap.attr(AttributeKey.valueOf("attr2"));
        Attribute<String> attr3 = attributeMap.attr(AttributeKey.valueOf("attr3"));
        attr3.set("value3");
        System.out.println(attr3.get());
    }

    static void resourceLeakTracker() throws InterruptedException {
        // 设置采用开关：io.netty.leakDetection.samplingInterval=1，保证每次分配都监控资源泄漏
        ByteBuf smallBuf1 = PooledByteBufAllocator.DEFAULT.buffer(700);
        ByteBuf smallBuf2 = PooledByteBufAllocator.DEFAULT.buffer(700);

        // 模拟方法退出后，不主动释放申请的ByteBuf，gc时会会放到回收队列，即检测出没有调用release方法，存在内存泄漏问题。
        smallBuf1 = null;

        // 调用release时，会调用DefaultResourceLeak的close方法，清理弱引用。因此gc时不会放到回收队列
        // smallBuf1.release();

        System.gc();

        // 等待2s，GC回收对象。在申请内存时 会调用 ResourceLeakDetector 的 track()方法时，会检测到发生内存泄漏
        Thread.sleep(2000);
        ByteBuf smallBuf3 = PooledByteBufAllocator.DEFAULT.buffer(700);
    }

    // 释放内存： 最终调用PoolArena的free方法
    // 对于small类型，基本上都会成功地 添加到线程私有的PoolThreadCache 缓存中
    // 对于normal规格，会根据maxCachedBufferCapacity大小 来确定缓存数组大小，不一定都会放到缓存中
    static void freePooledBuf() {
        // 回收时 会放到PoolThreadCache缓存中
        ByteBuf smallBuf1 = PooledByteBufAllocator.DEFAULT.buffer(700);
        ByteBuf smallBuf2 = PooledByteBufAllocator.DEFAULT.buffer(700);
        ByteBuf smallBuf3 = PooledByteBufAllocator.DEFAULT.buffer(700);
        // 一般情况下， small类型内存回收会 存储在PoolThreadCache的缓存中，不过随着分配次数的增多(超过阈值DEFAULT_CACHE_TRIM_INTERVAL)，
        // 如果缓存一直没用，则会清理掉不用的缓存。 然后重新开始计算分配次数，周而复始。
        smallBuf2.release();
        smallBuf3.release();
        ByteBuf smallBuf4 = PooledByteBufAllocator.DEFAULT.buffer(700);
        ByteBuf smallBuf5 = PooledByteBufAllocator.DEFAULT.buffer(700);

        // 此时PoolChunk的runsAvail： runOffset=3， pages=2045
        ByteBuf normalBuf1 = PooledByteBufAllocator.DEFAULT.buffer(50000);
        // 上述runSize需要7个page，此时PoolChunk的runsAvail： runOffset=10， pages=2038
        ByteBuf normalBuf2 = PooledByteBufAllocator.DEFAULT.buffer(60000);
        // 上述runSize需要8个page，此时PoolChunk的runsAvail： runOffset=18， pages=2030

        normalBuf1.release();
        // 释放后，此时PoolChunk的runsAvail有2段： runOffset=3， pages=7; runOffset=18， pages=2030

        normalBuf2.release();
        // 释放后，需要与runsAvail的前后两段合并： 组成一段： runOffset=3， pages=2045
    }

    static void pooledBuf() {
        // 按默认申请 256字节
        ByteBuf smallBuf1 = PooledByteBufAllocator.DEFAULT.buffer();
        ByteBuf smallBuf2 = PooledByteBufAllocator.DEFAULT.buffer();
        ByteBuf smallBuf3 = PooledByteBufAllocator.DEFAULT.buffer();

        ByteBuf cusSmallBuf1 = PooledByteBufAllocator.DEFAULT.buffer(700);
        // 模拟一个PoolSubpage分配不了的情况
        // runSize: 3*pageSize，每个element大小为768，sizeIdx为17， 可分配的element个数为 3*8192 / 768 = 32个
        for (int i = 0; i < 34; i++) {
            ByteBuf tmpSmallBuf = PooledByteBufAllocator.DEFAULT.buffer(700);
        }

        // 分配normal规格的内存：sizeIdx >= 39 (32KB)
        ByteBuf normalBuf1 = PooledByteBufAllocator.DEFAULT.buffer(50000);

        ByteBuf normalBuf2 = PooledByteBufAllocator.DEFAULT.buffer(14_000_000);
        // 模拟一个chunk容纳不下的情况，chunkSize默认为： 16_777_216
        // 此时当前的PoolChunkList都无法分配，会重新创建一个PoolChunk进行分配
        ByteBuf normalBuf3 = PooledByteBufAllocator.DEFAULT.buffer(14_000_000);

        // huge规格内存：大于size class类型，即申请内存大于 16M
        // huge规格内存分配不会池化，因为太大了没有意义。
        ByteBuf hugeBuf1 = PooledByteBufAllocator.DEFAULT.buffer(17_000_000);
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
