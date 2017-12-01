package com.huasheng.travel.ui.common;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.huasheng.travel.R;
import com.huasheng.travel.ui.widget.CustomMediaController;
import com.huasheng.travel.ui.widget.MyVideoView;


/**
 * Created by yc-mac on 2016/11/21.
 */

public class SingleFragmentActivity extends BaseActivity {

    public static final String PARAM_FRAGMENT_TITLE = "param_fragment_title";
    public static final String PARAM_FRAGMENT_NAME = "param_fragment_name";
    public static final String PARAM_FRAGMENT_TAG = "param_fragment_tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_activity);

        String fragmentName = getIntent().getStringExtra(PARAM_FRAGMENT_NAME);
        String tag = getIntent().getStringExtra(PARAM_FRAGMENT_TAG);
        Bundle args = getIntent().getExtras();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragment_container, Fragment.instantiate(this, fragmentName, args), tag);
        ft.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        String title = getIntent().getStringExtra(PARAM_FRAGMENT_TITLE);
        if (title != null) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(title);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        FrameLayout fullscreenVideoContainer = (FrameLayout) findViewById(R.id.fullscreen_video_container);
        if (fullscreenVideoContainer != null && fullscreenVideoContainer.getChildCount() > 0) {
            View view = fullscreenVideoContainer.getChildAt(0);
            if (view instanceof MyVideoView) {
                CustomMediaController mediaController = ((MyVideoView) view).getMediaController();
                if (mediaController != null && mediaController.isFullscreen()) {
                    mediaController.shrink();
                    fullscreenVideoContainer.setVisibility(View.GONE);
                    return true;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
