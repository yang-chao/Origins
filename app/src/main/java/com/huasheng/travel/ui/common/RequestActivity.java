package com.huasheng.travel.ui.common;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.huasheng.travel.R;
import com.huasheng.travel.api.request.BaseRequest;

/**
 * Created by YangChao on 15-11-4 下午3:30.
 */
public abstract class RequestActivity<T> extends BaseActivity implements RequestAction<T>,
        Response.Listener<T>, Response.ErrorListener, LoaderManager.LoaderCallbacks<T>, RequestUIInterface {

    private RequestImpl<T> mBaseImpl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getLayoutInflater();
        RelativeLayout rootView = (RelativeLayout) inflater.inflate(R.layout.fragment_base, null);
        RelativeLayout.LayoutParams params;
        // content
        View contentView = onCreateContentView(inflater, rootView);
        if (contentView != null) {
            params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            rootView.addView(contentView, params);
        }

        // loading
        ContentLoadingProgressBar progress = (ContentLoadingProgressBar) inflater.inflate(R.layout.base_progressbar, null);
        params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        progress.hide();
        rootView.addView(progress, params);

        // empty
        View emptyView = inflater.inflate(R.layout.base_empty, null);
        params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rootView.addView(emptyView, params);

        setContentView(rootView);
    }

    public abstract View onCreateContentView(LayoutInflater inflater, ViewGroup container);

    protected void startRequest() {
        mBaseImpl = new RequestImpl<T>(this, this);
        mBaseImpl.start();
    }

    /**
     * 请求网络数据
     *
     * @param refresh true刷新，false加载更多
     */
    public void requestData(boolean refresh) {
        mBaseImpl.requestData(refresh, true);
    }

    @Override
    public void requestData(boolean refresh, boolean showLoading) {
        mBaseImpl.requestData(refresh, showLoading);
    }

    @Override
    public void onResponse(T response) {
        mBaseImpl.onResponse(response);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        mBaseImpl.onErrorResponse(error);
    }

    @Override
    public Loader<T> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<T> loader, T data) {
        mBaseImpl.showLoading(false);
    }

    @Override
    public void onLoaderReset(Loader<T> loader) {

    }

    @Override
    public long getRefreshInterval() {
        return 0;
    }

    @Override
    public String getRefreshKey() {
        return getClass().getName();
    }

    @Override
    public boolean requestImmediately() {
        return true;
    }

    @Override
    public boolean lazyLoading() {
        return false;
    }

    @Override
    public RecyclerView.Adapter createAdapter(Context context) {
        return null;
    }

    @Override
    public RecyclerView.LayoutManager createLayoutManager() {
        return null;
    }

    @Override
    public LoaderManager.LoaderCallbacks<T> getLoaderCallbacks() {
        return null;
    }

    @Override
    public BaseRequest newRequest(boolean refresh) {
        return null;
    }

    @Override
    public void setHasMore(int responseSize, int requestSize) {

    }

    @Override
    public void setHasMore(boolean hasMore) {

    }

    @Override
    public boolean canLoadLocalData() {
        return false;
    }

    @Override
    public void showLoading(boolean show) {
        mBaseImpl.showLoading(show);
    }

    @Override
    public void showEmpty(boolean show) {
        mBaseImpl.showEmpty(show);
    }

    @Override
    public void setEmptyText(int resId) {
        mBaseImpl.setEmptyText(resId);
    }

    @Override
    public void setEmptyIcon(int resId) {
        mBaseImpl.setEmptyIcon(resId);
    }

    @Override
    public void setEmptyTextResId(int resId) {
        mBaseImpl.setEmptyTextResId(resId);
    }

    @Override
    public void setEmptyIconResId(int resId) {
        mBaseImpl.setEmptyIconResId(resId);
    }
}
