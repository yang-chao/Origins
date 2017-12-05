package com.assassin.origins.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by ph on 2016/11/30.
 */

public class InterceptEventLinearLayout extends LinearLayout{
    public InterceptEventLinearLayout(Context context) {
        super(context);
    }

    public InterceptEventLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InterceptEventLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }
}
