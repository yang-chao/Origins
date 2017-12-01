package com.huasheng.travel;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.multidex.MultiDexApplication;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.huasheng.travel.core.util.NetUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import static com.huasheng.travel.core.util.LogUtils.LOGD;


/**
 * Created by YangChao on 15-12-15 上午10:29.
 */
public class BaseApplication extends MultiDexApplication implements Application.ActivityLifecycleCallbacks,
        Thread.UncaughtExceptionHandler {
    private static final String TAG = "BaseApplication";
    private static BaseApplication sApplication;
    private static boolean mNoImageMode = false;

    public static BaseApplication getInstance() {
        return sApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;

        // Fresco
        ImagePipelineConfig.Builder config = ImagePipelineConfig.newBuilder(this);
        config.setDownsampleEnabled(true);
        Fresco.initialize(this, config.build());

        // 友盟统计
//        MobclickAgent.setDebugMode(true);
        MobclickAgent.openActivityDurationTrack(false);
        registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        unregisterActivityLifecycleCallbacks(this);
    }

    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();

        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        LOGD(TAG, "onActivityCreated - " + activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        LOGD(TAG, "onActivityStarted - " + activity);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        LOGD(TAG, "onActivityResumed - " + activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        LOGD(TAG, "onActivityPaused - " + activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        LOGD(TAG, "onActivityStopped - " + activity);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        LOGD(TAG, "onActivitySaveInstanceState - " + activity);
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        LOGD(TAG, "onActivityDestroyed - " + activity);
    }

    public static boolean isNoImageMode() {
        return mNoImageMode && !NetUtils.isWifi();
    }

    public static void setNoImageMode(boolean noImageMode) {
        mNoImageMode = noImageMode;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {

    }
}
