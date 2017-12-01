package com.huasheng.travel.ui.common;

import android.Manifest;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.huasheng.travel.R;
import com.huasheng.travel.api.model.Photo;
import com.huasheng.travel.core.constants.Constant;
import com.huasheng.travel.core.model.CommonHelper;
import com.huasheng.travel.core.model.LocalImageModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;



/**
 * 本地图片选择器
 * <p/>
 * Created by YC on 15-1-23.
 */
public class PhotoPickerActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Map<String, List<Photo>>> {

    public static final String PARAM_PATH = "param_path";
    public static final String PARAM_MAX_COUNT = "param_max_count";
    public static final String PARAM_SELECTED_COUNT = "param_selected_count";
    public static final int COUNT_INFINITE = -1;
    public static final int COUNT_DEFAULT = 9;

    static int TAKE_PHOTO_REQUEST_CODE = 100;
    /**
     * 可选图片总数
     */
    private int mMaxCount = 9;
    private int mSelectedCount;
    private PhotoAdapter mAdapter;
    /**
     * 保存Gallery中的图片路径
     */
    private ArrayList<Photo> mGalleryItems = new ArrayList<>();
    private List<String> mSelectedImagePath = new ArrayList<>();
    /**
     * 相机拍照后保存照片的URI
     */
    private Uri mPhotoUri;
//    private Button mConfirmBtn;


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.action_done);
        PhotoPickerActionProvider provider = (PhotoPickerActionProvider) MenuItemCompat.getActionProvider(menuItem);
        if (mSelectedCount != -1) {
            provider.updateCount(mSelectedCount);
        } else {
            provider.updateCount(mGalleryItems.size());
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photo_picker, menu);
        MenuItem menuItem = menu.findItem(R.id.action_done);
        MenuItemCompat.setActionProvider(menuItem, new PhotoPickerActionProvider(this, mMaxCount, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedCount == 0 || mGalleryItems.size() == 0) {
                    Toast.makeText(PhotoPickerActivity.this, R.string.toast_select_image_none, Toast.LENGTH_SHORT).show();
                    return;
                }

                // 完成选择
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra(PARAM_PATH, mGalleryItems);
                setResult(RESULT_OK, intent);
                finish();
            }
        }));
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_picker);
        mMaxCount = getIntent().getIntExtra(PARAM_MAX_COUNT, COUNT_DEFAULT);
        mSelectedCount = getIntent().getIntExtra(PARAM_SELECTED_COUNT, -1);

        ArrayList<Photo> photos = getIntent().getParcelableArrayListExtra(PARAM_PATH);
        if (photos != null) {
            for (int i = 0; i < photos.size(); i++) {
                mSelectedImagePath.add(photos.get(i).getPath());
            }
            mGalleryItems.addAll(photos);
        }

        // 图片列表
        RecyclerView recyclerView = (RecyclerView) findViewById(android.R.id.list);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new PhotoAdapter(this);
        recyclerView.setAdapter(mAdapter);

