package com.assassin.origins.core.utils;

import java.util.Calendar;

public class DateUtil {
    public static String getOccurrenceTime(long timestamp) {
        timestamp = timestamp * 1000;
        long currentMilliseconds = Calendar.getInstance().getTimeInMillis();
        long timeDifferent = currentMilliseconds - timestamp;
        if (timeDifferent < 60000) {// 一分钟之内
            return "刚刚";
        }
        if (timeDifferent < 3600000) {// 一小时之内
            long longMinute = timeDifferent / 60000;
            int minute = (int) (longMinute % 100);
            return minute + "分钟之前";
        }
        long l = 24 * 60 * 60 * 1000; // 每天的毫秒数
        if (timeDifferent < l) {// 小于一天
            long longHour = timeDifferent / 3600000;
            int hour = (int) (longHour % 100);
            return hour + "小时之前";
        } else {
            return timeDifferent % l + "天前";
        }
    }
}
