package com.assassin.origins.ui.widget;

import android.os.Handler;
import android.view.MotionEvent;

import com.assassin.origins.ui.view.IAutoScrollViewPager;


/**
 * Created by user on 15/12/14.
 */
public class ViewPagerAutoScrollHelper {
    private IAutoScrollViewPager mPager;
    private Handler mHandler;
    private int mInterval;
    private Runnable mAutoRunnable = new Runnable() {
        @Override
        public void run() {
            mPager.toNextPage();
            mHandler.postDelayed(this, mInterval);
        }
    };

    public ViewPagerAutoScrollHelper(IAutoScrollViewPager viewPager, int autoInterval) {
        mPager = viewPager;
        mInterval = autoInterval;
        mHandler = new Handler();
    }

    public void startAutoScroll() {
        stopAutoScroll();
        mHandler.postDelayed(mAutoRunnable, mInterval);
    }

    public void stopAutoScroll() {
        mHandler.removeCallbacks(mAutoRunnable);
    }

    public void dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                stopAutoScroll();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                startAutoScroll();
                break;
        }
    }
}
