package com.example.concurrent.date;

import java.time.*;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * 从jdk1.8开始，引入了java.time (JSR-310). 已不推荐使用joda
 */
public class JavaTimeTest {
    public static void main(String[] args) {
        // 时区
        System.out.println("当前时区时间: "+ ZonedDateTime.now()+
                        "\n构造时区时间: "+ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("Asia/Shanghai"))+
                        "\n所有可用的时区id: "+ ZoneId.getAvailableZoneIds());

        // 时间差
        LocalTime lt1 =LocalTime.of(0, 0,0);
        LocalTime lt2 =LocalTime.of(1, 59,59);
        // LocalTime lt2 =LocalTime.of(23, 59,59);
        Duration du = Duration.between(lt1, lt2);
        System.out.println("时间差(hours): " + du.toHours()); //相差的小时数 可能是负数
        System.out.println("时间差(seconds): " + du.getSeconds());

        // 月份和星期类
        System.out.println("月份："+ Arrays.toString(Month.values())+
                "\n星期："+Arrays.toString(DayOfWeek.values())+
                "\n月份int转name: "+Month.of(3)
        );

        // 年份
        Year y= Year.now();
        System.out.println("天数：" + y.length() + "; 是否闰年: " + y.isLeap() + "; 年份值: " + y.getValue());

        // 日期类
        LocalDate ld = LocalDate.now();//等价于LocalDate.now(Clock.systemDefaultZone())
        System.out.println("日期：" + ld + "; 年：" + ld.getYear() + "; 月：" + ld.getMonthValue() + "; 月中天："+ld.getDayOfMonth() + "\n"+
                "年中天:" + ld.getDayOfYear() + "; 周中天: " + ld.getDayOfWeek() + "; 是否闰年:" + ld.isLeapYear() + "; 月份天数:" + ld.lengthOfMonth() + "\n"+
                "年天数:" + ld.lengthOfYear()
        );
        LocalDate a = LocalDate.of(2012, 7, 2);
        LocalDate b = LocalDate.of(2012, 7, 2);
        System.out.println("a在b之后吗？" + a.isAfter(b) + "\n" + "a在b之前吗？" + a.isBefore(b) + "\n" + "a和b同一天吗？" + a.isEqual(b));

        // 时间类
        LocalTime lt = LocalTime.now();
        System.out.println("当前时间到毫秒: "+ lt +"; 时间: " + lt.getHour() + ":" + lt.getMinute() + ":" + lt.getSecond() + "." + lt.getNano());

        // 日期时间类
        LocalDateTime ldt = LocalDateTime.now();
        System.out.println("当前日期时间: " + ldt + "; 年：" + ldt.getYear() + "; 月：" + ldt.getMonthValue() + "; 日：" + ldt.getDayOfMonth()
                + "; 时间: " + ldt.getHour() + ":" + ldt.getMinute() + ":" + ldt.getSecond() + "." + ldt.getNano());

        // 秒类
        Instant is=Instant.now();
        System.out.println("1970-01-01到现在的秒: " + is.getEpochSecond() + " ==毫秒=="+is.toEpochMilli());

        System.out.println(TimeUnit.SECONDS.toNanos(2) + ", " + TimeUnit.SECONDS.toNanos(-2));
    }
}
