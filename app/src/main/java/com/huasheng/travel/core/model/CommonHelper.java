package com.huasheng.travel.core.model;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.Toast;

import com.huasheng.travel.BaseApplication;


/**
 * Created by user on 15/6/3.
 */
public class CommonHelper {

    public static void showToast(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        Toast.makeText(BaseApplication.getInstance(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(int resId) {
        if (resId == 0) {
            return;
        }
        showToast(resId, Toast.LENGTH_SHORT);
    }

    public static void showToast(int resId, int duration) {
        Toast.makeText(BaseApplication.getInstance(), BaseApplication.getInstance().getString(resId), duration).show();
    }

    public static void showToast(String msg, int duration) {
        Toast.makeText(BaseApplication.getInstance(), msg, duration).show();
    }

    public static String getString(int resId) {
        return BaseApplication.getInstance().getString(resId);
    }

    public static int getColor(int resId) {
        return BaseApplication.getInstance().getResources().getColor(resId);
    }

    public static String getStringFormat(int resId, Object... args) {
        return String.format(BaseApplication.getInstance().getString(resId), args);
    }

    public static Drawable getDrawable(int resId) {
        return BaseApplication.getInstance().getResources().getDrawable(resId);
    }

    public static int getDimensionPixelSize(int dimensId) {
        return BaseApplication.getInstance().getResources().getDimensionPixelSize(dimensId);
    }

    public static ProgressDialog showLoadingDialog(Context context, int msgId) {
        return showLoadingDialog(context, getString(msgId));
    }

    public static ProgressDialog showLoadingDialog(Context context, String msg) {
        ProgressDialog dialog = ProgressDialog.show(context, "", msg, true, true);
        return dialog;
    }

    public static void dismissDialog(AlertDialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

}
