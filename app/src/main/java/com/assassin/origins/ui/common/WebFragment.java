package com.assassin.origins.ui.common;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.assassin.origins.R;
import com.assassin.origins.core.util.WebViewConfig;
import com.assassin.origins.ui.widget.ObservableWebView;

/**
 * Created by yc on 16/1/13.
 */
public class WebFragment extends BaseFragment {
    private static final String TAG = "WebFragment";
    private WebImpl mWebImpl;
    public ObservableWebView mWebView;
    private boolean mUseCache = true;

    private boolean mUpdateTitleAfterLoading = true;

    public void disableCache() {
        mUseCache = false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey("updateTitle")) {
            mUpdateTitleAfterLoading = savedInstanceState.getBoolean("updateTitle", true);
        }
        return inflater.inflate(R.layout.activity_web, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mWebView = (ObservableWebView) view.findViewById(R.id.webview);
        mWebView.setBackgroundColor(getResources().getColor(R.color.colorBaseWhite));
//        mWebView.addJavascriptInterface(new ArticleJSInterface(getActivity(), mWebView), ArticleJSInterface.JS_OBJECT);
        String url = getArguments() != null ? getArguments().getString(WebImpl.PARAM_URL) : null;
        WebViewConfig.config(mWebView, mUseCache);
        mWebImpl = new WebImpl((AppCompatActivity) getActivity(), view,
                getActivity().findViewById(R.id.fullscreen_container), url);
        mWebImpl.setUpdateTitleAfterLoading(mUpdateTitleAfterLoading);
        if (mWebImpl.checkNetWork()) {
            if (!TextUtils.isEmpty(url)) {
                mWebImpl.loadUrl(url);
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey("updateTitle")) {
            mUpdateTitleAfterLoading = savedInstanceState.getBoolean("updateTitle", true);
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey("updateTitle")) {
            mUpdateTitleAfterLoading = savedInstanceState.getBoolean("updateTitle", true);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("updateTitle", mUpdateTitleAfterLoading);
    }


    public void setUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Bundle args = new Bundle();
        args.putString(WebImpl.PARAM_URL, url);
        setArguments(args);
    }

    public void loadUrl(String url) {
        mWebImpl.loadUrl(url);
    }

    public void loadJSMethod(String jsMethod) {
        mWebImpl.loadUrl(jsMethod, false);
    }

    public WebView getWebView() {
        return mWebImpl.getWebView();
    }

    public void setUpdateTitleAfterLoading(boolean update) {
        mUpdateTitleAfterLoading = update;
    }

    public void invokeJsMethod(String method) {
        Log.i(TAG, "invokeJsMethod:" + method + ";");
        loadJSMethod("javascript:" + method + "()");
    }

    public void invokeJsMethod(String method, String param) {
        Log.i(TAG, "invokeJsMethod:" + method + ";" + param);
        loadJSMethod("javascript:" + method + "('" + param + "')");
    }

    public void invokeJsMethod(String method, String[] params) {
        if (params == null) {
            return;
        }
        Log.i(TAG, "invokeJsMethod:" + method + ";" + params);
        StringBuilder paramBuilder = new StringBuilder();
        for (String param : params) {
            paramBuilder.append("'").append(param).append("',");
        }
        paramBuilder.deleteCharAt(paramBuilder.length() - 1);
        loadJSMethod("javascript:" + method + "(" + paramBuilder.toString() + ")");
    }

    @Override
    public void onResume() {
        super.onResume();
        mWebImpl.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mWebImpl.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mWebImpl != null) {
            mWebImpl.onDestroy();
        }
    }

    protected void hideLoading() {
        mWebImpl.hideLoading();
    }

    public void scrollTop() {
        mWebView.scrollTo(0, 0);
    }

    private int mPosition;

    public void setFragmentPosition(int position) {
        this.mPosition = position;
    }
}
