package com.example.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

public class TimeCalc {
    public static void main(String[] args) throws IOException {
        List<String> times = FileUtils.readLines(new File("C:\\Users\\Justin\\Desktop\\time.txt"), "utf-8");
        LocalTime sixClock = LocalTime.parse("06:00");
        LocalTime twoClock = LocalTime.parse("02:00");
        double count = 0;
        for (String time : times) {
            if (StringUtils.isBlank(time)) {
                continue;
            }
            String[] parts = time.split("\\s+");
            time = parts[1];

            LocalTime curTime = LocalTime.parse(time);
            if (curTime.isBefore(sixClock)) {
                count -= 1;
            } else {
                double mins = (double) Duration.between(twoClock, curTime).toMinutes();
                double hours = mins / 60;
                count += (hours - 5);
            }
        }

        System.out.println("left time: " + count);
    }
}
