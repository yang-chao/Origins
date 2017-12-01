package com.huasheng.travel.core.util.volley.error;

import com.android.volley.VolleyError;

/**
 * Created by yc on 16/1/29.
 */
public class PermissionDenyError extends VolleyError {

    private String mResponseJSON;

    public PermissionDenyError(String exceptionMessage) {
        super(exceptionMessage);
    }

    public PermissionDenyError(String exceptionMessage, String responseJSON) {
        super(exceptionMessage);
        this.mResponseJSON = responseJSON;
    }

    public String getResponseJSON() {
        return mResponseJSON;
    }
}
