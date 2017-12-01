package com.huasheng.travel.core.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.huasheng.travel.BaseApplication;

/**
 * Created by YangChao on 15-11-16 下午6:02.
 */
public class ObjectStorage {

    public static final String SHAREDPREFERENCES_NAME = "object_storage";

    private static SharedPreferences getInstance() {
        return BaseApplication.getInstance().getSharedPreferences(SHAREDPREFERENCES_NAME,
                Context.MODE_PRIVATE);
    }

    public static void put(String key, Object value) {
        SharedPreferences sp = getInstance();
        String json = new Gson().toJson(value);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, json);
        editor.apply();
    }

    public static <T> T get(String key, Class<T> clazz) {
        SharedPreferences sp = getInstance();
        String json = sp.getString(key, "");
        if (!TextUtils.isEmpty(json)) {
            return new Gson().fromJson(json, clazz);
        }
        return null;
    }

    public static <D, T> void putMap(String key, Map<D, T> value) {
        SharedPreferences sp = getInstance();
        String json = new Gson().toJson(value);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, json);
        editor.apply();
    }

    public static <D, T> Map<D, T> getMap(String key, Type type) {
        SharedPreferences sp = getInstance();
        String json = sp.getString(key, "");
        if (!TextUtils.isEmpty(json)) {
            return new Gson().fromJson(json, type);
        }
        return Collections.emptyMap();
    }

    public static <T> void putList(String key, List<T> value) {
        SharedPreferences sp = getInstance();
        String json = new Gson().toJson(value);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, json);
        editor.apply();
    }

    public static <T> List<T> getList(String key, Class<T[]> clazz) {
        SharedPreferences sp = getInstance();
        String json = sp.getString(key, "");
        if (!TextUtils.isEmpty(json)) {
            T[] objects = new Gson().fromJson(json, clazz);
            if (objects != null) {
                return new ArrayList<>(Arrays.asList(objects));
            }
        }
        return new ArrayList<>();
    }
}
