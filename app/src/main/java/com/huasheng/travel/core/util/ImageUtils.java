package com.huasheng.travel.core.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;

import com.huasheng.travel.core.util.volley.VolleyManager;

/**
 * Created by YC on 15-1-4.
 */
public class ImageUtils {
    private static final String TAG = "ImageUtils";

    private static boolean checkQiNiuHost(String url) {
        return url.startsWith("http://cdn.qiudd.net") || url.startsWith("http://ucdn.qiudd.net");
    }

    public static Uri configSlimUrl(Uri uri) {
        String url = uri.toString();
        if (TextUtils.isEmpty(url)) {
            return uri;
        }
        if (url.contains(".gif")) {
            return uri;
        }
        if (checkQiNiuHost(url)) {
            String s = uri.toString() + (TextUtils.isEmpty(uri.getQuery()) ? "?imageslim" : "|imageslim");
//            LOGD(TAG, "imageUrl: " + s);
            return Uri.parse(s);
        }
        return uri;
    }

    public static String configUrlWidth(String url, int width) {
        if (!checkQiNiuHost(url)) {
            return url;
        }
        if (!url.contains("?")) {
            url += "?";
        }
        if (!url.endsWith("?")) {
            url += "|";
        }
        return url + "imageView2/2/w/" + width;
    }

    public static void loadImage(ImageView view, String url) {
        loadImage(view, url, 0, 0);
    }

    public static void loadImage(ImageView view, String url, final int defaultImageResId, final int errorImageResId) {
        ImageLoader imageLoader = VolleyManager.getImageLoader();
        ImageLoader.ImageListener imageListener = ImageLoader.getImageListener(view, defaultImageResId, errorImageResId);
        imageLoader.get(url, imageListener);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
//        BitmapFactory.decode

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

}
