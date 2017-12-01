package com.huasheng.travel.core.handler;

import com.huasheng.travel.api.model.Result;

/**
 * Created by YangChao on 15-11-17 下午4:49.
 */
public interface HandlerAction<T> {

    /**
     * 解析JSON 数据
     *
     * @param result
     * @throws Exception
     */
    boolean process(Result<T> result) throws Exception;

    /**
     * 持久化存储（数据库、SharedPreferences、文件）
     */
    void saveData();
}
