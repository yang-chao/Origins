package com.assassin.origins.core.utils

import java.util.*

class DateUtils {

    companion object {
        fun getOccurrenceTime(currentTimestamp: Long): String {
            // 在主页面中设置当天时间
            val nowTime = Date()
            val currentMilliseconds = nowTime.time// 当前日期的毫秒值

            val timeDifferent = currentMilliseconds - currentTimestamp
            if (timeDifferent < 60000) {// 一分钟之内
                return "刚刚"
            }
            if (timeDifferent < 3600000) {// 一小时之内
                val longMinute = timeDifferent / 60000
                val minute = (longMinute % 100).toInt()
                return "${minute}分钟之前"
            }
            val l = (24 * 60 * 60 * 1000).toLong() // 每天的毫秒数
            if (timeDifferent < l) {// 小于一天
                val longHour = timeDifferent / 3600000
                val hour = (longHour % 100).toInt()
                return "${hour}小时之前"
            }
            if (timeDifferent >= l) {
                return "${timeDifferent % l}天前"
            }
            return "未知"
        }
    }
}