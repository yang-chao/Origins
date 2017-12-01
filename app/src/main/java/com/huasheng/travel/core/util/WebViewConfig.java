package com.huasheng.travel.core.util;

import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.huasheng.travel.BuildConfig;
import com.huasheng.travel.BaseApplication;
import com.huasheng.travel.ui.widget.WebChromeClientEx;

/**
 * Created by yc on 16/2/24.
 */
public class WebViewConfig {

    public static void config(WebView webView) {
        config(webView, true);
    }

    public static void config(WebView webView, boolean useCache) {
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
        if (useCache) {
            //无网时取本地缓存
            if (!NetUtils.checkNetwork()) { // loading offline
                webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            } else {
                webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT); // load online by default
            }
        } else {
            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        }
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setGeolocationEnabled(true);
        webView.getSettings().setGeolocationDatabasePath(webView.getContext().getCacheDir().toString());
        webView.getSettings().setUserAgentString(NetUtils.getUserAgent(true));
        WebChromeClient webChromeClient = new WebChromeClientEx(webView);
        webView.setWebChromeClient(webChromeClient);
    }
}
