package com.assassin.origins.core.util.volley.error;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;

/**
 * Created by yc on 16/8/20.
 */
public class MessageError extends VolleyError {

    private String errMessage;

    public MessageError(String errMessage) {
        this.errMessage = errMessage;
    }

    public MessageError(NetworkResponse response, String errMessage) {
        super(response);
        this.errMessage = errMessage;
    }

    public MessageError(String exceptionMessage, String errMessage) {
        super(exceptionMessage);
        this.errMessage = errMessage;
    }

    public MessageError(String exceptionMessage, Throwable reason, String errMessage) {
        super(exceptionMessage, reason);
        this.errMessage = errMessage;
    }

    public MessageError(Throwable cause, String errMessage) {
        super(cause);
        this.errMessage = errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }
}
