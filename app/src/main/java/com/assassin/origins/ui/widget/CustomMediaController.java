package com.assassin.origins.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import java.util.Formatter;
import java.util.Locale;

import com.assassin.origins.R;

public class CustomMediaController extends FrameLayout {

    private static final int DEFAUL_TTIME_OUT = 3000;
    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;

    private Context mContext;
    private MediaPlayerControl mPlayer;
    private AudioManager mAM;
    private PopupWindow mWindow;
    private View mAnchor;
    private View mRoot;

    //View
    private ImageView mPauseButton;
    private ImageView mExpandButton;
    private ImageView mShrinkButton;
    private TextView mTime;
    private TextView mPlayedTime;
    private ProgressBar mProgress;
    private int mAnimStyle;

    //时间及进度
    private long mDuration;
    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;

    //控制显示等变量
    private boolean mFromXml = false;
    private boolean mShowing;
    private boolean mDragging;
    private boolean mInstantSeeking = true;
    private boolean mFullScreen = false;
    private boolean mForceFullScreen = false;
    private boolean mUsedInList = false;

    private boolean mShowExtraButton;
    private boolean mShowShrink = false;
    private OnExpandShrinkListener mExpandShrinkListener;
    /**
     * 增加判断是否是视频直播
     */
    private boolean mLiveVideo = false;

