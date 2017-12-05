package com.assassin.origins.ui.common;

import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;

import com.assassin.origins.R;
import com.assassin.origins.ui.common.jsinterface.CommonJSInterface;

/**
 * Created by YangChao on 15-12-28 上午11:57.
 */
public class WebVideoActivity extends BaseActivity {
    private static final String TAG = "WebVideoActivity";
    private WebImpl mWebImpl;
    private CommonJSInterface mJSInterface;

    @Override
    protected void initActionBar() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_video);

        WebView webView = (WebView) findViewById(R.id.webview);
//        mJSInterface = new CommonJSInterface(this, webView);
        String url = getIntent() != null ? getIntent().getStringExtra(WebImpl.PARAM_URL) : null;
//        webView.addJavascriptInterface(mJSInterface, "Travel");
        mWebImpl = new WebImpl(this, findViewById(R.id.root), findViewById(R.id.fullscreen_container), url);
        if (mWebImpl.checkNetWork()) {
            boolean updateTitle = getIntent() != null ? getIntent().getBooleanExtra(WebImpl.PARAM_UPDATE_TITLE_AFTER_LOADING, true) : true;
            setUpdateTitleAfterLoading(updateTitle);
            if (!TextUtils.isEmpty(url)) {
                mWebImpl.loadUrl(url);
            }
        }

        String htmlReplaceTarget = getIntent().getStringExtra(WebImpl.PARAM_REPLACE_TARGET);
        String htmlReplaceReplacement = getIntent().getStringExtra(WebImpl.PARAM_REPLACE_REPLACEMENT);
        if (!TextUtils.isEmpty(htmlReplaceTarget) && !TextUtils.isEmpty(htmlReplaceReplacement)) {
            mWebImpl.replaceHtml(htmlReplaceTarget, htmlReplaceReplacement);
        }
    }

    public WebView getWebView() {
        return mWebImpl.getWebView();
    }

    public void loadUrl(String url) {
        mWebImpl.loadUrl(url);
    }

    protected void setUpdateTitleAfterLoading(boolean update) {
        mWebImpl.setUpdateTitleAfterLoading(update);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebImpl.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebImpl.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebImpl.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!mWebImpl.onBackPressed()) {
            super.onBackPressed();
        }
    }
}
