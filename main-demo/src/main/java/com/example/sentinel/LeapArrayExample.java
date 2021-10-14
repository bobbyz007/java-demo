package com.example.sentinel;

import com.alibaba.csp.sentinel.slots.statistic.MetricEvent;
import com.alibaba.csp.sentinel.slots.statistic.base.WindowWrap;
import com.alibaba.csp.sentinel.slots.statistic.data.MetricBucket;
import com.alibaba.csp.sentinel.slots.statistic.metric.BucketLeapArray;

import java.util.List;


public class LeapArrayExample {
    public static void main(String[] args) {
        // sampleCount： 总的窗口数
        // intervalInMs: 总的时间
        // 每个窗口时间： 总时间/总窗口数 = intervalInMs / sampleCount
        BucketLeapArray leapArray = new BucketLeapArray(60, 60 * 1000);
        long timeMillis = System.currentTimeMillis();
        WindowWrap<MetricBucket> window = leapArray.currentWindow(timeMillis);
        window.value().add(MetricEvent.PASS, 3);

        List<MetricBucket> values = leapArray.values(timeMillis);
        for (MetricBucket bucket : values) {
            System.out.println("pass: " + bucket.pass());
        }
    }
}