//        mConfirmBtn = (Button) findViewById(R.id.confirm);
//        mConfirmBtn.setText(getString(R.string.fragment_publish_photo_confirm,
//                mGalleryItems.size(), mMaxCount));
//        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mGalleryItems.size() == 0) {
//                    Toast.makeText(PhotoPickerActivity.this, R.string.toast_select_image_none, Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                // 完成选择
//                Intent intent = new Intent();
//                intent.putParcelableArrayListExtra(PARAM_PATH, mGalleryItems);
//                setResult(RESULT_OK, intent);
//                finish();
//            }
//        });

        // 申请访问存储权限
        requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Constant.PermissionRequestCode.PHOTO);
    }

    public boolean checkMaxCount() {
        if (mMaxCount == COUNT_INFINITE) {
            return true;
        }
        if (mSelectedCount != -1) {
            if (mSelectedCount >= mMaxCount) {
                CommonHelper.showToast(getString(R.string.tip_max_count, mMaxCount));
                return false;
            }
        } else {
            if (mGalleryItems.size() >= mMaxCount) {
                CommonHelper.showToast(getString(R.string.tip_max_count, mMaxCount));
                return false;
            }
        }
        return true;
    }

    public void tryOpenCamera() {
        // 申请摄像头权限
        requestPermission(Manifest.permission.CAMERA, Constant.PermissionRequestCode.CAMERA);
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "CoolScore");
        values.put(MediaStore.Images.Media.DESCRIPTION, "CoolScore Photo");
        values.put(MediaStore.Images.Media.MIME_TYPE, "images/jpeg");
        mPhotoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
        startActivityForResult(intent, PhotoPickerActivity.TAKE_PHOTO_REQUEST_CODE);
    }

    @Override
    protected void onPermissionGranted(int requestCode) {
        switch (requestCode) {
            case Constant.PermissionRequestCode.PHOTO:
                getSupportLoaderManager().initLoader(1, null, this);
                break;
            case Constant.PermissionRequestCode.CAMERA:
                openCamera();
                break;
        }
    }

    @Override
    protected void onPermissionDenied(int requestCode) {
        switch (requestCode) {
            case Constant.PermissionRequestCode.PHOTO:
                finish();
                break;
            case Constant.PermissionRequestCode.CAMERA:
                break;
        }
    }

    @Override
    protected void shouldShowRequestPermissionRationale(int requestCode) {
        super.shouldShowRequestPermissionRationale(requestCode);
        switch (requestCode) {
            case Constant.PermissionRequestCode.PHOTO:
                finish();
                break;
            case Constant.PermissionRequestCode.CAMERA:
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    public List<String> getSelectedImagePaths() {
        return mSelectedImagePath;
    }

    public void removeImageFromSelectedCollection(String removedPath) {
        if (TextUtils.isEmpty(removedPath)) {
            return;
        }
        Iterator<String> selectedItem = mSelectedImagePath.iterator();
        while (selectedItem.hasNext()) {
            String path = selectedItem.next();
            if (removedPath.equals(path)) {
                selectedItem.remove();
                break;
            }
        }
    }

    public void onImageSelected(final PhotoAdapter.ImageEntry entry) {
        if (entry == null) {
            return;
        }

        if (entry.add) {
            if (contains(entry)) {
                return;
            }

            Photo data = new Photo();
            data.path = entry.path;
            data.width = entry.width;
            data.height = entry.height;
            mGalleryItems.add(data);
            mSelectedImagePath.add(data.path);
            mSelectedCount++;
        } else {
            if (!contains(entry)) {
                return;
            }
            Iterator<Photo> iterator = mGalleryItems.iterator();
            while (iterator.hasNext()) {
                Photo p = iterator.next();
                if (!TextUtils.isEmpty(p.path) && p.path.equals(entry.path)) {
                    iterator.remove();
                    break;
                }
            }
            removeImageFromSelectedCollection(entry.path);
            mSelectedCount--;
        }
        invalidateOptionsMenu();
//        mConfirmBtn.setText(getString(R.string.fragment_publish_photo_confirm,
//                mGalleryItems.size(), mMaxCount));
    }

    private boolean contains(PhotoAdapter.ImageEntry entry) {
        if (entry == null) {
            return false;
        }
        for (Photo image : mGalleryItems) {
            if (image.path.equals(entry.path)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TAKE_PHOTO_REQUEST_CODE && resultCode == RESULT_OK) {
            if (mAdapter != null) {
                Uri uri = mPhotoUri;
                if (uri == null) {
                    return;
                }
                Bitmap thumbnail = MediaStore.Images.Thumbnails.getThumbnail(getContentResolver(),
                        ContentUris.parseId(uri), MediaStore.Images.Thumbnails.MINI_KIND, null);
                PhotoAdapter.ImageEntry entry = new PhotoAdapter.ImageEntry();
                entry.add = true;
                entry.bitmap = thumbnail;
                entry.position = 0;
                entry.folderName = LocalImageModel.ALL_IMAGE_NAME;
                entry.path = LocalImageModel.getImagePath(this, uri);
                // 相机拍照后暂时使用缩略图的宽高
                if (thumbnail != null) {
                    entry.width = thumbnail.getWidth();
                    entry.height = thumbnail.getHeight();
                }
                onImageSelected(entry);

                // 拍照结束后返回
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra(PARAM_PATH, mGalleryItems);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    @Override
    public Loader<Map<String, List<Photo>>> onCreateLoader(int id, Bundle args) {
        return new PhotoLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<Map<String, List<Photo>>> loader, Map<String, List<Photo>> data) {
        final List<String> folders = new ArrayList<>();
        for (String folderName : data.keySet()) {
            if (LocalImageModel.ALL_IMAGE_NAME.equals(folderName)) {
                continue;
            }
            folders.add(folderName);
        }
        // “全部图片放在首位”
        folders.add(0, LocalImageModel.ALL_IMAGE_NAME);

        SpinnerAdapter spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, folders);
        ActionBar.OnNavigationListener navigationListener = new ActionBar.OnNavigationListener() {
            @Override
            public boolean onNavigationItemSelected(int i, long l) {
                mAdapter.switchFolder(folders.get(i));
                return true;
            }
        };
        getSupportActionBar().setListNavigationCallbacks(spinnerAdapter, navigationListener);
        mAdapter.updateData(data);
    }

    @Override
    public void onLoaderReset(Loader<Map<String, List<Photo>>> loader) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGalleryItems.clear();
        mSelectedImagePath.clear();
        if (mAdapter != null) {
            mAdapter.clearCache();
        }
    }

    /**
     * 获取系统数据库中保存的所有图片
     * MediaStore.Images.Media.EXTERNAL_CONTENT_URI
     */
    private static class PhotoLoader extends AsyncTaskLoader<Map<String, List<Photo>>> {

        public PhotoLoader(Context context) {
            super(context);
            onContentChanged();
        }

        @Override
        public Map<String, List<Photo>> loadInBackground() {
            return LocalImageModel.getLocalImage(getContext());
        }

        @Override
        protected void onStartLoading() {
            if (takeContentChanged()) {
                forceLoad();
            }
        }

        @Override
        protected void onStopLoading() {
            cancelLoad();
        }
    }

}
