package com.huasheng.travel.core.util;

import android.content.Loader;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Picture;
import android.os.Process;
import android.view.View;
import android.webkit.WebView;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by ph on 2016/11/28.
 */

public class BitmapUtil {

    public static Bitmap loadBitmapFromWebView(WebView webView) {
        if (webView == null) {
            return null;
        }
        webView.setPictureListener(new WebView.PictureListener() {
            @Override
            public void onNewPicture(WebView view, Picture picture) {
                LogUtils.LOGE("TAG","onNewPicture");
            }
        });
        Picture snapShot = webView.capturePicture();
        Bitmap bmp = Bitmap.createBitmap(snapShot.getWidth(), snapShot.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        snapShot.draw(canvas);
        return bmp;
    }

    public static Bitmap loadBitmapFromViewByWidth(int width ,View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public static Bitmap mergeBitmap(Bitmap bitmap1, Bitmap bitmap2) {
        Bitmap bitmap3 = Bitmap.createBitmap(bitmap1.getWidth(), bitmap1.getHeight() + bitmap2.getHeight(), bitmap1.getConfig());
        Canvas canvas = new Canvas(bitmap3);
        canvas.drawBitmap(bitmap1, new Matrix(), null);
        canvas.drawBitmap(bitmap2, 0, bitmap1.getHeight(), null);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return bitmap3;
    }
}
