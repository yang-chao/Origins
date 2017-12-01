package com.huasheng.travel.ui.common;

import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import java.security.InvalidParameterException;
import java.util.HashSet;
import java.util.List;

import com.huasheng.travel.R;
import com.huasheng.travel.core.constants.Config;
import com.huasheng.travel.core.model.CommonHelper;
import com.huasheng.travel.ui.widget.SuperSwipeRefreshLayout;
import com.huasheng.travel.ui.widget.SwipeRefreshProxy;
import com.huasheng.travel.core.util.NetUtils;
import com.huasheng.travel.core.util.volley.VolleyManager;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

/**
 * 继承自{@link RequestImpl}，扩展了分页列表需要的业务逻辑，如滚动加载更多等。
 * <p/>
 * Created by YangChao on 15-11-11 下午3:10.
 */
public class RequestListImpl<T> extends RequestImpl<T> implements SwipeRefreshLayout.OnRefreshListener,
        SuperSwipeRefreshLayout.OnPullRefreshListener {

    protected boolean mHasMore = true;

    SwipeRefreshProxy mRefreshProxy;
    private RecyclerView mRecyclerView;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private int mPageIndex = Config.DEFAULT_PAGE_START;

    private boolean mRequestInQueueFinished = true;

    /**
     * 用过过滤翻页时产生的重复url，目前翻页都采用GET请求，可从地址直接判断；
     * 若采用POST请求会导致url一样，参数不一样，需要特别处理
     */
    private HashSet<String> mRequestUrls = new HashSet<>();

    private RecyclerView.OnScrollListener mOnListScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == SCROLL_STATE_IDLE && mHasMore) {
                int lastVisiblePos = -1;
                if (mLayoutManager instanceof LinearLayoutManager) {
                    lastVisiblePos = ((LinearLayoutManager) mLayoutManager).findLastCompletelyVisibleItemPosition();
                }
                int totalCount = mAdapter.getItemCount();
                if (lastVisiblePos == (totalCount - 1)) {
                    requestData(false, false);
                }
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

        }
    };

    public RequestListImpl(FragmentActivity activity, RequestAction requestAction) {
        super(activity, requestAction);
        mRecyclerView = (RecyclerView) activity.findViewById(android.R.id.list);
        mRefreshProxy = new SwipeRefreshProxy(activity.findViewById(R.id.refresh_layout));
    }

    public RequestListImpl(FragmentActivity activity, View view, RequestAction requestAction) {
        super(activity, view, requestAction);
        mRecyclerView = (RecyclerView) view.findViewById(android.R.id.list);
        mRefreshProxy = new SwipeRefreshProxy(view.findViewById(R.id.refresh_layout));
    }

    public int getPageIndex() {
        return mPageIndex;
    }

    public void setPageIndex(int pageIndex) {
        mPageIndex = pageIndex;
    }

    public void initView() {
        mLayoutManager = mCtrlAction.createLayoutManager();
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = mCtrlAction.createAdapter(mActivity);
        if (mAdapter == null) {
            throw new InvalidParameterException("Adapter must not be null");
        }
        mRefreshProxy.setOnRefreshListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(mOnListScrollListener);
    }

    @Override
    public void onResponse(T response) {
        mRequestInQueueFinished = true;
        super.onResponse(response);
        if (response instanceof List) {
            onRequestResponse((List) response);
        } else if (response != null) {
            mRefreshProxy.setRefreshing(false);
        }
    }

    protected void onRequestResponse(List data) {
        mRefreshProxy.setRefreshing(false);
        showLoading(false);

        if (data == null || data.isEmpty()) {
            setEmptyText(mEmptyTextResId == 0 ? R.string.empty_data : mEmptyTextResId);
            setEmptyIcon(mEmptyIconResId == 0 ? R.drawable.ic_net_err : mEmptyIconResId);
            if (getAdapter() instanceof HeaderFooterRecyclerAdapter) {
                if (!((HeaderFooterRecyclerAdapter) getAdapter()).useHeader()
                        && ((HeaderFooterRecyclerAdapter) getAdapter()).getBasicItemCount() == 0) {
                    showEmpty(true);
                }
            } else {
                if (getAdapter().getItemCount() == 0) {
                    showEmpty(true);
                }
            }
        } else {
            showEmpty(false);
        }
    }

    void setRefreshing(boolean refreshing) {
        mRefreshProxy.setRefreshing(refreshing);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        mRequestInQueueFinished = true;
        super.onErrorResponse(error);
        // 加载失败，回滚PageIndex
        if (mPageIndex > Config.DEFAULT_PAGE_START) {
            mPageIndex--;
        }
        mRefreshProxy.setRefreshing(false);
        if (getAdapter() instanceof HeaderFooterRecyclerAdapter) {
            if (((HeaderFooterRecyclerAdapter) getAdapter()).getBasicItemCount() == 0) {
                showEmpty(true);
            } else {
                showEmpty(false);
            }
        } else {
            if (getAdapter().getItemCount() == 0) {
                showEmpty(true);
            } else {
                showEmpty(false);
            }
        }
    }

    void markRequestFinished() {
        mRequestInQueueFinished = true;
    }

    public void setHasMore(int responseSize, int requestSize) {
        setHasMore(responseSize >= (requestSize / 2));
    }

    public void setHasMore(boolean hasMore) {
        this.mHasMore = hasMore;
        if (!mHasMore && mAdapter instanceof PageAdapter) {
            ((PageAdapter) mAdapter).showNoMore(true);
        }
    }

    @Override
    public void requestData(boolean refresh) {
        requestData(refresh, true);
    }

    @Override
    public void requestData(boolean refresh, boolean showRefreshing) {
        if (!NetUtils.checkNetwork()) {
            mRefreshProxy.setRefreshing(false);
            CommonHelper.showToast(R.string.err_net);
            return;
        }
        showEmpty(false);
        if (refresh) {
            mPageIndex = Config.DEFAULT_PAGE_START;
            mHasMore = true;
            mRequestUrls.clear();
        } else {
            mPageIndex++;
        }
        Request request = mCtrlAction.newRequest(refresh);
        if (request != null) {
            if (showRefreshing) {
                mRefreshProxy.setRefreshing(true);
                showLoading(false); // 对于列表刷新，目前只保留下拉刷新显示，隐藏中间的loading
            }
            if (mRequestInQueueFinished) {
                if (request.getMethod() == Request.Method.GET) {
                    if (mRequestUrls.add(request.getUrl())) { // GET请求需要根据URL过滤短时间内的相同请求
                        mRequestInQueueFinished = false;
                        VolleyManager.addRequest(request);
                    }
                } else {
                    mRequestInQueueFinished = false;
                    VolleyManager.addRequest(request);
                }
            } else if (mPageIndex > Config.DEFAULT_PAGE_START) {
                mPageIndex--;
            }
        } else {
            mRefreshProxy.setRefreshing(false);
        }
    }

    public RecyclerView.Adapter getAdapter() {
        return mAdapter;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Override
    public void onRefresh() {
        mRefreshProxy.onRefreshAction();
        if (mRefreshListener != null) {
            mRefreshListener.onRefresh();
        }
        requestData(true);
    }

    @Override
    public void onPullDistance(int distance) {

    }

    @Override
    public void onPullEnable(boolean enable) {
        mRefreshProxy.onPullEnableAction(enable);
    }

    public void release() {

    }

    private RefreshListener mRefreshListener;

    public void setRefreshListener(RefreshListener listener) {
        mRefreshListener = listener;
    }
    public interface RefreshListener {
        void onRefresh();
    }
}
