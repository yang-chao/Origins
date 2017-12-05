package com.assassin.origins.ui.common;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

/**
 * Created by user on 14-5-27.
 */
public abstract class WaitingTask<T> extends AsyncTask<Void, Void, T> implements DialogInterface.OnDismissListener {

    private Context mContext;
    private WeakReference<Activity> mActivity;
    private ProgressDialog mProgressDialog;
    private String mMessage;
    private boolean mCancelable;

    public WaitingTask(Activity activity) {
        mActivity = new WeakReference<Activity>(activity);
        mContext = activity.getApplicationContext();
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public void setCancelable(boolean cancelable) {
        mCancelable = cancelable;
    }

    @Override
    protected abstract T doInBackground(Void... params);

    public Activity getActivity() {
        return mActivity != null ? mActivity.get() : null;
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Activity activity = getActivity();
        if (mProgressDialog == null && activity != null) {
            mProgressDialog = new ProgressDialog(activity);
            mProgressDialog.setMessage(mMessage);
            mProgressDialog.setOnDismissListener(this);
            mProgressDialog.setCancelable(mCancelable);
        }
        if (mProgressDialog != null) {
            mProgressDialog.show();
        }
    }

    @Override
    protected void onPostExecute(T result) {
        super.onPostExecute(result);
        if (mProgressDialog != null) {
            if (mProgressDialog.isShowing()) {
                try {
                    mProgressDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mProgressDialog = null;
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (mProgressDialog != null) {
            if (mProgressDialog.isShowing()) {
                try {
                    mProgressDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mProgressDialog = null;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        cancel(true);
    }
}
