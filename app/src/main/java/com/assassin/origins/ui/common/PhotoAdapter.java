package com.assassin.origins.ui.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.assassin.origins.R;
import com.assassin.origins.api.model.Photo;
import com.assassin.origins.core.model.LocalImageModel;
import com.assassin.origins.core.util.NativeImageLoader;
import com.assassin.origins.core.util.SysUtils;

/**
 * Created by yc on 16/5/6.
 */
class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoHolder> {

    private Map<String, List<Photo>> mAllData = new HashMap<>();
    private List<Photo> mData = new ArrayList<>();
    private String mCurSelectFolder;

    private NativeImageLoader mNativeImageLoader;

    private Context mContext;
    private Point mPoint;


    public PhotoAdapter(Context context) {
        mContext = context;
        mNativeImageLoader = NativeImageLoader.getInstance(context);
        final int itemSize = SysUtils.getScreenWidth() / 3;
        mPoint = new Point(itemSize, itemSize);
    }

    public void updateData(Map<String, List<Photo>> source) {
        if (source == null || source.isEmpty()) {
            return;
        }
        mAllData.clear();
        mAllData.putAll(source);
        // 加载完数据后默认显示“全部图片”文件夹
        switchFolder(LocalImageModel.ALL_IMAGE_NAME);
    }

    public void switchFolder(String folderName) {
        mCurSelectFolder = folderName;
        List<Photo> data = mAllData.get(folderName);
        if (data != null) {
            mData.clear();
            mData.addAll(data);
            notifyDataSetChanged();
        }
    }

    @Override
    public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PhotoHolder(View.inflate(parent.getContext(), R.layout.adapter_photo_picker, null));
    }

    @Override
    public void onBindViewHolder(final PhotoHolder holder, final int position) {
        ViewGroup.LayoutParams params = holder.image.getLayoutParams();
        params.height = mPoint.y;
        holder.image.setLayoutParams(params);

        final Photo item = mData.get(position);
        final String imgPath = item.path;
        if (LocalImageModel.ALL_IMAGE_CAMERA.equals(imgPath)) {
            holder.checkBox.setVisibility(View.GONE);
            holder.mask.setVisibility(View.GONE);
            holder.image.setTag(null);
            holder.image.setScaleType(ImageView.ScaleType.CENTER);
            holder.image.setImageResource(R.drawable.ic_photo_picker_camera);
            holder.image.setBackgroundColor(0xFFFED700);
        } else {
            boolean checked = ((PhotoPickerActivity) mContext).getSelectedImagePaths().contains(imgPath);
            holder.checkBox.setChecked(checked);
            holder.checkBox.setVisibility(checked ? View.VISIBLE : View.GONE);
            holder.mask.setVisibility(checked ? View.VISIBLE : View.GONE);

            params = holder.mask.getLayoutParams();
            params.height = mPoint.y;
            holder.mask.setLayoutParams(params);

            holder.image.setTag(imgPath);

            mNativeImageLoader.loadNativeImage(imgPath, mPoint, R.drawable.ic_default, new NativeImageLoader.NativeImageCallBack() {
                @Override
                public void onImageLoaded(Bitmap bitmap, String path) {
                    if (holder.image != null) {
                        Object tag = holder.image.getTag();
                        if (tag != null && tag.equals(path)) {
                            holder.image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            holder.image.setImageBitmap(bitmap);
                        }
                    }
                }

                @Override
                public void setDefaultImageRes(int defaultImageRes, String path) {
                    if (holder.image != null) {
                        Object tag = holder.image.getTag();
                        if (tag != null && tag.equals(path)) {
                            holder.image.setScaleType(ImageView.ScaleType.CENTER);
                            holder.image.setImageResource(defaultImageRes);
                        }
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void clearCache() {
        if (mNativeImageLoader != null) {
            mNativeImageLoader.clearCache();
        }
    }

    public static class ImageEntry {
        public boolean add;
        public String path;
        public String folderName;
        public int position;
        public Bitmap bitmap;
        public int width;
        public int height;
    }

    class PhotoHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private CheckBox checkBox;
        private View mask;

        public PhotoHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
            mask = itemView.findViewById(R.id.mask);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PhotoPickerActivity activity = (PhotoPickerActivity) mContext;
                    if (!checkBox.isChecked() && !activity.checkMaxCount()) {
                        return;
                    }
                    Photo item = mData.get(getAdapterPosition());
                    if (item == null) {
                        return;
                    }
                    String imgPath = item.getPath();
                    if (LocalImageModel.ALL_IMAGE_CAMERA.equals(imgPath)) {
                        ((PhotoPickerActivity) mContext).tryOpenCamera();
                    } else {
                        ImageEntry entry = new ImageEntry();
                        entry.path = imgPath;
                        entry.folderName = mCurSelectFolder;
                        entry.position = getAdapterPosition();
                        entry.width = item.width;
                        entry.height = item.height;
                        if (checkBox.isChecked()) {
                            entry.add = false;
                        } else {
                            BitmapDrawable bitmapDrawable = (BitmapDrawable) image.getDrawable();
                            if (bitmapDrawable == null) {
                                return;
                            }
                            entry.add = true;
                            entry.bitmap = bitmapDrawable.getBitmap();
                        }
                        activity.onImageSelected(entry);
                        notifyItemChanged(getAdapterPosition());
                    }
                }
            });
        }
    }
}
