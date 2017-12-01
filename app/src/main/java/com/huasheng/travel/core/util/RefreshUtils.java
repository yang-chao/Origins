package com.huasheng.travel.core.util;

import com.huasheng.travel.core.constants.Pref;

/**
 * Created by YC on 15-2-28.
 */
public class RefreshUtils {

    /**
     * 默认刷新间隔时间
     */
    public static final long REFRESH_INTERVAL_DEFAULT = 30 * 60 * 1000; // 30分钟
    /**
     * 消息提醒刷新间隔时间
     */
    public static final long REFRESH_INTERVAL_MSG_NOTIFY = 10 * 60 * 1000;

    /*******
     * refresh key
     ********/
    public static final String KEY_MSG_NOTIFY = "key_msg_notify";

    public static final String KEY_CLEAR_DATA = "key_clear_data";

    /**
     * 根据给定的 key 判断是否需要刷新
     *
     * @param key
     * @param interval
     * @return
     */
    public static boolean shouldRefresh(String key, long interval) {
        long lastTime = getTimeMillis(key);
        if (lastTime <= 0) {
            return true;
        }
        long curTime = System.currentTimeMillis();
        return ((curTime - lastTime) >= interval);
    }

    public static void markRefreshed(String key) {
        PrefHelper.putLong(Pref.NAME_REFRESH, key, System.currentTimeMillis());
    }

    public static long getTimeMillis(String key) {
        return PrefHelper.getLong(Pref.NAME_REFRESH, key, 0);
    }
}
