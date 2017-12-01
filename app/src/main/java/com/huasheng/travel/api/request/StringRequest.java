package com.huasheng.travel.api.request;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;

import com.huasheng.travel.BuildConfig;

/**
 * Created by yc on 16/5/23.
 */

public class StringRequest extends BaseRequest<String> {

    public StringRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
    }

    public StringRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String result;
        try {
            result = new String(response.data, "UTF-8");
            if (BuildConfig.DEBUG) {
                Log.d("Request", getUrl());
                Log.d("Request", result);
            }
            return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return Response.error(new VolleyError());
    }
}
