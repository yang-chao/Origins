package com.assassin.origins.ui.common;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.assassin.origins.R;
import com.assassin.origins.api.request.BaseRequest;
import com.assassin.origins.core.util.NetUtils;

import static com.assassin.origins.core.util.LogUtils.LOGE;

/**
 * Created by YangChao on 15-11-5 下午4:14.
 */
public abstract class RequestFragment<T> extends BaseFragment implements RequestAction<T>,
        Response.Listener<T>, Response.ErrorListener, LoaderManager.LoaderCallbacks<T>, RequestUIInterface {

    RequestImpl<T> mBaseImpl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public LoaderManager.LoaderCallbacks<T> getLoaderCallbacks() {
        return this;
    }

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base, container, false);
        RelativeLayout rootView = (RelativeLayout) view.findViewById(R.id.root);
        RelativeLayout.LayoutParams params;

        // content
        View contentView = onCreateContentView(inflater, container, savedInstanceState);
        if (contentView != null) {
            params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            rootView.addView(contentView, params);
        }

        // loading
        View progress = inflater.inflate(R.layout.base_loading, container, false);
        params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        rootView.addView(progress, params);

        // empty
        View emptyView = inflater.inflate(R.layout.base_empty, container, false);
        params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rootView.addView(emptyView, params);

        return view;
    }

    public abstract View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBaseImpl = new RequestImpl<>(getActivity(), view, this);
        mBaseImpl.start();
    }

    /**
     * 进入页面后是否立即加载网络数据
     *
     * @return True立即加载网络数据，False加载本地
     */
    @Override
    public boolean requestImmediately() {
        return NetUtils.checkNetwork();
    }

    @Override
    public boolean lazyLoading() {
        return false;
    }

    /**
     * 请求网络数据
     *
     * @param refresh true刷新，false加载更多
     */
    protected void requestData(boolean refresh) {
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
        LOGE("onErrorResponse", error.toString());
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
    public BaseRequest newRequest(boolean refresh) {
        return null;
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
}
