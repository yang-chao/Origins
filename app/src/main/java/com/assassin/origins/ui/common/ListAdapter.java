package com.assassin.origins.ui.common;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YC on 15-1-9.
 */
public abstract class ListAdapter<T> extends RecyclerView.Adapter {

    public List<T> mData = new ArrayList<>();
    protected Context mContext;

    public ListAdapter(Context context) {
        this.mContext = context;
    }

    public List<T> getData() {
        return mData;
    }

    public T getItem(int position) {
        if (position < 0 || position >= mData.size()) {
            return null;
        }
        return mData.get(position);
    }

    public void updateData(List<T> data, boolean refresh) {
        if (data != null) {
            if (refresh) {
                mData.clear();
            }
            mData.addAll(data);
            notifyDataSetChanged();
        }
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }
}
