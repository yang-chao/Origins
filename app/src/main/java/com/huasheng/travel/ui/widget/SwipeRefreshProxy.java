package com.huasheng.travel.ui.widget;

import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.security.InvalidParameterException;

import com.huasheng.travel.R;

/**
 * Created by yc on 16/1/15.
 */
public class SwipeRefreshProxy {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SuperSwipeRefreshLayout mSuperSwipeRefreshLayout;

    // SuperSwipeRefreshLayout Header View
    private ProgressBar progressBar;
    private TextView textView;
    private ImageView imageView;

    public SwipeRefreshProxy(View refreshLayout) {
        if (refreshLayout instanceof SwipeRefreshLayout) {
            mSwipeRefreshLayout = (SwipeRefreshLayout) refreshLayout;
        } else if (refreshLayout instanceof SuperSwipeRefreshLayout) {
            mSuperSwipeRefreshLayout = (SuperSwipeRefreshLayout) refreshLayout;
            mSuperSwipeRefreshLayout.useFooter(false);
            mSuperSwipeRefreshLayout.setHeaderView(createHeaderView());
            mSuperSwipeRefreshLayout.setTargetScrollWithLayout(true);
        } else {
            throw new InvalidParameterException("refreshLayout should be either SwipeRefreshLayout or SuperSwipeRefreshLayout");
        }
    }

    public void setRefreshing(boolean refreshing) {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(refreshing);
        } else if (mSuperSwipeRefreshLayout != null) {
            mSuperSwipeRefreshLayout.setRefreshing(refreshing);
        }
    }

    public void setOnRefreshListener(Object listener) {
        if (mSwipeRefreshLayout != null && listener instanceof SwipeRefreshLayout.OnRefreshListener) {
            mSwipeRefreshLayout.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener) listener);
        } else if (mSuperSwipeRefreshLayout != null && listener instanceof SuperSwipeRefreshLayout.OnPullRefreshListener) {
            mSuperSwipeRefreshLayout.setOnPullRefreshListener((SuperSwipeRefreshLayout.OnPullRefreshListener) listener);
        }
    }

    private View createHeaderView() {
        View headerView = LayoutInflater.from(mSuperSwipeRefreshLayout.getContext())
                .inflate(R.layout.widget_superrefreshlayout_head, null);
        progressBar = (ProgressBar) headerView.findViewById(R.id.pb_view);
        textView = (TextView) headerView.findViewById(R.id.text_view);
//        textView.setText(R.string.match_list_history_pull);
        imageView = (ImageView) headerView.findViewById(R.id.image_view);
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageResource(R.drawable.ic_refresh_down);
        progressBar.setVisibility(View.GONE);
        return headerView;
    }

    public void onRefreshAction() {
        if (mSuperSwipeRefreshLayout == null) {
            return;
        }
//        textView.setText(R.string.match_list_history_refreshing);
        imageView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                mSuperSwipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
            }
        }, 2000);
    }

    public void onPullEnableAction(boolean enable) {
        if (mSuperSwipeRefreshLayout == null) {
            return;
        }
//        textView.setText(enable ? R.string.match_list_history_loose : R.string.match_list_history_pull);
        imageView.setVisibility(View.VISIBLE);
//        imageView.animate().rotation(enable ? 180 : 0);
        imageView.setRotation(enable ? 180 : 0);
    }
}
