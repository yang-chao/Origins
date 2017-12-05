/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.assassin.origins.core.handler;

import android.content.Context;

import com.facebook.common.internal.Sets;

import java.util.HashSet;

public abstract class JSONObjectHandler<T> implements HandlerAction<T> {

    protected Context mContext;
    private T mData;
    private boolean mRefresh = true;
    /**
     * 用作本地数据去重
     */
    protected HashSet<Long> mIdSet = new HashSet<>();

    public JSONObjectHandler(Context context) {
        mContext = context.getApplicationContext();
    }

    public boolean isRefresh() {
        return mRefresh;
    }

    public void setRefresh(boolean refresh) {
        this.mRefresh = refresh;
    }

    public T getData() {
        return mData;
    }

    public void setData(T data) {
        mData = data;
    }
}
