package com.huasheng.travel.ui.common;


import android.support.v7.widget.RecyclerView;

import java.util.List;

/**
 * Created by YC on 14-12-30.
 */
public class AdapterHandler {

    public static void notifyDataSetChanged(RecyclerView.Adapter adapter, List data) {
        notifyDataSetChanged(adapter, data, true);
    }

    public static void notifyDataSetChanged(RecyclerView.Adapter adapter, List data, boolean refresh) {
        if (adapter == null || data == null) {
            return;
        }
        if (adapter instanceof ListAdapter) {
            List origData = ((ListAdapter) adapter).getData();
            if (refresh) {
                origData.clear();
            }
            origData.addAll(data);
            adapter.notifyDataSetChanged();
        }
    }

    public static void updateDataWithoutNotify(RecyclerView.Adapter adapter, List data, boolean refresh) {
        if (adapter == null || data == null) {
            return;
        }
        if (adapter instanceof ListAdapter) {
            List origData = ((ListAdapter) adapter).getData();
            if (refresh) {
                origData.clear();
            }
            origData.addAll(data);
        }
    }

    public static void clear(RecyclerView.Adapter adapter) {
        if (adapter == null) {
            return;
        }
        if (adapter instanceof ListAdapter) {
            ((ListAdapter) adapter).getData().clear();
            adapter.notifyDataSetChanged();
        }
    }
}
