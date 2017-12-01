package com.huasheng.travel.ui.view;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * 无限循环viewpager
 */
public class CyclicViewPager extends ViewPager {
    private InnerPagerAdapter mCyclicPagerAdapter;
    //是否能循环滑动（当内容只有一页时，一般取消循环滑动功能）
    private boolean mCycle = true;

    public CyclicViewPager(Context context) {
        this(context, null);
    }

    public CyclicViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setCycle(boolean cycle) {
        mCycle = cycle;
    }


    private void init() {
        setOnPageChangeListener(null);
    }

    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        super.setOnPageChangeListener(new InnerOnPageChangeListener(listener));
    }


    @Override
    public void setAdapter(PagerAdapter adapter) {
        mCyclicPagerAdapter = new InnerPagerAdapter(adapter);
        super.setAdapter(mCyclicPagerAdapter);
        if (mCycle) {
            setCurrentItem(1, false);
        }
    }


    public int getNormalCount() {
        return mCyclicPagerAdapter.getNormalCount();
    }

    private int getNormalPos(int position) {
        return mCyclicPagerAdapter.getNormalPos(position);
    }

    public void setAdjustedCurrentItem(int pos) {
        if (pos >= mCyclicPagerAdapter.getCount() - 1) {
            setCurrentItem(1, false);
        } else if (pos == 0) {
            setCurrentItem(mCyclicPagerAdapter.getCount() - 2, false);
        } else {
            setCurrentItem(pos);
        }
    }

    private class InnerOnPageChangeListener implements OnPageChangeListener {
        private OnPageChangeListener mListener;

        public InnerOnPageChangeListener(OnPageChangeListener listener) {
            mListener = listener;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (mListener != null) {
                mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (mListener != null) {
                mListener.onPageSelected(getNormalPos(position));
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (mListener != null) {
                mListener.onPageScrollStateChanged(state);
            }
            if (mCycle) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    int curPos = getCurrentItem();
                    setAdjustedCurrentItem(curPos);
                }
            }
        }
    }

    private class InnerPagerAdapter extends PagerAdapter {
        private PagerAdapter mAdapter;

        public InnerPagerAdapter(PagerAdapter adapter) {
            mAdapter = adapter;
            mAdapter.registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    notifyChanged();
                }

                @Override
                public void onInvalidated() {
                    notifyChanged();
                }
            });
        }

        private void notifyChanged() {
            setAdjustedCurrentItem(getCurrentItem());
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return getNormalCount() + (mCycle ? 2 : 0);
        }

        public int getNormalCount() {
            return mAdapter.getCount();
        }

        public int getNormalPos(int position) {
            if (mCycle) {
                if (position == 0) {
                    position = getNormalCount() - 1;
                } else if (position > getNormalCount()) {
                    position = 0;
                } else {
                    position -= 1;
                }
            }
            return position;
        }

        @Override
        public int getItemPosition(Object object) {
            return mAdapter.getItemPosition(object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return mAdapter.isViewFromObject(view, object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (mCycle) {
                position = getNormalPos(position);
            }
            return mAdapter.instantiateItem(container, position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            mAdapter.destroyItem(container, position, object);
        }
    }

}
