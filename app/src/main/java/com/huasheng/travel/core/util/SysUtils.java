package com.huasheng.travel.core.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.huasheng.travel.BaseApplication;
import com.huasheng.travel.core.constants.Pref;


/**
 * <br/>
 * 系统工具类. <br/>
 * 主要用于获取系统信息,如设备ID、操作系统版本等
 */
public class SysUtils {

    public static String FIRST_INIT_DATA_KEY = "first_init_data_key";
    private static String NEWLY_INSTALLED_KEY = "newly_installed_key";

    /**
     * 获取渠道号
     *
     * @return
     */
    public static String getChannelId() {
        return getMetaData("UMENG_CHANNEL");
    }

    /**
     * 获取AndroidManifest中定义的meta-data数据
     * xiug
     *
     * @param name
     * @return
     */
    public static String getMetaData(String name) {
        try {
            Context context = BaseApplication.getInstance();
            if (context == null) {
                return "";
            }
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            if (appInfo != null) {
                return appInfo.metaData.getString(name);
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取android id
     *
     * @return
     */
    public static String getAndroidId() {
        if (isEmulator(BaseApplication.getInstance())) {
            return "";
        }
        try {
            return Settings.Secure.getString(BaseApplication.getInstance().getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * 是否是模拟器
     *
     * @param context
     * @return
     */
    public static boolean isEmulator(Context context) {
        String imei = getIMEI(context);
        if ("000000000000000".equals(imei)) {
            return true;
        }
        return (Build.MODEL.equalsIgnoreCase("sdk")) || (Build.MODEL.equalsIgnoreCase("google_sdk")) || Build.BRAND.equalsIgnoreCase("generic");
    }

    /**
     * 当前是否横屏
     *
     * @param context
     * @return
     */
    public static boolean isLandscape(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    /**
     * 系统为Android5.0, API 21
     *
     * @return
     */
    public static boolean isLollipop() {
        // Can use static final com.huasheng.travel.core.constants like HONEYCOMB, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * 基于Android3.0的平板
     *
     * @param context
     * @return
     */
    public static boolean isHoneycombTablet(Context context) {
        return isHoneycomb() && isTablet(context);
    }

    /**
     * 系统为Android3.0
     *
     * @return
     */
    public static boolean isHoneycomb() {
        // Can use static final com.huasheng.travel.core.constants like HONEYCOMB, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    /**
     * 系统为Android2.1.x
     *
     * @return
     */
    public static boolean isEclair_MR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_MR1;
    }

    /**
     * 判断是否为平板设备
     *
     * @param context
     * @return
     */
    public static boolean isTablet(Context context) {
        //暂时屏蔽平板
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            return context.getResources().getConfiguration().smallestScreenWidthDp >= 600;
//    		return (context.getResources().getConfiguration().screenLayout
//                    & Configuration.SCREENLAYOUT_SIZE_MASK)
//                    >= Configuration.SCREENLAYOUT_SIZE_LARGE;
        }
        return false;
    }

    /**
     * 获取设备ID.
     *
     * @return
     */
    public static String getDeviceId() {
        String id = getIMEI(BaseApplication.getInstance());
        if (TextUtils.isEmpty(id)) {
            id = getAndroidId();
        }
        return id;
    }

    private static String getIMEI(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String imei = tm.getDeviceId();
            return imei;
        } catch (Exception ioe) {
        }
        return null;
    }

    /**
     * 获取设备名称.
     *
     * @return
     */
    public static String getBuildModel() {
        return Build.MODEL;
    }

    /**
     * 获取设备SDK版本号.
     *
     * @return
     */
    public static int getBuildVersionSDK() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * 获取设备系统版本号.
     *
     * @return
     */
    public static String getBuildVersionRelease() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 判断SD卡是否插入 即是否有SD卡
     */
    public static boolean isSDCardMounted() {
        return android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
                .getExternalStorageState());
    }

    /**
     * 是否：已经挂载,但只拥有可读权限
     */
    public static boolean isSDCardMountedReadOnly() {
        return android.os.Environment.MEDIA_MOUNTED_READ_ONLY.equals(android.os.Environment
                .getExternalStorageState());
    }

    /**
     * 获取android当前可用内存大小
     */
    public static String getAvailMemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo mi = new MemoryInfo();
        am.getMemoryInfo(mi);
        // mi.availMem; 当前系统的可用内存

        return Formatter.formatFileSize(context, mi.availMem);// 将获取的内存大小规格化
    }

    /**
     * 获取屏幕的亮度
     */
    public static int getScreenBrightness(Activity activity) {
        int nowBrightnessValue = 0;
        ContentResolver resolver = activity.getContentResolver();
        try {
            nowBrightnessValue = Settings.System.getInt(resolver,
                    Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nowBrightnessValue;
    }

    /**
     * 获取手机屏幕密度
     *
     * @param context
     * @return
     */
    public static float getScreenDensity(Context context) {
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        return dm.density;
    }

    /**
     * 获取手机屏幕的宽和高
     *
     * @param c
     * @return map("w", width) map("h",height);
     */
    public static HashMap<String, Integer> getWidth_Height(Context c) {
        DisplayMetrics metrics = c.getApplicationContext().getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        HashMap<String, Integer> m = new HashMap<String, Integer>();
        m.put("w", width);
        m.put("h", height);
        return m;
    }

    /**
     * 获取平板在横屏时webview的宽度
     *
     * @param c
     * @return
     */
    public static int getTabletWebViewWidth(Context c) {
        // 0.82f根据当前webview的padding计算得来
        return (int) ((float) SysUtils.getScreenWidth() * 0.82f / c.getResources().getDisplayMetrics().density);
    }

    /**
     * 获取手机屏幕的宽和高size wxh
     *
     * @param c
     * @return width X height
     */
    public static String getWidthXHeight(Context c) {
        Map<String, Integer> m = getWidth_Height(c);
        String size = m.get("w") + "x" + m.get("h");
        return size;
    }

    /**
     * 获取手机分辨率宽度大小
     *
     * @return
     */
    public static int getScreenWidth() {
        DisplayMetrics dm = BaseApplication.getInstance().getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 获取手机分辨率长度大小
     *
     * @return
     */
    public static int getScreenHeight() {
        DisplayMetrics dm = BaseApplication.getInstance().getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    /**
     * 4.0+获取虚拟导航高度
     *
     * @return
     */
    public static int getNavigationBarHeight() {
        Resources resources = BaseApplication.getInstance().getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    /**
     * 获取手机状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        Rect rect = new Rect();
        ((FragmentActivity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect.top;
    }

    /**
     * 获取ActionBar高度
     *
     * @param context
     * @return
     */
    public static int getActionBarSize(Context context) {
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        }
        return 0;
    }

    /**
     * 获取屏幕内容视图的高度（屏幕高度 - 系统状态栏高度 - 虚拟导航条高度）
     *
     * @return
     */
    public static int getScreenContentHeight(Context context) {
        int screenHeight = getScreenHeight();
        int statusBarHeight = getStatusBarHeight(context);
        int naviHeight = getNavigationBarHeight();
        return screenHeight - statusBarHeight - naviHeight;
    }

    /**
     * 获取应用窗口高度
     *
     * @param context
     * @return
     */
    public static int getAppWindowHeight(Context context) {
        Rect rect = new Rect();
        ((FragmentActivity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect.bottom - rect.top;
    }

    /**
     * 获得设备html宽度
     *
     * @param context
     * @return
     */
    public static int getDeviceHtmlWidth(Context context) {

        if (isHoneycombTablet(context) && isLandscape(context)) {
            return getTabletWebViewWidth(context);
        }

        int width = (int) (getScreenWidth() / context.getResources().getDisplayMetrics().density);

        return width;
    }

    /**
     * 得到dimen定义的大小
     *
     * @param context
     * @param dimenId
     * @return
     */
    public static int getDimension(Context context, int dimenId) {
        return (int) context.getResources().getDimension(dimenId);
    }

    public static boolean isNewlyInstalled() {
        return PrefHelper.getBoolean(NEWLY_INSTALLED_KEY, true);
    }

    public static void markNewlyInstalled() {
        PrefHelper.putBoolean(NEWLY_INSTALLED_KEY, false);
    }

    public static void openApp(Activity activity, String packageName, String pageName) {
        Intent intent = new Intent();
        ComponentName cmp = new ComponentName(packageName, pageName);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(cmp);
        activity.startActivityForResult(intent, 0);
    }

    /**
     * 判断应用是否安装
     *
     * @param context
     * @param appName
     * @return
     */
    public static boolean isAppInstalled(Context context, String appName) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(appName, 0);
            if (null != packageInfo) {
                return true;
            }
        } catch (NameNotFoundException e) {
        }
        return false;
    }

    /**
     * 检测支付宝是否安装
     *
     * @param context
     * @return
     */
    public static boolean checkAliPayInstalled(Context context) {
        Uri uri = Uri.parse("alipays://platformapi/startApp");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        ComponentName componentName = intent.resolveActivity(context.getPackageManager());
        return componentName != null;
    }

    /**
     * 是否第一次初始化数据
     */
    public static boolean firstInitColumnData() {
        return PrefHelper.getBoolean(FIRST_INIT_DATA_KEY, true);
    }


    /**
     * 返回应用版本号
     *
     * @return
     */
    public static String getAppVersion() {
        return getAppVersion(BaseApplication.getInstance().getPackageName());
    }

    /**
     * 返回应用版本号
     *
     * @return
     */
    public static int getAppVersionCode() {
        return getAppVersionCode(BaseApplication.getInstance().getPackageName());
    }

    public static String getLocalMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        String mac = info.getMacAddress();
        return TextUtils.isEmpty(mac) ? "" : mac;
    }

    /**
     * 根据packageName包名的应用获取应用版本名称,如未安装返回null
     *
     * @param packageName
     * @return
     */
    public static String getAppVersion(String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return null;
        }
        try {
            PackageInfo info = BaseApplication.getInstance().getPackageManager().getPackageInfo(packageName, 0);
            return info.versionName;
        } catch (NameNotFoundException e) {
            return null;
        }
    }

    /**
     * 根据packageName包名的应用获取应用版本名称,如未安装返回null
     *
     * @param packageName
     * @return
     */
    public static int getAppVersionCode(String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return 0;
        }
        try {
            PackageInfo info = BaseApplication.getInstance().getPackageManager().getPackageInfo(packageName, 0);
            return info.versionCode;
        } catch (NameNotFoundException e) {
            return 0;
        }
    }

    /**
     * 第一次启动某个Build版本
     *
     * @return
     */
    public static boolean firstLaunchVersion(boolean markImmediately) {
        String key = String.format(Locale.getDefault(), Pref.VERSION_LAUNCH_FIRST_TIME, getAppVersionCode());
        boolean result = PrefHelper.getBoolean(Pref.NAME_VERSION, key, true);
        if (result && markImmediately) {
            PrefHelper.putBoolean(Pref.NAME_VERSION, key, false);
        }
        return result;
    }

    /**
     * 根据packageName包名的应用获取应用信息,如未安装返回null
     *
     * @param context
     * @param packageName
     * @return
     */
    public static PackageInfo getAppInfo(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return null;
        }
        try {
            return context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (NameNotFoundException e) {
            return null;
        }
    }

    public static boolean shouldOpenApp(Context context) {
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<RunningTaskInfo> tasksInfoList = am.getRunningTasks(1);

            if (tasksInfoList == null || tasksInfoList.size() == 0) {
                return false;
            }

            String appName = context.getPackageName();
            RunningTaskInfo taskInfo = tasksInfoList.get(0);
            if (taskInfo.numActivities == 1 || !appName.equals(taskInfo.baseActivity.getPackageName())) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 判断是不是合法时间
     *
     * @return
     */
    public static boolean isValidTime(String startTime, String endTime) {
        return isValidTime("yyyy-MM-dd HH:mm:ss", "Asia/Shanghai", startTime, endTime);
    }

    /**
     * 判断是不是合法时间
     *
     * @return
     */
    public static boolean isValidTime(String format, String timeZone, String startTime, String endTime) {
        try {
            SimpleDateFormat df = new SimpleDateFormat(format);
            TimeZone timeZoneshanghai = TimeZone.getTimeZone(timeZone);
            df.setTimeZone(timeZoneshanghai);

            Date startDate = df.parse(startTime);
            Date endDate = df.parse(endTime);
            long start = startDate.getTime();
            long end = endDate.getTime();

            long now = System.currentTimeMillis();
            if (now > start && now < end) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 显示键盘
     *
     * @param view
     */
    public static void showKeyBoard(View view) {
        if (view == null) {
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager) BaseApplication.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager == null) {
            return;
        }
        view.requestFocus();
        inputMethodManager.showSoftInput(view, 0);
    }

    /**
     * 隐藏键盘
     *
     * @param windowToken
     */
    public static void hideKeyBoard(IBinder windowToken) {
        InputMethodManager inputMethodManager = (InputMethodManager) BaseApplication.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager == null || !inputMethodManager.isActive()) {
            return;
        }
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0);
    }

    /**
     * 复制内容到系统剪切板
     *
     * @param context
     * @param text
     */
    public static void setTextToClipboard(Context context, String text) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager == null) {
            return;
        }
        ClipData data = ClipData.newPlainText("text", text);
        clipboardManager.setPrimaryClip(data);
    }

    public static String getClipboardText() {
        ClipboardManager clipboardManager = (ClipboardManager) BaseApplication.getInstance().getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager == null) {
            return "";
        }
        ClipData clipData = clipboardManager.getPrimaryClip();
        if (clipData == null || clipData.getItemCount() < 1) {
            return "";
        }
        return clipData.getItemAt(0).coerceToText(BaseApplication.getInstance()).toString();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void setImmersiveMode(boolean enable, View view){
        if(view == null){
            return;
        }
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT){
            return;
        }
        if (enable) {
            view.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
        } else {
            view.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }
}
