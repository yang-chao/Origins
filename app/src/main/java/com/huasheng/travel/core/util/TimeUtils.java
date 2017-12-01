package com.huasheng.travel.core.util;

import android.content.Context;
import android.text.TextUtils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

import com.huasheng.travel.BaseApplication;
import com.huasheng.travel.R;

/**
 * Created by YC on 14/12/20.
 */
public class TimeUtils {

    public static final SimpleDateFormat SDF_ALL = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    public static final SimpleDateFormat SDF_YEAR_MONTH_DAY = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    public static final SimpleDateFormat SDF_YEAR_MONTH_DAY_DOT = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
    public static final SimpleDateFormat SDF_MONTH_DAY = new SimpleDateFormat("MM-dd", Locale.getDefault());
    public static final SimpleDateFormat SDF_MONTH_DAY_HOUR_MINUTE = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
    public static final SimpleDateFormat SDF_MONTH_DAY_CHINESE = new SimpleDateFormat("MM月dd日", Locale.getDefault());
    public static final SimpleDateFormat SDF_YEAR_MONTH_CHINESE = new SimpleDateFormat("yyyy年MM月", Locale.getDefault());
    public static final SimpleDateFormat SDF_MONTH_DAY_DOT = new SimpleDateFormat("MM.dd", Locale.getDefault());
    public static final SimpleDateFormat SDF_HOUR_MINUTE = new SimpleDateFormat("HH:mm", Locale.getDefault());


    public static String timestampToDate(long timestamp) {
        return timestampToDate(SDF_ALL, timestamp);
    }

    public static String timestampToDate(long timestamp, SimpleDateFormat format) {
        return timestampToDate(format, timestamp);
    }

    public static String dateTransform(String dateStr, SimpleDateFormat sourceFormat, SimpleDateFormat targetFormat) {
        try {
            return targetFormat.format(sourceFormat.parse(dateStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateStr;
    }

    public static String timestampToDate(SimpleDateFormat simpleDateFormat, long timestamp) {
        return simpleDateFormat.format(new Date(timestamp));
    }

    public static Calendar getYMD(String date) {
        if (!TextUtils.isEmpty(date)) {
            try {
                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                calendar.setTime(SDF_YEAR_MONTH_DAY.parse(date));
                return calendar;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 返回 format 格式的时间字符串
     * 时间格式为 yyyy-MM-dd HH:mm:ss
     * yyyy 返回4位年份
     * MM 返回2位月份
     * dd 返回2位日
     * 时间类同
     *
     * @return 相应日期类型的字符串
     */
    public static String getCurrentDate() {
        return SDF_ALL.format(new Date());
    }


    /**
     * 获取时间差
     *
     * @param currentTimeInMs
     * @return
     */
    public static String getDateDiffer(long currentTimeInMs) {
        Date date = new Date(currentTimeInMs);
        return getDateDiffer(date);
    }

    /**
     * 获取时间差
     *
     * @param date
     * @return
     */
    public static String getDateDiffer(String date) {
        if (TextUtils.isEmpty(date)) {
            return "";
        }
        Date begin = null;
        try {
            begin = SDF_ALL.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return getDateDiffer(begin);
    }

    private static String getDateDiffer(Date begin) {
        Context context = BaseApplication.getInstance();
        if (begin == null) {
            return "";
        }
        String date = "";
        try {
            Date end = SDF_ALL.parse(getCurrentDate());
            long between = (end.getTime() - begin.getTime()) / 1000;//除以1000是为了转换成秒

            long day = between / (24 * 3600);
            long hour = between % (24 * 3600) / 3600;
            long minute = between % 3600 / 60;
            long second = between % 60 / 60;
           /* if (day >= 10) {
                date = SDF_YEAR_MONTH_DAY.format(begin);
            } else */
            if (day >= 1) {
                date = context.getString(R.string.day_ago, day);
            } else if (hour > 0 && day == 0) {
                date = context.getString(R.string.hour_ago, hour);
            } else if (minute > 0 && hour == 0 && day == 0) {
                date = context.getString(R.string.min_ago, minute);
            }/*else if(second>0 && minute==0&&hour==0 && day == 0){
                date = second + "秒前";
            }*/ else {
                date = context.getString(R.string.one_min_ago);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String getUpdate(String datetime) {
        try {
            Date date = SDF_ALL.parse(datetime);
            return SDF_MONTH_DAY.format(date) + "更新";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getUpdate(long datetime) {
        Date date = new Date(datetime);
        return SDF_MONTH_DAY.format(date) + "更新";
    }

    public static boolean isSameDay(long timeA, long timeB, SimpleDateFormat format) {
        if (format != null) {
            Date a = new Date(timeA);
            Date b = new Date(timeB);
            return a.getYear() == b.getYear() && a.getMonth() == b.getMonth()
                    && a.getDay() == b.getDay();
        }
        return false;
    }

    public static boolean isSameDay(Calendar calendarA, Calendar calendarB) {
        return calendarA != null && calendarB != null
                && calendarA.get(Calendar.YEAR) == calendarB.get(Calendar.YEAR)
                && calendarA.get(Calendar.MONTH) == calendarB.get(Calendar.MONTH)
                && calendarA.get(Calendar.DAY_OF_MONTH) == calendarB.get(Calendar.DAY_OF_MONTH);
    }

    public static String getDayOfWeekName(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        String weekName = getDayOfWeekName(calendar);
        return weekName;
    }

    public static String getDayOfWeekName(Calendar calendar) {
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
            case Calendar.MONDAY:
                return "星期一";
            case Calendar.TUESDAY:
                return "星期二";
            case Calendar.WEDNESDAY:
                return "星期三";
            case Calendar.THURSDAY:
                return "星期四";
            case Calendar.FRIDAY:
                return "星期五";
            case Calendar.SATURDAY:
                return "星期六";
            case Calendar.SUNDAY:
                return "星期日";
            default:
                return "";
        }
    }

    /**
     * 获取每天零点的时间
     *
     * @return
     */
    public static Long getStartTime() {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime().getTime();
    }

    public static String getDuration(float duration) {
        StringBuilder formatBuilder = new StringBuilder();
        Formatter formatter = new Formatter(formatBuilder, Locale.getDefault());
        int totalSeconds = (int) duration;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        formatBuilder.setLength(0);
        if (hours > 0) {
            return formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return formatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    public static String getLastCommentTime(long updatedAt) {
        long currentTime = System.currentTimeMillis();
        long time = currentTime - updatedAt;
        if (time < 60000){
            return "刚刚更新";
        }else if (time < 3600000) {
            return time / 60000 + "分钟前更新";
        } else if (time < 86400000) {
            return time / 3600000 + "小时前更新";
        } else if (time < 172800000L) {
            return time / 86400000 + "天前更新";
        } else {
            return getUpdate(updatedAt);
        }
    }

    public static String getPublishTime(long updatedAt) {
        long currentTime = System.currentTimeMillis();
        long time = currentTime - updatedAt;
        if (time < 60000){
            return "刚刚发布";
        }else if (time < 3600000) {
            return time / 60000 + "分钟前发布";
        } else if (time < 86400000) {
            return time / 3600000 + "小时前发布";
        } else if (time < 172800000L) {
            return time / 86400000 + "天前发布";
        } else {
            Date date = new Date(updatedAt);
            return SDF_MONTH_DAY.format(date) + "发布";
        }
    }
}
