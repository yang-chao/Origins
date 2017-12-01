package com.huasheng.travel.ui.common;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.huasheng.travel.BaseApplication;
import com.huasheng.travel.R;
import com.huasheng.travel.core.util.SysUtils;
import com.huasheng.travel.ui.MainActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

/**
 * Created by YangChao on 15-12-17 上午10:39.
 */
public class BaseActivity extends AppCompatActivity {
    public static final String PARAM_DO_NOT_START_APP = "param_do_not_start_app";
    private boolean isStart = false;
    private int mRequestCode;
    private boolean mHasActionBar = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected boolean enableTheme() {
        return true;
    }

    protected void setHasActionBar(boolean hasActionBar) {
        mHasActionBar = hasActionBar;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isStart) {
            isStart = true;
            if (mHasActionBar) {
                initActionBar();
            }
        }
    }

    @Override
    public void finish() {
        Intent intent = getIntent();
        if (intent != null && !intent.getBooleanExtra(PARAM_DO_NOT_START_APP, false) &&
                SysUtils.shouldOpenApp(this) && !(this instanceof MainActivity)) {
            startActivity(new Intent(this, MainActivity.class));
        }
        super.finish();
    }

    /**
     * 友盟统计使用，自定义当前页面的名称
     *
     * @return
     */
    protected String getPageName() {
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(getPageName())) {
            MobclickAgent.onPageStart(getPageName());
        }
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!TextUtils.isEmpty(getPageName())) {
            MobclickAgent.onPageEnd(getPageName());
        }
        MobclickAgent.onPause(this);
    }

    /**
     * 默认添加Toolbar，子类可覆盖本方法
     */
    protected void initActionBar() {
        View root = findViewById(android.R.id.content);
        ViewGroup parent = (ViewGroup) root.getParent();
        Toolbar toolbar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.base_toolbar, null);
        parent.addView(toolbar, 0);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeButtonEnabled(true);
        }

        final GestureDetectorCompat gestureDetector = new GestureDetectorCompat(this,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
//                        EventBus.getDefault().post(new ToolbarEvent());
                        return super.onDoubleTap(e);
                    }
                });
        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };
        toolbar.setOnTouchListener(onTouchListener);
    }

    public Toolbar getToolbar() {
        return (Toolbar) findViewById(R.id.toolbar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment f : fragments) {
                if (f instanceof BaseFragment && f.isVisible()) {
                    if (((BaseFragment) f).onBackPressed()) {
                        return;
                    }
                }
            }
        }
        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == mRequestCode) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(requestCode);
            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                onPermissionDenied(requestCode);
            }
        }
    }

    /**
     * 请求权限
     *
     * @param permission
     * @param requestCode
     */
    protected void requestPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(BaseApplication.getInstance(), permission)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                shouldShowRequestPermissionRationale(requestCode);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{permission}, requestCode);
                mRequestCode = requestCode;
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            onPermissionGranted(requestCode);
        }
    }

    /**
     * 权限获得允许
     */
    protected void onPermissionGranted(int requestCode) {

    }

    /**
     * 请求权限被拒绝
     */
    protected void onPermissionDenied(int requestCode) {

    }

    protected void shouldShowRequestPermissionRationale(int requestCode) {
        Toast.makeText(this, R.string.permission_tip, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
