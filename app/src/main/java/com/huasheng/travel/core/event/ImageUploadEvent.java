package com.huasheng.travel.core.event;

/**
 * Created by yc on 16/3/15.
 */
public class ImageUploadEvent {
    public final String imageUrl;
    public final int code;

    public ImageUploadEvent(String imageUrl, int code) {
        this.imageUrl = imageUrl;
        this.code = code;
    }
}
