/*
 * @(#) WebChromeClientEx.java 2014-1-7
 * 
 * Copyright 2010 NetEase.com, Inc. All rights reserved.
 */
package com.assassin.origins.ui.widget;

import android.graphics.Bitmap;
import android.os.Message;
import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * 类WebChromeClientEx
 *
 * @author hzliuqing
 * @version 2014-1-7
 */
public class WebChromeClientEx extends WebChromeClient {
    
    private WebView webView;
    
    public WebChromeClientEx (WebView webView) {
        this.webView = webView;
    }
    
    @Override
    public final boolean onJsPrompt(WebView view, String url, String message,
            String defaultValue, JsPromptResult result) { 
        if (view instanceof WebViewEx) {
            if (((WebViewEx) webView).handleJsInterface(view, url, message, defaultValue, result)) {
                return true;
            }
        }
        
        return super.onJsPrompt(view, url, message, defaultValue, result);
    }
    
    @Override
    public /*final*/ void onProgressChanged(WebView view, int newProgress) {
        if (webView instanceof WebViewEx) {
            ((WebViewEx) webView).injectJavascriptInterfaces(view);
        }
        super.onProgressChanged(view, newProgress);
    }
    
    @Override
    public void onReceivedTitle(WebView view, String title) {
        if (webView instanceof WebViewEx) {
            ((WebViewEx) webView).injectJavascriptInterfaces(view);
        }
        super.onReceivedTitle(view, title);
    }
    
    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        if (webView instanceof WebViewEx) {
            ((WebViewEx) webView).injectJavascriptInterfaces(view);
        }
        super.onReceivedIcon(view, icon);
    }
    
    @Override
    public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
        if (webView instanceof WebViewEx) {
            ((WebViewEx) webView).injectJavascriptInterfaces(view);
        }
        super.onReceivedTouchIconUrl(view, url, precomposed);
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog,
                    boolean isUserGesture, Message resultMsg) {
        if (webView instanceof WebViewEx) {
            ((WebViewEx) webView).injectJavascriptInterfaces(view);
        }
        return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
    }

}
