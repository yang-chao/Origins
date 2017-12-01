package com.huasheng.travel.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by gl on 2016/8/6.
 *
 * 为LinearLayout添加marginTop属性
 */
public class MarginTopLinearLayout extends LinearLayout {

    public MarginTopLinearLayout(Context context) {
        super(context);
    }

    public MarginTopLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarginTopLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int getMarginTop() {
        MarginLayoutParams lp = (MarginLayoutParams) getLayoutParams();
        return lp.topMargin;
    }

    /**
     * 反射调用，勿删
     * @param marginTop
     */
    public void setMarginTop(int marginTop) {
        MarginLayoutParams lp = (MarginLayoutParams) getLayoutParams();
        lp.setMargins(0,marginTop, 0, 0);
        setLayoutParams(lp);
    }

}
