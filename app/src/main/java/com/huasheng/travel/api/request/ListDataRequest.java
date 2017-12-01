package com.huasheng.travel.api.request;

import android.support.annotation.NonNull;

import com.android.volley.Response;
import com.huasheng.travel.api.model.Result;
import com.huasheng.travel.core.handler.JSONHandler;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;



/**
 * Created by YC on 14-1-14.
 */
public class ListDataRequest<T> extends ResultRequest<List<T>> {

    private static final String TAG = ListDataRequest.class.getSimpleName();
    private JSONHandler<List<T>> mJSONHandler;
    private boolean mPersistence = true;

    public ListDataRequest(int method, String url, JSONHandler<List<T>> jsonHandler, boolean needPersistent, Type type,
                           Response.Listener<List<T>> listener, Response.ErrorListener errorListener) {
        super(method, url, type, listener, errorListener);
        mJSONHandler = jsonHandler;
        mPersistence = needPersistent;
        if (mJSONHandler != null) {
            mJSONHandler.setData(new ArrayList<T>());
        }
    }

    @Override
    protected void handleResult(@NonNull Result<List<T>> result, String json) {
        if (mJSONHandler != null) {
            try {
                mJSONHandler.process(result);
                if (mPersistence) {
                    mJSONHandler.saveData();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