    private OnExtraViewEventListener mOnExtraViewListener;
    private OnShownListener mShownListener;
    private OnHiddenListener mHiddenListener;
    private OnPauseResumeListener mOnPauseResumeListener;
    private OnClickListener mPauseListener = new OnClickListener() {
        public void onClick(View v) {
            doPauseResume();
            show(DEFAUL_TTIME_OUT);
        }
    };
    private OnClickListener mExpandListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            expand();
        }
    };

    private OnClickListener mShrinkListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            shrink();
        }
    };

    private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
        public void onStartTrackingTouch(SeekBar bar) {
            mDragging = true;
            show(3600000);
            mHandler.removeMessages(SHOW_PROGRESS);
            if (mInstantSeeking)
                mAM.setStreamMute(AudioManager.STREAM_MUSIC, true);
        }

        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (!fromuser || mPlayer == null) {
                return;
            }

            long duration = mPlayer.getDuration();
            long newposition = (duration * progress) / 1000L;
            mPlayer.seekTo((int) newposition);
            if (mInstantSeeking) {
                mPlayer.seekTo(newposition);
            }
            if (mTime != null) {
                mTime.setText(stringForTime(mDuration));
            }
            if (mPlayedTime != null) {
                mPlayedTime.setText(stringForTime(mPlayer.getCurrentPosition()));
            }
        }

        public void onStopTrackingTouch(SeekBar bar) {
            if (!mInstantSeeking)
                mPlayer.seekTo((mDuration * bar.getProgress()) / 1000);
            show(DEFAUL_TTIME_OUT);
            mHandler.removeMessages(SHOW_PROGRESS);
            mAM.setStreamMute(AudioManager.STREAM_MUSIC, false);
            mDragging = false;
            mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            long pos;
            switch (msg.what) {
                case FADE_OUT:
                    hide();
                    break;
                case SHOW_PROGRESS:
                    pos = setProgress();
                    if (!mDragging && mShowing) {
                        msg = obtainMessage(SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                        updatePausePlay();
                    }
                    break;
            }
        }
    };

    public CustomMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRoot = this;
        mFromXml = true;
        initController(context);
    }

    public CustomMediaController(Context context, boolean configFullScreen) {
        super(context);
        if (!mFromXml && initController(context)) {
            initFloatingWindow();
        }
        mForceFullScreen = configFullScreen;
        mFullScreen = mForceFullScreen;
    }

    public CustomMediaController(Context context, boolean configFullScreen, boolean usedInList) {
        super(context);
        if (!mFromXml && initController(context)) {
            initFloatingWindow();
        }
        mForceFullScreen = configFullScreen;
        mFullScreen = mForceFullScreen;
        mUsedInList = usedInList;
    }

    private boolean initController(Context context) {
        mContext = context;
        mAM = (AudioManager) mContext.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        return true;
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        if (mRoot != null)
            initControllerView(mRoot);
    }

    private void initFloatingWindow() {
        mWindow = new PopupWindow(mContext);
        mWindow.setFocusable(false);
        mWindow.setBackgroundDrawable(null);
        mWindow.setOutsideTouchable(true);
        mAnimStyle = android.R.style.Animation;
    }

    private boolean mHasInflated;

    /**
     * Set the view that acts as the anchor for the control view. This can for
     * example be a VideoView, or your Activity's main view.
     *
     * @param view The view to which to anchor the controller when it is visible.
     */
    public void setAnchorView(View view) {
        mAnchor = view;
        if (!mHasInflated) {
            removeAllViews();
            mRoot = makeControllerView();
            mHasInflated = true;
        }
        if (!mFromXml) {
            mWindow.setContentView(mRoot);
            mWindow.setWidth(LayoutParams.MATCH_PARENT);
            mWindow.setHeight(LayoutParams.WRAP_CONTENT);
        }
        initControllerView(mRoot);
    }

    /**
     * Create the view that holds the widgets that control playback. Derived
     * classes can override this to create their own.
     *
     * @return The controller view.
     */
    protected View makeControllerView() {
        return ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.widget_video_media_controller, this);
    }

    private void initControllerView(View v) {

        //播放暂停按钮
        mPauseButton = (ImageView) v.findViewById(R.id.pause);
        if (mPauseButton != null) {
            mPauseButton.requestFocus();
            mPauseButton.setOnClickListener(mPauseListener);
        }

        //全屏播放
        mExpandButton = (ImageView) v.findViewById(R.id.expand);
        if (mExpandButton != null) {
            mExpandButton.setOnClickListener(mExpandListener);
            mExpandButton.setVisibility(mForceFullScreen || mFullScreen ? View.GONE : View.VISIBLE);
        }

        //全屏缩回
        mShrinkButton = (ImageView) v.findViewById(R.id.shrink);
        if (mShrinkButton != null) {
            mShrinkButton.setOnClickListener(mShrinkListener);
            mShrinkButton.setVisibility(((!mForceFullScreen && mFullScreen) || mShowShrink) ? View.VISIBLE : View.GONE);
        }

        //进度
        mProgress = (SeekBar) v.findViewById(R.id.mediacontroller_progress);
        if (mProgress != null) {
            if (mProgress instanceof SeekBar) {
                SeekBar seeker = (SeekBar) mProgress;
                seeker.setOnSeekBarChangeListener(mSeekListener);
            }
            mProgress.setMax(1000);
        }

        //时间相关
        mTime = (TextView) v.findViewById(R.id.time);
        mPlayedTime = (TextView) v.findViewById(R.id.played_time);
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

        //如果是视频直播，重新设置control（1.隐藏seekBar，2.添加最小化按钮）
        if (mLiveVideo) {
            mProgress.setVisibility(GONE);
            mTime.setVisibility(GONE);
            mPlayedTime.setVisibility(GONE);
        }

        if (mUsedInList) {
            mPauseButton.setVisibility(GONE);
            mTime.setVisibility(VISIBLE);
            mProgress.setVisibility(VISIBLE);
            mPlayedTime.setVisibility(VISIBLE);
        }
    }

    public void setMediaPlayer(MediaPlayerControl player) {
        mPlayer = player;
        updatePausePlay();
    }

    public void setOnShownListener(OnShownListener l) {
        mShownListener = l;
    }

    public void setOnHiddenListener(OnHiddenListener l) {
        mHiddenListener = l;
    }

    public void setOnExtraViewListener(OnExtraViewEventListener listener) {
        mOnExtraViewListener = listener;
    }

    public void setExpandListener(OnClickListener listener) {
        mExpandListener = listener;
    }

    public void setShrinkListener(OnClickListener listener) {
        mShrinkListener = listener;
    }

    public void setOnPauseResumeListener(OnPauseResumeListener onPauseResumeListener) {
        mOnPauseResumeListener = onPauseResumeListener;
    }

    public void setOnExpandShrinkListener(OnExpandShrinkListener listener) {
        mExpandShrinkListener = listener;
    }

    public void setShowShrink(boolean show) {
        mShowShrink = show;
    }

    public void setShowExtraButton(boolean show) {
        mShowExtraButton = show;
    }

    private boolean isFirstCalculate = true;

    public void show() {
        show(DEFAUL_TTIME_OUT);
    }

    /**
     * Show the controller on screen. It will go away automatically after
     * 'timeout' milliseconds of inactivity.
     *
     * @param timeout The timeout in milliseconds. Use 0 to show the controller
     *                until hide() is called.
     */
    public void show(int timeout) {
        if (!mShowing && mAnchor != null && mAnchor.getWindowToken() != null) {
            if (mPauseButton != null) {
                mPauseButton.requestFocus();
            }
            disableUnsupportedButtons();

            if (mFromXml) {
                setVisibility(View.VISIBLE);
            } else {
                int[] location = new int[2];

                // we need to know the size of the controller so we can properly position it
                // within its space
                if (isFirstCalculate) {
                    mRoot.measure(MeasureSpec.makeMeasureSpec(mAnchor.getWidth(), MeasureSpec.AT_MOST),
                            MeasureSpec.makeMeasureSpec(mAnchor.getHeight(), MeasureSpec.AT_MOST));
                    isFirstCalculate = false;
                }

                mAnchor.getLocationOnScreen(location);
                Rect anchorRect = new Rect(location[0], location[1], location[0] + mAnchor.getWidth(), location[1] + mAnchor.getHeight() - mRoot.getMeasuredHeight());

                mWindow.setAnimationStyle(mAnimStyle);
                mWindow.showAtLocation(mAnchor, Gravity.NO_GRAVITY, anchorRect.left, anchorRect.bottom);
            }
            mShowing = true;
            if (mShownListener != null)
                mShownListener.onShown(mWindow);

        }
        updatePausePlay();
        mHandler.sendEmptyMessage(SHOW_PROGRESS);

        if (timeout != 0) {
            mHandler.removeMessages(FADE_OUT);
            mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_OUT), timeout);
        }
    }

    public boolean isShowing() {
        return mShowing;
    }

    public void hide() {
        if (mAnchor == null)
            return;

        if (mShowing) {
            try {
                mHandler.removeMessages(SHOW_PROGRESS);
                if (mFromXml)
                    setVisibility(View.GONE);
                else
                    mWindow.dismiss();
            } catch (IllegalArgumentException ex) {
                //Log.d("MediaController already removed");
            }
            mShowing = false;
            if (mHiddenListener != null)
                mHiddenListener.onHidden();
        }
    }

    public void expand() {
        if (mFullScreen) {
            return;
        }
        Activity activity = ((Activity) mContext);
        if (activity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            hide();
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mFullScreen = true;
            updateExpandShrink();
            updateFullscreenStatus(true);
            if (activity instanceof AppCompatActivity) {
                ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
                if (actionBar != null) {
                    actionBar.hide();
                }
            }
            if (mExpandShrinkListener != null) {
                mExpandShrinkListener.onExpand();
            }
        }
    }

    public void shrink() {
        if (!mFullScreen) {
            return;
        }
        Activity activity = ((Activity) mContext);
        if (activity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            hide();
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mFullScreen = false;
            updateExpandShrink();
            updateFullscreenStatus(false);
            if (activity instanceof AppCompatActivity) {
                ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
                if (actionBar != null) {
                    actionBar.show();
                }
            }
            if (mExpandShrinkListener != null) {
                mExpandShrinkListener.onShrink();
            }
        }
    }

    private void updateFullscreenStatus(boolean bUseFullscreen) {
        if (bUseFullscreen) {
            ((Activity) mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            ((Activity) mContext).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        } else {
            ((Activity) mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            ((Activity) mContext).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    private void disableUnsupportedButtons() {
        try {
            if (mPauseButton != null && mPlayer != null && !mPlayer.canPause()) {
                mPauseButton.setEnabled(false);
            }
        } catch (IncompatibleClassChangeError ex) {
        }
    }


    public boolean isFullscreen() {
        return mFullScreen;
    }

    private long setProgress() {
        if (mPlayer == null || mDragging)
            return 0;

        long position = mPlayer.getCurrentPosition();
        long duration = mPlayer.getDuration();
        if (mProgress != null) {
            if (duration > 0) {
                long pos = 1000L * position / duration;
                mProgress.setProgress((int) pos);
            }
            int percent = mPlayer.getBufferPercentage();
            mProgress.setSecondaryProgress(percent * 10);
        }

        mDuration = duration;

        if (mTime != null) {
            mTime.setText(stringForTime(mDuration));
        }
        if (mPlayedTime != null) {
            mPlayedTime.setText(stringForTime(position));
        }

        return position;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        show(DEFAUL_TTIME_OUT);
        return true;
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        show(DEFAUL_TTIME_OUT);
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (event.getRepeatCount() == 0 && (keyCode == KeyEvent.KEYCODE_HEADSETHOOK || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || keyCode == KeyEvent.KEYCODE_SPACE)) {
            doPauseResume();
            show(DEFAUL_TTIME_OUT);
            if (mPauseButton != null) {
                mPauseButton.requestFocus();
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP) {
            if (mPlayer.isPlaying()) {
                mPlayer.pause();
                updatePausePlay();
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU) {
            hide();
            return true;
        } else {
            show(DEFAUL_TTIME_OUT);
        }
        return super.dispatchKeyEvent(event);
    }

    private void updateExpandShrink() {
        if (mRoot == null || mExpandButton == null || mShrinkButton == null
                || mPauseButton == null) {
            return;
        }

        if (mFullScreen) {
            mExpandButton.setVisibility(View.GONE);
            mShrinkButton.setVisibility(View.VISIBLE);
        } else {
            mExpandButton.setVisibility(View.VISIBLE);
            mShrinkButton.setVisibility(View.GONE);
        }
    }

    private void updatePausePlay() {
        if (mPlayer == null || mRoot == null || mPauseButton == null)
            return;

        if (mPlayer.isPlaying()) {
            mPauseButton.setImageResource(R.drawable.ic_video_pause);
        } else {
            mPauseButton.setImageResource(R.drawable.ic_video_play_smalll);
        }
    }

    public void doPauseResume() {
        if (mPlayer == null) {
            return;
        }

        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        } else {
            mPlayer.start();
        }
        updatePausePlay();

        if (mOnPauseResumeListener != null) {
            mOnPauseResumeListener.onPauseResume(mPlayer.isPlaying());
        }
    }

    private String stringForTime(long timeMs) {
        int totalSeconds = (int) (timeMs / 1000);

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    /**
     * 设置是否为视频直播流
     */
    public void setLive(boolean isVisible) {
        mLiveVideo = isVisible;
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (mPauseButton != null) {
            mPauseButton.setEnabled(enabled);
        }
        if (mProgress != null)
            mProgress.setEnabled(enabled);
        disableUnsupportedButtons();
        super.setEnabled(enabled);
    }

    public interface MediaPlayerControl {
        void start();

        void pause();

        int getDuration();

        int getCurrentPosition();

        void seekTo(int pos);

        void seekTo(long pos);

        boolean isPlaying();

        int getBufferPercentage();

        boolean canPause();

        boolean canSeekBackward();

        boolean canSeekForward();
    }

    public interface OnShownListener {
        void onShown(PopupWindow controlWindow);
    }

    public interface OnHiddenListener {
        void onHidden();
    }

    public interface OnPauseResumeListener {
        void onPauseResume(boolean isPlaying);
    }

    public interface OnExtraViewEventListener {
        void onExtraViewClick(View v);

        void onExtraViewCheckedChange(CompoundButton v, boolean isChecked);
    }

    public interface OnExpandShrinkListener {
        void onExpand();

        void onShrink();
    }
}
