package com.assassin.origins.ui.common;

import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.HashMap;
import java.util.Map;

import com.assassin.origins.BaseApplication;
import com.assassin.origins.BuildConfig;
import com.assassin.origins.R;
import com.assassin.origins.core.model.CommonHelper;
import com.assassin.origins.core.util.NetUtils;
import com.assassin.origins.core.util.SysUtils;
import com.assassin.origins.ui.widget.WebChromeClientEx;
import com.assassin.origins.ui.widget.WebViewClientEx;

import static com.assassin.origins.core.util.LogUtils.LOGD;

/**
 * Created by yc on 16/1/13.
 */
public class WebImpl implements RequestUIInterface {
    private static final String TAG = "WebImpl";
    public static final String PARAM_URL = "param_url";
    public static final String PARAM_TITLE = "param_title";
    public static final String PARAM_UPDATE_TITLE_AFTER_LOADING = "param_update_title_after_loading";
    public static final String PARAM_REPLACE_TARGET = "param_replace_target";
    public static final String PARAM_REPLACE_REPLACEMENT = "param_replace_replacement";
    public static final String PARAM_HIDE_ACTION_BAR = "param_hide_action_bar";
    public static final String PARAM_WILL_BACK_ACTION = "param_will_back_action";
    public static final String PARAM_NEWS = "param_news";
    public static final String PARAM_CHECK_LOGIN = "param_check_login";
    private WebView mWebView;
    private View progressZone;
    private View emptyView;
    private ImageView emptyIcon;
    private TextView emptyRetry;
    private String mUrl;

    private boolean mUpdateTitleAfterLoading = true;

    private AppCompatActivity mActivity;
    private FrameLayout mFullscreenContainer;

    private boolean mVideoFullScreen = false;
    private View mFullScreenView = null;
    private WebChromeClient.CustomViewCallback mCustomCallback = null;

    private String mTarget;
    private String mReplacement;

    public WebImpl(AppCompatActivity activity, View view, View fullscreenContainer, String url) {
        mUrl = url;
        mActivity = activity;
        mWebView = (WebView) view.findViewById(R.id.webview);
        emptyView = view.findViewById(android.R.id.empty);
        emptyRetry = (TextView) view.findViewById(R.id.empty_text);
        emptyIcon = (ImageView) view.findViewById(R.id.empty_icon);
        progressZone = view.findViewById(R.id.progress_zone);
        SimpleDraweeView progressbar = (SimpleDraweeView) view.findViewById(R.id.web_progressbar);
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse("res:///" + R.drawable.ic_loading))
                .setAutoPlayAnimations(true)
                .build();
        progressbar.setController(controller);
        if (fullscreenContainer instanceof FrameLayout) {
            mFullscreenContainer = (FrameLayout) fullscreenContainer;
        }
        if (mFullscreenContainer == null) {
            mFullscreenContainer = (FrameLayout) view.findViewById(R.id.fullscreen_container);
        }
        initWebView(mWebView);

