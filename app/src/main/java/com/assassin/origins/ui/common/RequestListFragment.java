package com.assassin.origins.ui.common;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.assassin.origins.R;
import com.assassin.origins.api.request.BaseRequest;
import com.assassin.origins.core.event.ToolbarEvent;
import com.assassin.origins.core.util.RefreshUtils;
import com.assassin.origins.ui.widget.WrapContentLinearLayoutManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by YangChao on 15-11-5 下午4:14.
 */
public abstract class RequestListFragment<T> extends RequestFragment<T> {
    private static final String TAG = "RequestListFragment";

    RequestListImpl mListImpl;

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mBaseImpl = new RequestListImpl<>(getActivity(), view, this);
        mListImpl = (RequestListImpl) mBaseImpl;
        mListImpl.initView();
        mListImpl.setLoaderId(getLoaderId());
        mListImpl.start();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mListImpl != null) {
            mListImpl.release();
        }
    }

    /**
     * 是否响应顶部或者底部tab双击事件
     *
     * @return
     */
    protected boolean shouldRespondToolbarEvent() {
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ToolbarEvent event) {
        if (isVisible() && shouldRespondToolbarEvent()) {
            mListImpl.getRecyclerView().scrollToPosition(0);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public boolean requestImmediately() {
        return super.requestImmediately()
                && RefreshUtils.shouldRefresh(getRefreshKey(), getRefreshInterval());
    }

    /**
     * 返回页面刷新间隔时间,0表示立刻刷新
     *
     * @return
     */
    public long getRefreshInterval() {
        return 0;
    }

    /**
     * 加载本地数据的Loader Id
     *
     * @return
     */
    public int getLoaderId() {
        return 0;
    }

    /**
     * 获取当前页面刷新Key，默认使用当前类名，复用的类需要重写该方法以区别不用页面的Key标识
     *
     * @return
     */
    @Override
    public String getRefreshKey() {
        return this.getClass().getName();
    }

    protected int getPageIndex() {
        return mListImpl.getPageIndex();
    }

    protected void setPageIndex(int pageIndex) {
        mListImpl.setPageIndex(pageIndex);
    }

    protected RecyclerView getRecyclerView() {
        return mListImpl.getRecyclerView();
    }

    public void setRefreshListener(RequestListImpl.RefreshListener listener) {
        if (mListImpl != null) {
            mListImpl.setRefreshListener(listener);
        }
    }

    /**
     * 对于列表数据使用
     *
     * @return
     */
    @Override
    public final BaseRequest<T> createRequest() {
        return null;
    }

    @Override
    public void setHasMore(int responseSize, int requestSize) {
        mListImpl.setHasMore(responseSize, requestSize);
    }

    @Override
    public void setHasMore(boolean hasMore) {
        mListImpl.setHasMore(hasMore);
    }

    protected void markRequestFinished() {
        mListImpl.markRequestFinished();
    }

    protected void setRefreshing(boolean refreshing) {
        mListImpl.setRefreshing(refreshing);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public RecyclerView.Adapter getAdapter() {
        if (mListImpl == null) {
            return null;
        }
        return mListImpl.getAdapter();
    }

    @Override
    public RecyclerView.LayoutManager createLayoutManager() {
        WrapContentLinearLayoutManager layoutManager = new WrapContentLinearLayoutManager(getActivity());
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        return layoutManager;
    }

    @Override
    public void onResponse(T response) {
        super.onResponse(response);
        // 列表刷新后记录刷新时间
        RefreshUtils.markRefreshed(getRefreshKey());
    }

    @Override
    public void onLoadFinished(Loader<T> loader, T data) {
        super.onLoadFinished(loader, data);
        if (data != null) {
            mBaseImpl.showEmpty(false);
            mListImpl.mRefreshProxy.setRefreshing(false);
        }
    }

    @Override
    public void onLoaderReset(Loader<T> loader) {
        RecyclerView recyclerView = mListImpl.getRecyclerView();
        if (recyclerView != null) {
            recyclerView.setAdapter(null);
        }
    }
}
