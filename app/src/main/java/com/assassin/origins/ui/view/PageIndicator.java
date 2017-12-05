package com.assassin.origins.ui.view;

import android.support.v4.view.ViewPager;

/**
 * Created by user on 15/12/11.
 */
public interface PageIndicator {
    void setViewPager(ViewPager viewPager);

    void setCurrentItem(int item);

    void setOnPageChangeListener(ViewPager.OnPageChangeListener listener);

    void notifyChanged();
}
