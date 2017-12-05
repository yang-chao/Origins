package com.assassin.origins.ui.common;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import com.assassin.origins.BaseApplication;
import com.assassin.origins.R;
import com.assassin.origins.core.constants.Constant;
import com.assassin.origins.core.model.CommonHelper;
import com.assassin.origins.core.util.SysUtils;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.assassin.origins.core.util.LogUtils.LOGD;

/**
 * Created by YC on 15-6-8.
 */
public class ImageActivity extends BaseActivity {
    private static final String TAG = "ImageActivity";
    public static final String PARAM_URLS = "param_urls";
    public static final String PARAM_POS = "param_pos";
    public static final String PARAM_HIDE_INDICATOR = "param_hide_indicator";

    private ArrayList<String> mUrls;
    private ImageHandler mImageHandler;
    private ViewPager mViewPager;
    private TextView mCounterView;

    private long mDownloadId;
    private DownloadManager mDownloadManager;

    private int mScreenWidth;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                if (mDownloadId == downloadId) {
                    Uri uri = mDownloadManager.getUriForDownloadedFile(downloadId);
                    if (uri != null) {
                        CommonHelper.showToast(getString(R.string.save_to,
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                                        + File.separator + getString(R.string.app_name)
                                        + File.separator + uri.getLastPathSegment()), Toast.LENGTH_LONG);
                    }
                }
            }
        }
    };

    @Override
    protected void initActionBar() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        mUrls = getIntent().getStringArrayListExtra(PARAM_URLS);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mCounterView = (TextView) findViewById(R.id.counter);
        if (getIntent().getBooleanExtra(PARAM_HIDE_INDICATOR, false)) {
            mCounterView.setVisibility(View.GONE);
        }
        PagerAdapter adapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return mUrls == null ? 0 : mUrls.size();
            }

            @Override
            public Object instantiateItem(ViewGroup container, final int position) {
                String url = mUrls.get(position);
                if (url != null && url.toLowerCase().contains(".gif")) {
                    mImageHandler = new SimpleDraweeViewImageHandler(new SimpleDraweeView(ImageActivity.this), url);
                } else {
                    mImageHandler = new PhotoViewImageHandler(new PhotoView(ImageActivity.this), url);
                }
                ImageView imageView = mImageHandler.loadImage();
                mImageHandler.prepareClick();
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT);
                container.addView(imageView, params);
                return imageView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                if (object == null) {
                    return;
                }
                ImageView imageView = (ImageView) object;
                imageView.setImageBitmap(null);
                container.removeView((View) object);
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        };
        mViewPager.setAdapter(adapter);
        int position = getIntent().getIntExtra(PARAM_POS, 0);
        mCounterView.setText(position + 1 + " / " + mUrls.size());
        mViewPager.setCurrentItem(position);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (mUrls != null) {
                    mCounterView.setText(position + 1 + " / " + mUrls.size());
                }
            }
        });

        // 初始化下载相关
        mDownloadManager = (DownloadManager) BaseApplication.getInstance().getSystemService(Context.DOWNLOAD_SERVICE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(mReceiver, filter);

        mScreenWidth = SysUtils.getScreenWidth();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constant.PermissionRequestCode.PHOTO: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    downloadAction();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
            }
        }
    }

    public void onDownloadAction(View view) {
        if (ContextCompat.checkSelfPermission(BaseApplication.getInstance(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                CommonHelper.showToast(R.string.permission_tip);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.PermissionRequestCode.PHOTO);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            downloadAction();
        }
    }

    private void downloadAction() {
        int pos = mViewPager.getCurrentItem();
        if (mUrls == null || pos < 0 || pos >= mUrls.size()) {
            return;
        }
        final String url = mUrls.get(pos);
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (url.contains(".gif")) {
            download(url);
        } else {
            ImageRequest request = ImageRequest.fromUri(url);
            ImagePipeline imagePipeline = Fresco.getImagePipeline();
            DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(request,
                    BaseApplication.getInstance());
            BaseBitmapDataSubscriber dataSubscriber = new BaseBitmapDataSubscriber() {
                @Override
                protected void onNewResultImpl(Bitmap bitmap) { // onNewResultImpl方法是在子线程调用的
                    FileOutputStream out;
                    try {
                        final String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                                + File.separator + getString(R.string.app_name);
                        File directory = new File(path);
                        if (!directory.exists()) {
                            directory.mkdirs();
                        }
                        String fileName = Uri.parse(url).getLastPathSegment();
                        if (!fileName.toLowerCase().endsWith(".png") && !fileName.toLowerCase().endsWith(".jpg")) {
                            fileName = fileName + ".jpg";
                        }
                        final File outputFile = new File(path, fileName);
                        if (!outputFile.exists()) {
                            out = new FileOutputStream(outputFile);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            out.flush();
                            out.close();
                        }
                        ImageActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CommonHelper.showToast(getString(R.string.save_to, path), Toast.LENGTH_LONG);
                                MediaScannerConnection.scanFile(BaseApplication.getInstance(), new String[]{"" + outputFile}, null, null);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                    ImageActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonHelper.showToast(R.string.image_decode_failed);
                        }
                    });
                }
            };
            dataSource.subscribe(dataSubscriber, CallerThreadExecutor.getInstance());
        }
    }

    private void download(String url) {
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES,
                getString(R.string.app_name) + File.separator + uri.getLastPathSegment());
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

        try {
            mDownloadId = mDownloadManager.enqueue(request);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            startDownloadManagerInfoActivity(this);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    /**
     * 进入DownloadManager设置页面
     *
     * @param context
     */
    private void startDownloadManagerInfoActivity(Context context) {
        Toast.makeText(context, R.string.update_downloader_unable, Toast.LENGTH_SHORT).show();
        try {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", "com.android.providers.downloads", null);
            intent.setData(uri);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static abstract class ImageHandler {
        String mUrl;

        ImageHandler(String url) {
            this.mUrl = url;
        }

        abstract ImageView loadImage();

        abstract void prepareClick();
    }

    private class SimpleDraweeViewImageHandler extends ImageHandler {

        private SimpleDraweeView mImageView;

        SimpleDraweeViewImageHandler(SimpleDraweeView imageView, String url) {
            super(url);
            mImageView = imageView;
        }

        @Override
        ImageView loadImage() {
            mImageView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setUri(Uri.parse(mUrl))
                    .setAutoPlayAnimations(true)
                    .build();
            mImageView.setController(controller);
            return mImageView;
        }

        @Override
        void prepareClick() {
            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    private class PhotoViewImageHandler extends ImageHandler {
        private PhotoView mImageView;

        PhotoViewImageHandler(PhotoView imageView, String url) {
            super(url);
            mImageView = imageView;
        }

        @Override
        ImageView loadImage() {
            ImagePipeline imagePipeline = Fresco.getImagePipeline();
            BaseBitmapDataSubscriber dataSubscriber = new BaseBitmapDataSubscriber() {
                @Override
                protected void onNewResultImpl(Bitmap bitmap) {
                    if (bitmap == null) {
                        return;
                    }
                    try {
                        final Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, mScreenWidth,
                                (int) (bitmap.getHeight() * mScreenWidth / (float) bitmap.getWidth()), true);
                        if (newBitmap != null) {
                            mImageView.post(new Runnable() {
                                @Override
                                public void run() {
                                    mImageView.setImageBitmap(newBitmap);
                                }
                            });
                        }
                    } catch (OutOfMemoryError e) {
                        e.printStackTrace();
                    }
                }

                @Override
                protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                    LOGD(TAG, "onFailureImpl");
                    mImageView.post(new Runnable() {
                        @Override
                        public void run() {
                            CommonHelper.showToast(R.string.image_decode_failed);
                        }
                    });
                }
            };
            ImageRequest imageRequest = ImageRequest.fromUri(mUrl);
            DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(imageRequest, ImageActivity.this);
            dataSource.subscribe(dataSubscriber, CallerThreadExecutor.getInstance());
            return mImageView;
        }

        @Override
        void prepareClick() {
            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            mImageView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    finish();
                }
            });
        }
    }
}
