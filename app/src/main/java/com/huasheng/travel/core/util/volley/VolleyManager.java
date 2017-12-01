package com.huasheng.travel.core.util.volley;

import android.content.Context;

import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;

import java.io.File;

import com.huasheng.travel.BaseApplication;

/**
 * Created by YC on 14/12/20.
 */
public class VolleyManager {

    /**
     * 缓存目录
     */
    public static final String DEFAULT_CACHE_DIR = "volley_cache";
    private static VolleyManager sVolleyManager;
    private RequestQueue mRequestQueue;
    private ImageLoader.ImageCache mImageCache;
    private ImageLoader mImageLoader;


    private VolleyManager() {
        init(BaseApplication.getInstance());
    }

    private static VolleyManager getVolleyManager() {
        if (sVolleyManager == null) {
            sVolleyManager = new VolleyManager();
        }
        return sVolleyManager;
    }

    public static RequestQueue getRequestQueue() {
        return getVolleyManager().mRequestQueue;
    }

    public static ImageLoader.ImageCache getImageCache() {
        return getVolleyManager().mImageCache;
    }

    public static ImageLoader getImageLoader() {
        return getVolleyManager().mImageLoader;
    }

    public static void addRequest(Request request) {
        getRequestQueue().add(request);
    }

    private void init(Context context) {
        mRequestQueue = CustomVolley.newRequestQueue(context);
        mImageCache = new BitmapLruCache(context);
        mImageLoader = new ImageLoader(mRequestQueue, mImageCache);
        mRequestQueue.start();
    }

    private static class CustomVolley {
        /**
         * Default on-disk cache directory.
         */

        public static RequestQueue newRequestQueue(Context context, HttpStack stack) {
            File cacheDir = new File(context.getExternalCacheDir(), DEFAULT_CACHE_DIR);
            stack = new HurlStack();
            Network network = new BasicNetwork(stack);
            // 修改缓存大小
            RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir, 200 * 1024 * 1024), network);
            queue.start();
            return queue;
        }

        /**
         * Creates a default instance of the worker pool and calls {@link RequestQueue#start()} on it.
         *
         * @param context A {@link Context} to use for creating the cache dir.
         * @return A started {@link RequestQueue} instance.
         */
        public static RequestQueue newRequestQueue(Context context) {
            return newRequestQueue(context, null);
        }
    }
}
