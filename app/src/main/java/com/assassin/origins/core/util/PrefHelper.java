
package com.assassin.origins.core.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.util.Set;

import com.assassin.origins.BaseApplication;

/**
 * 本类处理SharePreference相关.
 */
public class PrefHelper {

    public static boolean getBoolean(String prefName, String prefKey,
            boolean defaultValue) {
        SharedPreferences sp = getSharedPreferences(prefName);
        return sp != null ? sp.getBoolean(prefKey, defaultValue) : defaultValue;
    }
    
    public static float getFloat(String prefName, String prefKey,
            float defaultValue) {
        SharedPreferences sp = getSharedPreferences(prefName);
        return sp != null ? sp.getFloat(prefKey, defaultValue) : defaultValue;
    }

    public static int getInt(String prefName, String prefKey, int defaultValue) {
        SharedPreferences sp = getSharedPreferences(prefName);
        return sp != null ? sp.getInt(prefKey, defaultValue) : defaultValue;
    }

    public static long getLong(String prefName, String prefKey, long defaultValue) {
        SharedPreferences sp = getSharedPreferences(prefName);
        return sp != null ? sp.getLong(prefKey, defaultValue) : defaultValue;
    }

    public static String getString(String prefName, String prefKey,
            String defaultValue) {
        SharedPreferences sp = getSharedPreferences(prefName);
        return sp != null ? sp.getString(prefKey, defaultValue) : defaultValue;
    }

    public static Set<String> getStringSet(String prefName, String prefKey,
                                   Set<String> defaultValue) {
        SharedPreferences sp = getSharedPreferences(prefName);
        return sp != null ? sp.getStringSet(prefKey, defaultValue) : defaultValue;
    }

    public static void putBoolean(String prefName, String prefKey, boolean value) {
        getSharedPreferences(prefName).edit().putBoolean(prefKey, value).apply();
    }

    public static void putFloat(String prefName, String prefKey, float value) {
        getSharedPreferences(prefName).edit().putFloat(prefKey, value).apply();
    }

    public static void putInt(String prefName, String prefKey, int value) {
        getSharedPreferences(prefName).edit().putInt(prefKey, value).apply();
    }

    public static void putLong(String prefName, String prefKey, long value) {
        getSharedPreferences(prefName).edit().putLong(prefKey, value).apply();
    }
    
    public static void putString(String prefName, String prefKey, String value) {
        getSharedPreferences(prefName).edit().putString(prefKey, value).apply();
    }

    public static void putStringSet(String prefName, String prefKey, Set<String> value) {
        getSharedPreferences(prefName).edit().putStringSet(prefKey, value).apply();
    }

    public static void remove(String prefName, String prefKey) {
        getSharedPreferences(prefName).edit().remove(prefKey).apply();
    }
    
    public static void remove(String prefName, String[] prefKeys) {
        if (prefKeys == null || prefKeys.length == 0) {
            return;
        }
        SharedPreferences sp = getSharedPreferences(prefName);
        Editor editor = sp.edit();
        for (String key : prefKeys) {
            if (!TextUtils.isEmpty(key)) {
                editor.remove(key);
            }
        }
        editor.apply();
    }

    public static boolean getBoolean(String prefKey, boolean defaultValue) {
        return getSharedPreferences().getBoolean(prefKey, defaultValue);
    }

    public static float getFloat(String prefKey, float defaultValue) {
        return getSharedPreferences().getFloat(prefKey, defaultValue);
    }

    public static int getInt(String prefKey, int defaultValue) {
        return getSharedPreferences().getInt(prefKey, defaultValue);
    }

    public static long getLong(String prefKey, long defaultValue) {
        return getSharedPreferences().getLong(prefKey, defaultValue);
    }

    public static String getString(String prefKey, String defaultValue) {
        return getSharedPreferences().getString(prefKey, defaultValue);
    }

    public static Set<String> getStringSet(String prefKey, Set<String> defaultValue) {
        return getSharedPreferences().getStringSet(prefKey, defaultValue);
    }

    public static void putBoolean(String prefKey, boolean value) {
        getSharedPreferences().edit().putBoolean(prefKey, value).apply();
    }

    public static void putFloat(String prefKey, float value) {
        getSharedPreferences().edit().putFloat(prefKey, value).apply();
    }

    public static void putInt(String prefKey, int value) {
        getSharedPreferences().edit().putInt(prefKey, value).apply();
    }

    public static void putLong(String prefKey, long value) {
        getSharedPreferences().edit().putLong(prefKey, value).apply();
    }

    public static void putString(String prefKey, String value) {
        getSharedPreferences().edit().putString(prefKey, value).apply();
    }

    public static void putStringSet(String prefKey, Set<String> value) {
        getSharedPreferences().edit().putStringSet(prefKey, value).apply();
    }

    public static void remove(String prefKey) {
        getSharedPreferences().edit().remove(prefKey).apply();
    }
    
    public static void removeKeys(String[] prefKeys) {
        if (prefKeys == null || prefKeys.length == 0) {
            return;
        }
        SharedPreferences sp = getSharedPreferences();
        Editor editor = sp.edit();
        for (String key : prefKeys) {
            if (!TextUtils.isEmpty(key)) {
                editor.remove(key);
            }
        }
        editor.apply();
    }

    /**
     * 得到默认SharePreference
     * 
     * @return
     */
    public static SharedPreferences getSharedPreferences() {
        return getSharedPreferences(null);
    }

    /**
     * 根据名字得到SharePreference
     * 
     * @param prefName
     * @return
     */
    public static SharedPreferences getSharedPreferences(String prefName) {
        if (TextUtils.isEmpty(prefName)) {
            return PreferenceManager.getDefaultSharedPreferences(BaseApplication.getInstance());
        } else {
            return BaseApplication.getInstance().getSharedPreferences(prefName, Context.MODE_PRIVATE);
        }
    }
}
