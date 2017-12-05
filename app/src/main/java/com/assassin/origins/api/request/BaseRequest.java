package com.assassin.origins.api.request;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;

import com.assassin.origins.BuildConfig;
import com.assassin.origins.core.util.NetUtils;

/**
 * Created by YangChao on 15-12-15 上午10:26.
 */
public abstract class BaseRequest<T> extends Request<T> {

    //    private WeakReference<Response.Listener<T>> mListenerRef;
    private Response.Listener<T> mListener;
    private Map<String, String> mParams;
    private Map<String, String> mHeaders;
    private boolean mNeedVerifyToken = false;

    public BaseRequest(String url, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        this(Method.GET, url, listener, errorListener);
        setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public BaseRequest(int method, String url, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
//        mListenerRef = new WeakReference<Response.Listener<T>>(listener);
        mListener = listener;
        //volley内部根据url缓存，post情况下url一致但参数不一致，故设置post情况下不使用cache
        if (method == Method.POST) {
            setShouldCache(false);
        }
    }

    public BaseRequest putParam(String key, String value) {
        if (mParams == null) {
            mParams = new HashMap<>();
        }
        mParams.put(key, value);
        return this;
    }

    public BaseRequest putHeader(String key, String value) {
        if (mHeaders == null) {
            mHeaders = new HashMap<>();
        }
        mHeaders.put(key, value);
        return this;
    }

    public void needVerifyToken(boolean need) {
        mNeedVerifyToken = need;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        if (BuildConfig.DEBUG && mParams != null) {
            Log.d("BaseRequest", mParams.toString());
        }
        return mParams;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        if (mHeaders == null) {
            mHeaders = new HashMap<>();
        }
        mHeaders.put("User-agent", NetUtils.getUserAgent(false));
        return mHeaders;
    }

    @Override
    protected void deliverResponse(T response) {
//        if (mListenerRef != null && mListenerRef.get() != null) {
//            mListenerRef.get().onResponse(response);
//        }
        if (mListener != null) {
            mListener.onResponse(response);
        }
    }

    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError) {
        if (volleyError.networkResponse != null) {
            int statusCode = volleyError.networkResponse.statusCode;
            switch (statusCode) {
            }
        }
        return super.parseNetworkError(volleyError);
    }
}
