package com.assassin.origins.api.request;

import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.Map;

import com.assassin.origins.BuildConfig;
import com.assassin.origins.api.model.Result;
import com.assassin.origins.core.util.volley.error.InvalidTokenError;
import com.assassin.origins.core.util.volley.error.MessageError;
import com.assassin.origins.core.util.volley.error.PermissionDenyError;


/**
 * Created by YC on 15-3-12.
 */
public class ResultRequest<T> extends BaseRequest<T> {

    private Response.Listener<Result<T>> mResultListener;
    private Type mType;

    public ResultRequest(int method, String url, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public ResultRequest(int method, String url, Type type, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        mType = type;
    }

    @Override
    protected final Response parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, "UTF-8");
            if (BuildConfig.DEBUG) {
                Log.d("Request", getUrl());
                Log.d("Request", json);
            }
            Result result;
            if (mType != null) {
                result = new Gson().fromJson(json, mType);
            } else {
                result = new Gson().fromJson(json, Result.class);
            }
            if (result != null) {
                if (result.isTokenInvalid()) {
//                    if (AccountModel.isLoggedOn()) {
//                        Account account = AccountModel.getAccount();
//                        if (account != null) {
//                            AccountModel.logout(account.getToken());
//                        }
//                        // 清除本地数据
//                        AccountModel.clearAccount();
//                        EventBus.getDefault().post(new LogoutEvent());
//                    }
                    return Response.error(new InvalidTokenError("Token invalid"));
                } else if (result.isSuccess()) {
                    handleResult(result, json);
                    handleResponseHeader(response.headers);
                    return Response.success(result.getData(), HttpHeaderParser.parseCacheHeaders(response));
                } else if (result.isPermissionDeny()) {
                    return Response.error(new PermissionDenyError("Permission deny, ret=" + result.getRet(), json));
                } else if (result.isMessageError()) {
                    return Response.error(new MessageError(result.getMsg()));
                } else {
                    VolleyError error = handleExtraStatus(result.getRet());
                    if (error != null) {
                        return Response.error(error);
                    } else {
                        return Response.error(new VolleyError("Result ret error, ret=" + result.getRet()));
                    }
                }
            } else {
                return Response.error(new VolleyError("Result null"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(new VolleyError(e));
        }
    }

    protected void handleResult(@NonNull Result<T> result, String json) {

    }

    protected void handleResponseHeader(Map<String, String> header) {

    }

    protected VolleyError handleExtraStatus(int statusCode) {
        return null;
    }
}
