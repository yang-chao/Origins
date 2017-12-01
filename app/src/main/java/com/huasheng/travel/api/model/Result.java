package com.huasheng.travel.api.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by YC on 15-1-21.
 */
public class Result<T> {

    /**
     * 成功
     */
    public static final int OK = 0;
    /**
     * 服务器内部出现异
     * 常导致请求失败
     */
    public static final int ERR_SERVER = 1;
    /**
     * ⽤户⾝份失效或者
     * 不合法，需要⽤户
     * （重新）登录
     */
    public static final int ERR_AUTH = 2;
    /**
     * 不允许⽤户发送这
     * 个请求（封禁）
     */
    public static final int ERR_PERMISSION = 3;
    /**
     * 数据不存在
     */
    public static final int ERR_NON_EXIST = 4;
    /**
     * 重复
     */
    public static final int ERR_DUPLICATED = 6;
    /**
     * bo币不足
     */
    public static final int ERR_BO_COIN_NOT_ENOUGH = 7;
    /**
     * 显示服务器返回的错误
     */
    public static final int ERR_MESSAGE = 9;


    /**
     * 成功0，失败⾮非0
     */
    private int ret;
    /**
     * 错误信息
     */
    private String msg;

    private T data;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        if (this.data != null) {
            this.data = null;
        }
        this.data = data;
    }

    public boolean isSuccess() {
        return ret == OK;
    }

    public boolean isPermissionDeny() {
        return ret == ERR_PERMISSION;
    }

    public boolean isMessageError() {
        return ret == ERR_MESSAGE;
    }

    public boolean isTokenInvalid() {
        return ret == ERR_AUTH;
    }

    public static String getString(String json, String key) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject data = jsonObject.optJSONObject("data");
            if (data != null) {
                return data.optString(key);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int getString(JSONObject jsonObject, String key) {
        JSONObject data = jsonObject.optJSONObject("data");
        if (data != null) {
            return data.optInt(key);
        }
        return -1;
    }

    public static int getInt(JSONObject jsonObject, String key) {
        JSONObject data = jsonObject.optJSONObject("data");
        if (data != null) {
            return data.optInt(key);
        }
        return -1;
    }

    public static boolean getBoolean(JSONObject jsonObject, String key) {
        JSONObject data = jsonObject.optJSONObject("data");
        if (data != null) {
            return data.optBoolean(key, false);
        }
        return false;
    }
}
