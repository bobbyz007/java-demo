package com.example.sentinel;


import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.context.Context;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.node.DefaultNode;
import com.alibaba.csp.sentinel.slotchain.ProcessorSlotEntryCallback;
import com.alibaba.csp.sentinel.slotchain.ResourceWrapper;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.statistic.StatisticSlotCallbackRegistry;

/**
 * 连接dashboard控制台： -Dcsp.sentinel.dashboard.server=127.0.0.1:8080 -Dproject.name=sentinel-learning
 *
 */
public class SentinelDemo {
    static Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
        SentinelDemo demo = new SentinelDemo();

        // set global callback
        StatisticSlotCallbackRegistry.addEntryCallback("entry-callback", new ProcessorSlotEntryCallback<DefaultNode>() {
            @Override
            public void onPass(Context context, ResourceWrapper resourceWrapper, DefaultNode param, int count, Object... args) throws Exception {
                System.out.println("onPass: " + resourceWrapper.getName() + " with count: " + count);
            }

            @Override
            public void onBlocked(BlockException ex, Context context, ResourceWrapper resourceWrapper, DefaultNode param, int count, Object... args) {
                System.out.println("onBlocked: " + resourceWrapper.getName() + " with count: " + count);
            }
        });

        // mock invoking per 100 ms
        while (true) {
            demo.getUserInfo("SentinelDemo", 1);
            Thread.sleep(100);
        }

        // wait here.
        // wait0();
    }

    static void wait0() throws InterruptedException {
        while (true) {
            synchronized (lock) {
                System.out.println("waiting here");
                lock.wait();
            }
        }
    }

    public void getUserInfo(String application, long accountId) {
        ContextUtil.enter("user-center", application);
        Entry entry = null;
        try {
            entry = SphU.entry("getUserInfo", EntryType.IN);

            getOrderInfo(accountId);
        } catch (BlockException e) {
            throw new RuntimeException("system is busy");
        } finally {
            if (entry != null) {
                entry.exit();
            }
        }
    }

    public String getOrderInfo(long accountId) {
        Entry entry = null;
        try {
            entry = SphU.entry("getOrderInfo");

            // 例如调用order服务，此时压力传导到order服务，因此entry的类型是 EntryType.OUT

            return accountId + ": order info";
        } catch (BlockException e) {
            return null;
        } finally {
            if (entry != null) {
                entry.exit();
            }
        }
    }
}
