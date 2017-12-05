package com.assassin.origins.core.util;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 本地图片加载器,采用的是异步解析本地图片，单例模式利用getInstance()获取NativeImageLoader实例
 * 调用loadNativeImage()方法加载本地图片，此类可作为一个加载本地图片的工具类
 */
public class NativeImageLoader {
    /**
     * 缓存目录
     */
    public static final String DEFAULT_CACHE_DIR = "disk_cache";
    private static NativeImageLoader mInstance;
    private final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            MsgEntry entry = (MsgEntry) msg.obj;
            if (entry == null) {
                return;
            }
            if (entry.bitmap != null) {
                entry.callBack.onImageLoaded(entry.bitmap, entry.path);
            } else if (entry.defaultResId != 0) {
                entry.callBack.setDefaultImageRes(entry.defaultResId, entry.path);
            }
        }
    };
    private Context mContext;
    private LruCache<String, Bitmap> mMemoryCache;
    private DiskLruCache mDiskCache;
    private ExecutorService mImageThreadPool = Executors.newFixedThreadPool(2);

    private NativeImageLoader(Context context) {
        mContext = context.getApplicationContext();
        //获取应用程序的最大内存
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024 / 1024);
        //用最大内存的1/8来存储图片
        final int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {

            //获取每张图片的大小
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024 / 1024;
            }
        };

        try {
            mDiskCache = DiskLruCache.open(new File(context.getExternalCacheDir(), DEFAULT_CACHE_DIR),
                    SysUtils.getAppVersionCode(), 1, 200 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过此方法来获取NativeImageLoader的实例
     *
     * @return
     */
    public static NativeImageLoader getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new NativeImageLoader(context);
        }
        return mInstance;
    }

    public void clearCache() {
        if (mMemoryCache != null) {
            mMemoryCache.evictAll();
        }
    }

    /**
     * 加载本地图片，对图片不进行裁剪
     *
     * @param path
     * @param callBack
     * @return
     */
    public void loadNativeImage(final String path, int defaultImageRes, final NativeImageCallBack callBack) {
        loadNativeImage(path, null, defaultImageRes, callBack);
    }

    /**
     * 此方法来加载本地图片，这里的Point是用来封装ImageView的宽和高，我们会根据ImageView控件的大小来裁剪Bitmap
     * 如果你不想裁剪图片，调用loadNativeImage(final String path, final NativeImageCallBack mCallBack)来加载
     *
     * @param path
     * @param point
     * @param callBack
     * @return
     */
    public void loadNativeImage(final String path, final Point point, final int defaultImageRes, final NativeImageCallBack callBack) {
        mImageThreadPool.execute(new Runnable() {

            @Override
            public void run() {
                // 先获取内存中的Bitmap
                Bitmap bitmap = getBitmapFromMemCache(path);
                if (bitmap == null) {
                    // 内存没有命中，从磁盘缓存读取
                    bitmap = getBitmapFromDisk(path);
                }
                if (bitmap != null) {
                    // Post到主线程
                    postImage(path, bitmap, 0, callBack);

                    addBitmapToMemoryCache(path, bitmap);
                } else { // 若该Bitmap不在内存缓存中，则启用线程去加载本地的图片，并将Bitmap加入到MemoryCache和DiskCache中
                    // 设置默认图片
                    postImage(path, null, defaultImageRes, callBack);

                    //先获取图片的缩略图；剪裁时进一步缩小图片长宽以减小内存占用，增加图片加载速度
                    bitmap = decodeThumbBitmapForFile(path, point == null ? 0 : point.x / 2, point == null ? 0 : point.y / 2);
                    if (bitmap == null) {
                        return;
                    }
                    // Post到主线程
                    postImage(path, bitmap, 0, callBack);

                    //将图片加入到内存缓存
                    addBitmapToMemoryCache(path, bitmap);
                    addBitmapToDisk(path, bitmap);
                }
            }
        });
    }

    private void postImage(String path, Bitmap bitmap, int defaultImageRes, NativeImageCallBack callBack) {
        Message msg = mHandler.obtainMessage();
        MsgEntry entry = new MsgEntry();
        entry.defaultResId = defaultImageRes;
        entry.path = path;
        entry.bitmap = bitmap;
        entry.callBack = callBack;
        msg.obj = entry;
        mHandler.sendMessage(msg);
    }

    /**
     * 往内存缓存中添加Bitmap
     *
     * @param key
     * @param bitmap
     */
    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (bitmap != null && getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    private void addBitmapToDisk(String key, Bitmap bitmap) {
        if (mDiskCache == null) {
            return;
        }
        OutputStream out = null;
        try {
            String md5Key = StringUtils.md5(key);
            if (md5Key != null) {
                md5Key = md5Key.toLowerCase();
            }
            DiskLruCache.Editor editor = mDiskCache.edit(md5Key);
            if (editor != null) {
                out = editor.newOutputStream(0);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                editor.commit();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 根据key来获取内存中的图片
     *
     * @param key
     * @return
     */
    private Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    private Bitmap getBitmapFromDisk(String key) {
        Bitmap bitmap = null;
        if (mDiskCache != null) {
            try {
                String md5Key = StringUtils.md5(key);
                if (md5Key != null) {
                    md5Key = md5Key.toLowerCase();
                }
                DiskLruCache.Snapshot snapshot = mDiskCache.get(md5Key);
                if (snapshot != null) {
                    InputStream in = snapshot.getInputStream(0);
                    bitmap = BitmapFactory.decodeStream(in);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    /**
     * 根据View(主要是ImageView)的宽和高来获取图片的缩略图
     *
     * @param path
     * @param viewWidth
     * @param viewHeight
     * @return
     */
    private Bitmap decodeThumbBitmapForFile(String path, int viewWidth, int viewHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        //设置为true,表示解析Bitmap对象，该对象不占内存
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        //设置缩放比例
        options.inSampleSize = computeScale(options, viewWidth, viewHeight);

        //设置为false,解析Bitmap对象加入到内存中
        options.inJustDecodeBounds = false;

        try {
            return BitmapFactory.decodeFile(path, options);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 根据View(主要是ImageView)的宽和高来计算Bitmap缩放比例。默认不缩放
     *
     * @param options
     * @param viewWidth
     * @param viewHeight
     */
    private int computeScale(BitmapFactory.Options options, int viewWidth, int viewHeight) {
        int inSampleSize = 1;
        int bitmapWidth = options.outWidth;
        int bitmapHeight = options.outHeight;

        if (viewWidth == 0 || viewHeight == 0) {
            return inSampleSize;
        }

        //假如Bitmap的宽度或高度大于我们设定图片的View的宽高，则计算缩放比例
        if (bitmapWidth > viewWidth || bitmapHeight > viewWidth) {
            int widthScale = Math.round((float) bitmapWidth / (float) viewWidth);
            int heightScale = Math.round((float) bitmapHeight / (float) viewHeight);

            //为了保证图片不缩放变形，我们取宽高比例最小的那个
            inSampleSize = widthScale < heightScale ? widthScale : heightScale;
        }
        return inSampleSize;
    }


    /**
     * 加载本地图片的回调接口
     *
     * @author xiaanming
     */
    public interface NativeImageCallBack {
        /**
         * 当子线程加载完了本地的图片，将Bitmap和图片路径回调在此方法中
         *
         * @param bitmap
         * @param path
         */
        void onImageLoaded(Bitmap bitmap, String path);

        void setDefaultImageRes(int defaultImageRes, String path);
    }

    class MsgEntry {
        Bitmap bitmap;
        String path;
        int defaultResId = 0;
        NativeImageCallBack callBack;
    }
}

