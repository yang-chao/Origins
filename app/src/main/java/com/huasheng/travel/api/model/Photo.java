package com.huasheng.travel.api.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yc on 16/5/6.
 */
public class Photo implements Parcelable {
    public static final Creator<Photo> CREATOR = new Creator<Photo>() {

        @Override
        public Photo createFromParcel(Parcel source) {
            Photo imageData = new Photo();
            imageData.path = source.readString();
            imageData.url = source.readString();
            imageData.desc = source.readString();
            imageData.width = source.readInt();
            imageData.height = source.readInt();
            return imageData;
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };
    public String path;
    public String url;
    public String desc;
    public int width;
    public int height;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeString(url);
        dest.writeString(desc);
        dest.writeInt(width);
        dest.writeInt(height);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
