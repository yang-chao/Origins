package com.huasheng.travel.core.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.huasheng.travel.BaseApplication;

import java.io.File;


/**
 * 拍照、从相册取图以及剪裁图片相关处理。<br>
 * Created by YangChao on 15-12-22 下午2:02.
 */
public class Crop {

    public static final int REQ_PICK = 300; // 从相册取图
    public static final int REQ_PICK_AND_CROP = 301; // 从相册取图并剪裁
    public static final int REQ_CAPTURE = 302; // 拍照
    public static final int REQ_CROP = 303; // 剪裁
    /**
     * 拍照临时文件存储路径
     */
    private static final String CAPTURE_TEMP_FILE_PATH = "/capture_temp.jpg";
    /**
     * 剪裁图片临时存储路径
     */
    private static final String CROP_TEMP_FILE_PATH = "/crop_temp.jpg";
    private Intent cropIntent;

    public Crop(String action) {
        cropIntent = new Intent(action);
        cropIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
    }

    public static String getCaptureTempPath() {
        return BaseApplication.getInstance().getExternalFilesDir(null) + CAPTURE_TEMP_FILE_PATH;
    }

    public static Uri getCaptureTempUri() {
        return Uri.fromFile(new File(getCaptureTempPath()));
    }

    public static String getCropTempPath() {
        return BaseApplication.getInstance().getExternalFilesDir(null) + CROP_TEMP_FILE_PATH;
    }

    public static Uri getCropTempUri() {
        return Uri.fromFile(new File(getCropTempPath()));
    }

    public static boolean captureFileExists() {
        return new File(getCaptureTempPath()).exists();
    }

    // TODO: 15-12-22 选择合适的时间删除临时文件
    public static void clearTempFiles() {
        FileUtils.deleteFile(new File(getCaptureTempPath()));
        FileUtils.deleteFile(new File(getCropTempPath()));
    }

    /**
     * 从相册选取照片并剪裁（Android 6.0上剪裁无效）
     *
     * @param activity
     */
    public static void pickAndCrop(Activity activity) {
        new Crop(Intent.ACTION_PICK)
                .crop()
                .setType("image/*")
                .asSquare()
                .outSize(128, 128)
                .returnData(false)
                .output(getCropTempUri())
                .noFaceDetection(true)
                .outputFormat(Bitmap.CompressFormat.JPEG.toString())
                .start(activity, REQ_PICK_AND_CROP);
    }

    /**
     * 从相册选取照片
     *
     * @param activity
     */
    public static void pick(Activity activity) {
        try {
            new Crop(Intent.ACTION_PICK)
                    .setType("image/*")
                    .returnData(true)
                    .start(activity, REQ_PICK);
        } catch (SecurityException e) {
            new Crop(Intent.ACTION_GET_CONTENT)
                    .setType("image/*")
                    .returnData(true)
                    .start(activity, REQ_PICK);
        }
    }

    /**
     * 拍照并保存临时文件
     *
     * @param activity
     */
    public static void takePicture(Activity activity) {
        new Crop(MediaStore.ACTION_IMAGE_CAPTURE)
                .output(getCaptureTempUri())
                .start(activity, REQ_CAPTURE);
    }

    /**
     * 剪裁图片
     *
     * @param activity
     * @param uri
     */
    public static void crop(Activity activity, Uri uri) {
        new Crop("com.android.camera.action.CROP")
                .setDataAndType(uri == null ? Crop.getCaptureTempUri() : uri, "image/*")
                .crop()
                .asSquare()
                .outSize(128, 128)
                .output(getCropTempUri())
                .returnData(false)
                .outputFormat(Bitmap.CompressFormat.JPEG.toString())
                .noFaceDetection(true)
                .start(activity, REQ_CROP);
    }

    /**
     * 剪裁图片
     *
     * @param activity
     * @param uri
     */
    public static void crop(Activity activity, Uri uri, int outWidth, int outHeight) {
        new Crop("com.android.camera.action.CROP")
                .setDataAndType(uri == null ? Crop.getCaptureTempUri() : uri, "image/*")
                .crop()
                .asSquare()
                .outSize(outWidth, outHeight)
                .output(getCropTempUri())
                .returnData(false)
                .outputFormat(Bitmap.CompressFormat.JPEG.toString())
                .noFaceDetection(true)
                .start(activity, REQ_CROP);
    }

    public Crop crop() {
        cropIntent.putExtra(Extra.CROP, "true");
        return this;
    }

    public Crop setData(Uri data) {
        cropIntent.setData(data);
        return this;
    }

    public Crop setType(String type) {
        cropIntent.setType(type);
        return this;
    }

    public Crop setDataAndType(Uri data, String type) {
        cropIntent.setDataAndType(data, type);
        return this;
    }

    public Crop output(Uri uri) {
        cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        return this;
    }

    /**
     * Set fixed aspect ratio for crop area
     *
     * @param x Aspect X
     * @param y Aspect Y
     */
    public Crop withAspect(int x, int y) {
        cropIntent.putExtra(Extra.ASPECT_X, x);
        cropIntent.putExtra(Extra.ASPECT_Y, y);
        return this;
    }

    /**
     * Crop area with fixed 1:1 aspect ratio
     */
    public Crop asSquare() {
        cropIntent.putExtra(Extra.ASPECT_X, 1);
        cropIntent.putExtra(Extra.ASPECT_Y, 1);
        return this;
    }

    public Crop outSize(int x, int y) {
        cropIntent.putExtra(Extra.OUTPUT_X, x);
        cropIntent.putExtra(Extra.OUTPUT_Y, y);
        return this;
    }

    public Crop returnData(boolean returnData) {
        cropIntent.putExtra(Extra.RETURN_DATA, returnData);
        return this;
    }

    public Crop outputFormat(String outputFormat) {
        cropIntent.putExtra(Extra.OUTPUT_FORMAT, outputFormat);
        return this;
    }

    public Crop noFaceDetection(boolean noFaceDetection) {
        cropIntent.putExtra(Extra.NO_FACE_DETECTION, noFaceDetection);
        return this;
    }

    public Crop scale(boolean scale) {
        cropIntent.putExtra(Extra.SCALE, scale);
        return this;
    }

    public void start(Activity activity, int requestCode) {
        cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        activity.startActivityForResult(cropIntent, requestCode);
    }

    interface Extra {
        String CROP = "crop";
        String ASPECT_X = "aspectX";
        String ASPECT_Y = "aspectY";
        String OUTPUT_X = "outputX";
        String OUTPUT_Y = "outputY";
        String OUTPUT_FORMAT = "outputFormat";
        String NO_FACE_DETECTION = "noFaceDetection";
        String RETURN_DATA = "return-data";
        String SCALE = "scale";
    }
}