        // 设置listener
        view.findViewById(android.R.id.empty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetUtils.checkNetwork()) {
                    CommonHelper.showToast(R.string.err_net);
                    return;
                }
                if (mWebView != null) {
                    showEmpty(false);
                    loadUrl(mUrl);
                }
            }
        });
    }

    boolean checkNetWork() {
        if (!NetUtils.checkNetwork()) {
            setEmptyText(R.string.err_retry);
//            setEmptyIcon(R.drawable.ic_net_err);
            showEmpty(true);
            return false;
        }
        return true;
    }

    /**
     * 设置webview 参数
     *
     * @param webView
     */
    public void initWebView(WebView webView) {
        if (webView == null) {
            return;
        }
        // 调试WebView
        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (0 != (BaseApplication.getInstance().getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE)) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDatabaseEnabled(false);
        webView.getSettings().setSaveFormData(true);
        webView.getSettings().setSavePassword(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setTextZoom(100); // 限制正文字体大小为100%，防止修改系统字体后造成的正文布局错乱
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        //无网时取本地缓存
        if (!NetUtils.checkNetwork()) { // loading offline
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        } else {
            webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT); // load online by default
        }
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setGeolocationEnabled(true);
        webView.getSettings().setGeolocationDatabasePath(webView.getContext().getCacheDir().toString());
        webView.getSettings().setUserAgentString(NetUtils.getUserAgent(true));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        WebChromeClient webChromeClient = new WebChromeClientEx(webView) {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (mUpdateTitleAfterLoading) {
                    ActionBar actionBar = mActivity.getSupportActionBar();
                    if (actionBar != null) {
                        actionBar.setTitle(title);
                    }
                }
            }

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                showFullScreenVideo(view, callback);
            }

            @Override
            public void onHideCustomView() {
                hideFullScreenVideo();
            }
        };
        webView.setWebChromeClient(webChromeClient);
        webView.setWebViewClient(new WebViewClientEx(webView) {

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                return shouldInterceptRequestEx(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hideLoading();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                if (!NetUtils.checkNetwork()) {
                    setEmptyText(R.string.err_retry);
//                    setEmptyIcon(R.drawable.ic_net_err);
                    showEmpty(true);
                    hideLoading();
                } else {
                    onReceivedWebErrors();
                }
            }
        });
    }

    public void loadUrl(String url) {
        loadUrl(url, true);
    }

    public void loadUrl(String url, boolean showLoading) {
        if (TextUtils.isEmpty(url) || mWebView == null) {
            return;
        }
        LOGD("WebImpl", "path--->" + url);
        if (showLoading) {
            showLoading();
        }
        Map<String, String> extraHeaders = new HashMap<>();
        mWebView.loadUrl(url, extraHeaders);
    }

    public void replaceHtml(String target, String replacement) {
        mTarget = target;
        mReplacement = replacement;
    }

    public WebView getWebView() {
        return mWebView;
    }

    public void onReceivedWebErrors() {

    }

    public void invokeJsMethod(String method) {
        LOGD(TAG, "invokeJsMethod:" + method + ";");
        loadUrl("javascript:" + method + "()", false);
    }

    public void invokeJsMethod(String method, String param) {
        LOGD(TAG, "invokeJsMethod:" + method + ";" + param);
        loadUrl("javascript:" + method + "('" + param + "')", false);
    }

    /**
     * 调用JS方法
     *
     * @param webView
     * @param method
     * @param param
     */
    public static void invokeJsMethod(WebView webView, String method, String param) {
        if (webView == null) {
            return;
        }
        LOGD(TAG, "invokeJsMethod:" + method + ";" + param);
        webView.loadUrl("javascript:" + method + "('" + param + "')");
    }

    private void showFullScreenVideo(View view, WebChromeClient.CustomViewCallback callback) {
        if (mVideoFullScreen) {
            return;
        }
        mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        ActionBar actionBar = mActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        if (mCustomCallback != null) {
            mCustomCallback.onCustomViewHidden();
            mCustomCallback = null;
            return;
        }
        mFullscreenContainer.addView(view);
        mFullScreenView = view;
        mCustomCallback = callback;
        // hide main browser view
        mWebView.setVisibility(View.GONE);
        mFullscreenContainer.setVisibility(View.VISIBLE);
        mFullscreenContainer.bringToFront();
        mVideoFullScreen = true;
    }

    private void hideFullScreenVideo() {
        if (!mVideoFullScreen) {
            return;
        }
        mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActionBar actionBar = mActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }
        if (mFullScreenView != null) {
            mFullScreenView.setVisibility(View.GONE);
            mFullscreenContainer.removeView(mFullScreenView);
            mFullscreenContainer.setVisibility(View.GONE);
            mFullScreenView = null;
            if (mWebView != null) {
                mWebView.setVisibility(View.VISIBLE);
            }
        }
        if (mCustomCallback != null) {
            mCustomCallback.onCustomViewHidden();
            mCustomCallback = null;
        }
        mVideoFullScreen = false;
    }

    public boolean onBackPressed() {
        if (mVideoFullScreen) {
            hideFullScreenVideo();
            return true;
        }
        return false;
    }

    public void onPause() {
        if (mWebView != null) {
            mWebView.onPause();
        }
        invokeJsMethod("pageWillDisappear");
    }

    public void onResume() {
        if (mWebView != null) {
            mWebView.onResume();
        }
        invokeJsMethod("pageWillAppear");
    }

    public void onDestroy() {
        // TODO: 16/1/28 WebView.destroy() called while still attached!
        if (mWebView != null) {
            mWebView.destroy();
        }
    }

    public WebResourceResponse shouldInterceptRequestEx(WebView view, String url) {
        return null;
    }

    public void showLoading() {
        progressZone.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        progressZone.setVisibility(View.GONE);
    }

    public void setUpdateTitleAfterLoading(boolean updateTitleAfterLoading) {
        this.mUpdateTitleAfterLoading = updateTitleAfterLoading;
    }

    @Override
    public void showLoading(boolean show) {
        if (progressZone == null) {
            return;
        }
        if (show) {
            progressZone.setVisibility(View.VISIBLE);
        } else {
            progressZone.setVisibility(View.GONE);
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
            emptyRetry.setVisibility(View.GONE);
        } else {
            emptyRetry.setText(resId);
            emptyRetry.setVisibility(View.VISIBLE);
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

    }

    @Override
    public void setEmptyIconResId(int resId) {

    }
}
