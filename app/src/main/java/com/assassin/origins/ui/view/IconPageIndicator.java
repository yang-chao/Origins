package com.assassin.origins.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.assassin.origins.R;


/**
 * Created by user on 15/12/11.
 */
public class IconPageIndicator extends LinearLayout implements PageIndicator, ViewPager.OnPageChangeListener {
    protected int RES_ID_INVALID = -1;
    private ViewPager mViewPager;
    private int mIndicatorGap;
    private int mSelectedPos;
    private int mIndicatorImgResId;

    private ViewPager.OnPageChangeListener mOnPageChangeListener;

    public IconPageIndicator(Context context) {
        this(context, null);
    }

    public IconPageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.IconPageIndicator);
        mIndicatorGap = typedArray.getDimensionPixelSize(R.styleable.IconPageIndicator_indicatorGap, 0);
        mIndicatorImgResId = typedArray.getResourceId(R.styleable.IconPageIndicator_indicatorImg, RES_ID_INVALID);
        typedArray.recycle();
    }

    @Override
    public void setViewPager(ViewPager viewPager) {
        if (mViewPager == viewPager) {
            return;
        }
        mViewPager = viewPager;
        mViewPager.setOnPageChangeListener(this);
        notifyChanged();
    }

    @Override
    public void setCurrentItem(int item) {
        setItemSelected(item, true);
        if (mSelectedPos != item) {
            setItemSelected(mSelectedPos, false);
            mSelectedPos = item;
        }
    }

    private void setItemSelected(int pos, boolean selected) {
        View child = getChildAt(pos);
        if (child != null) {
            child.setSelected(selected);
        }
    }

    @Override
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mOnPageChangeListener = listener;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrollStateChanged(state);
        }
    }

    @Override
    public void onPageSelected(int position) {
        setCurrentItem(position);
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageSelected(position);
        }
    }

    @Override
    public void notifyChanged() {
        removeAllViews();
        if (mViewPager == null) {
            return;
        }
        int count = 0;
        if (mViewPager instanceof IIndicatorViewPager) {
            count = ((IIndicatorViewPager) mViewPager).getIndicatorCount();
        } else if (mViewPager.getAdapter() != null) {
            count = mViewPager.getAdapter().getCount();
        }
        for (int i = 0; i < count; i++) {
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (i > 0) {
                params.leftMargin = mIndicatorGap;
            }
            ImageView imageView = new ImageView(getContext());
            imageView.setImageResource(mIndicatorImgResId);
            addView(imageView, params);
        }
        setCurrentItem(Math.min(mSelectedPos, count - 1 < 0 ? 0 :count -1) );
        requestLayout();
    }
}
