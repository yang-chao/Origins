package com.assassin.origins.ui.common;

import android.content.Context;
import android.support.v4.view.ActionProvider;
import android.view.View;
import android.widget.TextView;

import com.assassin.origins.R;

/**
 * Created by yc on 16/1/20.
 */
public class PhotoPickerActionProvider extends ActionProvider {
    private int mCount;
    private int mMaxCount;
    private View.OnClickListener mOnActionClick;

    public PhotoPickerActionProvider(Context context, int maxCount, View.OnClickListener onActionClick) {
        super(context);
        mOnActionClick = onActionClick;
        mMaxCount = maxCount;
    }

    @Override
    public View onCreateActionView() {
        View countView = View.inflate(getContext(), R.layout.menu_photo_picker, null);

        countView.setOnClickListener(mOnActionClick);
        return countView;
    }

    public void updateCount(int count) {
        mCount = count;
    }
}
