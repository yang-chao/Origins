package com.assassin.origins.core.util.volley;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.lang.ref.WeakReference;

/**
 * Created by user on 15/7/29.
 */
public class SafeErrorListener implements Response.ErrorListener {
    private WeakReference<Response.ErrorListener> mErrorListenerWeakReference;

    public SafeErrorListener(Response.ErrorListener listener) {
        mErrorListenerWeakReference = new WeakReference<Response.ErrorListener>(listener);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if (mErrorListenerWeakReference != null && mErrorListenerWeakReference.get() != null) {
            mErrorListenerWeakReference.get().onErrorResponse(error);
        }
    }
}
