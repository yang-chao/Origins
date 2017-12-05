package com.assassin.origins.core.util;

import android.graphics.Bitmap;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by user on 15/11/11.
 */
public class UiUtils {
    public static Bitmap getViewSnapshot(View view) {
        if (view == null) {
            return null;
        }
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache(), 0, 0, view.getWidth(), view.getHeight());
        view.setDrawingCacheEnabled(false);
        return bmp;
    }

    public static View addSnapshotViewInViewGroup(final ViewGroup rootContent) {
        if (rootContent == null) {
            return null;
        }
        final ImageView imageView = new ImageView(rootContent.getContext());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setImageBitmap(UiUtils.getViewSnapshot(rootContent));
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //屏蔽手势
                return true;
            }
        });
        rootContent.addView(imageView);
        return imageView;
    }

}
