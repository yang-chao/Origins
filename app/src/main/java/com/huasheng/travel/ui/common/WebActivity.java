package com.huasheng.travel.ui.common;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import org.json.JSONException;
import org.json.JSONObject;

import com.huasheng.travel.R;
import com.huasheng.travel.core.util.NetUtils;
import com.huasheng.travel.ui.common.jsinterface.CommonJSInterface;

import de.greenrobot.event.EventBus;

/**
 * Created by YangChao on 15-12-28 上午11:57.
 */
public class WebActivity extends BaseActivity {
    private static final String TAG = "WebActivity";

    private WebImpl mWebImpl;
    private CommonJSInterface mJSInterface;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        if (mJSInterface.getShare() != null) {
//            getMenuInflater().inflate(R.menu.menu_share, menu);
//        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                return true;
            case android.R.id.home:
                if (shouldGoBack()) {
                    finish();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        EventBus.getDefault().register(this);

        WebView webView = (WebView) findViewById(R.id.webview);
        webView.setBackgroundColor(getResources().getColor(R.color.colorBaseWhite));
        webView.addJavascriptInterface(mJSInterface, "Travel");
        String url = getIntent() != null ? getIntent().getStringExtra(WebImpl.PARAM_URL) : null;
        mWebImpl = new WebImpl(this, findViewById(R.id.root), findViewById(R.id.fullscreen_container), url);
        if (mWebImpl.checkNetWork()) {
            boolean updateTitle = getIntent() == null || getIntent().getBooleanExtra(WebImpl.PARAM_UPDATE_TITLE_AFTER_LOADING, true);
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

    @Override
    protected void onStart() {
        super.onStart();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            if (getIntent() != null) {
                String title = getIntent().getStringExtra(WebImpl.PARAM_TITLE);
                if (!TextUtils.isEmpty(title)) {
                    actionBar.setTitle(title);
                }
            }
        }
    }

    @Override
    protected void initActionBar() {
        if (!getIntent().getBooleanExtra(WebImpl.PARAM_HIDE_ACTION_BAR, false)) {
            super.initActionBar();
        }
    }

    public WebView getWebView() {
        return mWebImpl.getWebView();
    }

    public void loadUrl(String url) {
        mWebImpl.loadUrl(url);
    }

    public void loadJSMethod(String jsMethod) {
        mWebImpl.loadUrl(jsMethod, false);
    }

    protected void setUpdateTitleAfterLoading(boolean update) {
        mWebImpl.setUpdateTitleAfterLoading(update);
    }

    public void invokeJsMethod(String method) {
        mWebImpl.invokeJsMethod(method);
    }

    public void invokeJsMethod(String method, String param) {
        mWebImpl.invokeJsMethod(method, param);
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
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        if (mWebImpl.onBackPressed()) {
            return;
        }
        if (!shouldGoBack()) {
            return;
        }
        super.onBackPressed();
    }

    private boolean shouldGoBack() {
        if (!NetUtils.checkNetwork()) {
            return true;
        }
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.getBoolean(WebImpl.PARAM_WILL_BACK_ACTION, false)) {
            invokeJsMethod("willBack");
            return false;
        }
        return true;
    }

    public void onReceivedWebErrors() {
        mWebImpl.onReceivedWebErrors();
    }

    protected void hideLoading() {
        mWebImpl.hideLoading();
    }

    protected void refresh() {
        if (mWebImpl != null && mWebImpl.getWebView() != null) {
            mWebImpl.getWebView().reload();
        }
    }
}
