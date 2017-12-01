package com.huasheng.travel.core.model;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;


import com.huasheng.travel.api.model.Photo;
import com.huasheng.travel.core.util.SysUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by YC on 15-1-23.
 */
public class LocalImageModel {

    public static final String ALL_IMAGE_NAME = "全部图片";
    public static final String ALL_IMAGE_CAMERA = "camera";
    public static final String ALL_IMAGE_ADD = "add";

    public static int getBigImageWidth(Context context) {
        return SysUtils.getScreenWidth() - 20;
    }

    public static Map<String, List<Photo>> getLocalImage(Context context) {
        int targetWidth = getBigImageWidth(context);
        Map<String, List<Photo>> groupMap = new HashMap<>();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        //只查询jpeg和png的图片
        Cursor cursor = context.getContentResolver().query(uri, null,
                MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Images.Media.MIME_TYPE + "=?",
                new String[]{"image/jpeg", "image/png", "image/gif"}, MediaStore.Images.Media.DATE_ADDED);

        if (cursor == null) {
            return Collections.emptyMap();
        }

        List<Photo> allList = new ArrayList<Photo>();
        Photo cameraEntry = new Photo();
        cameraEntry.path = ALL_IMAGE_CAMERA;
        allList.add(cameraEntry);
        groupMap.put(ALL_IMAGE_NAME, allList);

        for (int i = cursor.getCount() - 1; i >= 0; i--) {
            cursor.moveToPosition(i);
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            Photo data = new Photo();
            data.path = path;
            data.width = targetWidth;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                int width = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.WIDTH));
                int height = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT));
                if (width != 0) {
                    data.height = (int) Math.floor((float) height * data.width / width);
                }
            }

            if (data.height == 0) { // 低版本系统没有宽高信息，或者下载的图片宽高信息没有存到数据库
                // 解析图片，获取宽高
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(path, options);
                int width = options.outWidth;
                int height = options.outHeight;
                if (width != 0) {
                    data.height = (int) Math.floor((float) height * data.width / width);
                }
            }

            //获取该图片的父路径名
            String parentName = new File(path).getParentFile().getName();

            //根据父路径名将图片放入到groupMap中
            if (!groupMap.containsKey(parentName)) {
                List<Photo> childList = new ArrayList<Photo>();
                childList.add(data);
                groupMap.put(parentName, childList);
            } else {
                groupMap.get(parentName).add(data);
            }
            groupMap.get(ALL_IMAGE_NAME).add(data);
        }
        cursor.close();
        return groupMap;
    }

    public static String getImagePath(Context context, Uri uri) {
        String path = null;
        String[] pro = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, pro, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

}
