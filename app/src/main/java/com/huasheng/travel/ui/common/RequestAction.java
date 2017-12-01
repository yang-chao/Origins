package com.huasheng.travel.ui.common;

import android.content.Context;
import android.support.v4.app.LoaderManager;
import android.support.v7.widget.RecyclerView;

import com.huasheng.travel.api.request.BaseRequest;


/**
 * 请求类型页面（Fragment 或 Activity）实现此接口，提供给具体实现类（{@link RequestImpl} 、{@link RequestListImpl}）。
 * <p>
 * Created by YangChao on 15-11-4 下午6:44.
 */
public interface RequestAction<T> {

    /**
     * 子类实现此方法返回加载数据 Request。
     * 通常用于一个页面只有一次网络请求的情况。
     *
     * @return Request
     */
    BaseRequest createRequest();

    /**
     * 通常用于分页加载数据，每次请求构造新的 Url
     *
     * @param refresh 标识刷新还是加载更多，true 刷新，false 加载更多。
     * @return Request
     */
    BaseRequest newRequest(final boolean refresh);

    /**
     * 请求网络数据
     *
     * @param refresh     true刷新，false加载更多
     * @param showLoading 是否显示 loading 视图
     */
    void requestData(boolean refresh, boolean showLoading);

    /**
     * 设置是否可以加载更多。默认规则，若服务器返回的数目大于等于请求数目的一半就认为有更多数据。
     *
     * @return
     */
    void setHasMore(int responseSize, int requestSize);

    /**
     * 设置是否可以加载更多。默认规则，若服务器返回的数目大于等于请求数目的一半就认为有更多数据。
     *
     * @return
     */
    void setHasMore(boolean hasMore);

    /**
     * 子类实现此接口提供自定义 Adapter，通常用于分页列表
     *
     * @param context
     * @return
     */
    RecyclerView.Adapter createAdapter(Context context);

    /**
     * 返回列表需要的LayoutManager
     *
     * @return
     */
    RecyclerView.LayoutManager createLayoutManager();

    /**
     * Loader 用于加载本地数据，RequestFragment 实现此方法将回调（LoaderCallbacks）提供给实现类{@link RequestImpl}
     *
     * @return
     */
    LoaderManager.LoaderCallbacks<T> getLoaderCallbacks();

    /**
     * 进入页面后是否立即加载网络数据
     *
     * @return True立即加载网络数据，False加载本地
     */
    boolean requestImmediately();

    /**
     * 延迟加载
     *
     * @return
     */
    boolean lazyLoading();

    /**
     * 返回页面刷新间隔时间,0表示立刻刷新
     *
     * @return
     */
    long getRefreshInterval();

    /**
     * 获取当前页面刷新Key，默认使用当前类名，复用的类需要重写该方法以区别不用页面的Key标识
     *
     * @return
     */
    String getRefreshKey();

    /**
     * 是否使用本地缓存的数据
     *
     * @return
     */
    boolean canLoadLocalData();


}
