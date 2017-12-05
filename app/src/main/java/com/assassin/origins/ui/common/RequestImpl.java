package com.assassin.origins.ui.common;

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.AsyncTaskLoader;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import com.assassin.origins.BaseApplication;
import com.assassin.origins.R;
import com.assassin.origins.api.request.BaseRequest;
import com.assassin.origins.core.util.NetUtils;
import com.assassin.origins.core.util.volley.VolleyManager;

/**
 * 数据加载逻辑实现类，负责处理网络和本地数据相关逻辑（请求、加载、回调），并把回调传递给controller（Fragment 或 Activity）。<br>
 * 此类和{@link RequestListImpl} 的目的是为了统一数据加载类型 Activity 和 Fragment 的实现，将它们的实现抽象出来。<br>
 * 目前只做了初步设计，有待改进。<br>
 * <p/>
 * Created by YangChao on 15-11-4 下午5:17.
 */
public class RequestImpl<T> implements RequestUIInterface {

    protected static final int LOADER_ID = 101;
    protected FragmentActivity mActivity;
    private Fragment mFragment;
    protected RequestAction mCtrlAction;

    protected int mEmptyTextResId = 0;
    protected int mEmptyIconResId = 0;
    private View emptyView;
    private ImageView emptyIcon;
    private TextView emptyText;
    private SimpleDraweeView progressBar;
    private int mLoaderId = LOADER_ID;

    public RequestImpl(FragmentActivity activity, RequestAction ctrlAction) {
        init(activity, ctrlAction);
        emptyView = activity.findViewById(android.R.id.empty);
        emptyText = (TextView) activity.findViewById(R.id.empty_text);
        emptyIcon = (ImageView) activity.findViewById(R.id.empty_icon);
        progressBar = (SimpleDraweeView) activity.findViewById(R.id.sd_progressbar);
    }

    public RequestImpl(FragmentActivity activity, final View view, RequestAction ctrlAction) {
        init(activity, ctrlAction);
        emptyView = view.findViewById(android.R.id.empty);
        emptyText = (TextView) view.findViewById(R.id.empty_text);
        emptyIcon = (ImageView) view.findViewById(R.id.empty_icon);
        progressBar = (SimpleDraweeView) view.findViewById(R.id.sd_progressbar);
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse("res:///" + R.drawable.ic_loading))
                .setAutoPlayAnimations(true)
                .build();
        progressBar.setController(controller);

        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retry();
            }
        });
    }

    protected void init(FragmentActivity activity, RequestAction ctrlAction) {
        mActivity = activity;
        mCtrlAction = ctrlAction;
    }

    public void start() {
        if (useLocalData()) {
            mActivity.getSupportLoaderManager().restartLoader(mLoaderId, null, mCtrlAction.getLoaderCallbacks());
        }
        if (mCtrlAction.requestImmediately() && !mCtrlAction.lazyLoading()) {
            requestData(true);
        } else {
            showLoading(false);
            if (!useLocalData()) { // 没有使用本地数据
                showEmpty(true);
            }
            if (!NetUtils.checkNetwork()) {
                setEmptyText(R.string.err_retry);
                setEmptyIcon(R.drawable.ic_net_err);
            }
        }
    }

    public void retry() {
        requestData(true, true);
    }

    public void setLoaderId(int loadId) {
        mLoaderId = loadId;
    }

    public void requestData(boolean refresh) {
        requestData(refresh, true);
    }

    /**
     * 请求网络数据
     *
     * @param refresh true刷新，false加载更多
     */
    public void requestData(boolean refresh, boolean showLoading) {
        if (!NetUtils.checkNetwork()) {
            Toast.makeText(BaseApplication.getInstance(), R.string.err_net, Toast.LENGTH_SHORT).show();
            return;
        }
        BaseRequest request = mCtrlAction.createRequest();
        if (request != null) {
            showLoading(showLoading);
            VolleyManager.addRequest(request);
        }
    }

    public boolean useLocalData() {
        return mActivity != null && mCtrlAction.canLoadLocalData();
    }

    public void onResponse(T response) {
        showLoading(false);
        if (response == null) {
            setEmptyText(mEmptyTextResId == 0 ? R.string.empty_data : mEmptyTextResId);
            setEmptyIcon(mEmptyIconResId == 0 ? R.drawable.ic_empty : mEmptyIconResId);
            showEmpty(true);
        } else {
            showEmpty(false);
        }
    }

    public void onErrorResponse(VolleyError error) {
        showLoading(false);
        if (!NetUtils.checkNetwork()) {
            setEmptyText(R.string.err_net);
        } else {
            setEmptyText(mEmptyTextResId == 0 ? R.string.empty_data : mEmptyTextResId);
            setEmptyIcon(mEmptyIconResId == 0 ? R.drawable.ic_empty : mEmptyIconResId);
            showEmpty(true);
        }
    }

    @Override
    public void showLoading(boolean show) {
        if (progressBar == null) {
            return;
        }
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void showEmpty(boolean show) {
        if (emptyView == null) {
            return;
        }
        emptyView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setEmptyText(int resId) {
        if (resId == 0) {
            emptyText.setVisibility(View.GONE);
        } else {
            emptyText.setText(resId);
            emptyText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setEmptyIcon(int resId) {
        if (resId == 0) {
            emptyIcon.setVisibility(View.GONE);
        } else {
            emptyIcon.setImageResource(resId);
            emptyIcon.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setEmptyTextResId(int resId) {
        mEmptyTextResId = resId;
    }

    @Override
    public void setEmptyIconResId(int resId) {
        mEmptyIconResId = resId;
    }

    /**
     * 加载本地数据
     */
    public abstract static class LocalLoader<D> extends AsyncTaskLoader<D> {

        public LocalLoader(Context context) {
            super(context);
            onContentChanged();
        }

        @Override
        protected void onStartLoading() {
            if (takeContentChanged()) {
                forceLoad();
            }
        }

        @Override
        protected void onStopLoading() {
            cancelLoad();
        }
    }
}
