package com.huasheng.travel.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by liu_shuai on 16/1/11.
 */
public class MyRecyclerView extends RecyclerView{
    public MyRecyclerView(Context context) {
        super(context);
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int sizeWidth = MeasureSpec.getSize(widthSpec);
        int sizeHeight = 0;
        int count = getAdapter().getItemCount();
        LayoutManager manager = getLayoutManager();
        if (manager != null && manager instanceof GridLayoutManager) {
            int childCount = manager.getChildCount();
            int spanCount = ((GridLayoutManager)manager).getSpanCount();
            View child = manager.getChildAt(0);
            if (child != null) {
                child.measure(widthSpec, heightSpec);
                int childHeight = child.getMeasuredHeight();
                sizeHeight = childHeight * (childCount / spanCount);
            }

        }
        super.onMeasure(sizeWidth, sizeHeight);
    }
}
