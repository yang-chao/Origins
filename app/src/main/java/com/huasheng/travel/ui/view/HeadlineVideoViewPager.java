package com.huasheng.travel.ui.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by majun on 16/8/26.
 */
public class HeadlineVideoViewPager extends ViewPager {
    public HeadlineVideoViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    int mlastX = 0;
    int mLastY = 0;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = x - mlastX;
                int deltaY = y - mLastY;
                if (Math.abs(deltaY) > Math.abs(deltaX)) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        mlastX = x;
        mLastY = y;
        return super.dispatchTouchEvent(ev);
    }
}
