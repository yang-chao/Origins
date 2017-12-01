/*
 * @(#) WebViewClientEx.java 2014-1-7
 * 
 * Copyright 2010 NetEase.com, Inc. All rights reserved.
 */
package com.huasheng.travel.ui.widget;

import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * 类WebViewClientEx
 *
 * @author hzliuqing
 * @version 2014-1-7
 */
public class WebViewClientEx extends WebViewClient {

    private WebView webView;

    public WebViewClientEx(WebView webView) {
        this.webView = webView;
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        if (webView instanceof WebViewEx) {
            ((WebViewEx) webView).injectJavascriptInterfaces(view);
        }
        super.onLoadResource(view, url);
    }

    @Override
    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
        if (webView instanceof WebViewEx) {
            ((WebViewEx) webView).injectJavascriptInterfaces(view);
        }
        super.doUpdateVisitedHistory(view, url, isReload);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        if (webView instanceof WebViewEx) {
            ((WebViewEx) webView).injectJavascriptInterfaces(view);
        }
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if (webView instanceof WebViewEx) {
            ((WebViewEx) webView).injectJavascriptInterfaces(view);
        }
        super.onPageFinished(view, url);
    }
}
