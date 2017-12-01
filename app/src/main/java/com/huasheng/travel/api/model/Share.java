package com.huasheng.travel.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.io.Serializable;

/**
 * Created by yc on 16/1/11.
 */
public class Share implements Serializable {

    public static final String PLATFORM_ALL = "all";
    public static final String PLATFORM_QQ = "qq";
    public static final String PLATFORM_QZ = "qz";
    public static final String PLATFORM_WX = "wx";
    public static final String PLATFORM_WX_CIRCLE = "pyq";
    public static final String PLATFORM_WB = "wb";
    public static final String PLATFORM_COPY = "copy";

    @SerializedName("type")
    private String platform;
    private String title;
    @SerializedName("desc")
    private String content;
    private String url;
    private String image;
    private int imageResId;
    private File mFile;
    private boolean isAddFrom = true;  //微博分享时是否添加 “分享自肆客足球app”

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public File getFile() {
        return mFile;
    }

    public void setFile(File file) {
        mFile = file;
    }

    public boolean isAddFrom() {
        return isAddFrom;
    }

    public void setAddFrom(boolean addFrom) {
        isAddFrom = addFrom;
    }
}
