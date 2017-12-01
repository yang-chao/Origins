package com.huasheng.travel.core.constants;

/**
 * Prefs里保存所有关于SharedPreferences的字符串常量。
 * 请以NAME_或者KEY_开头。
 * <p/>
 * Created by YangChao on 15-11-4 下午4:49.
 */
public class Pref {
    /************************ PREF NAME *************************/
    /**
     * 刷新管理
     */
    public static final String NAME_REFRESH = "refresh";
    /**
     * 用户管理
     */
    public static final String NAME_ACCOUNT = "account";
    /**
     * 版本管理
     */
    public static final String NAME_VERSION = "version";
    /**
     * 收藏列表
     */
    public static final String FAVORITE = "favorite";
    /**
     * 文章、帖子点赞记录
     */
    public static final String NAME_LIKE = "likeState";
    /**
     * 文章、帖子已读记录
     */
    public static final String NAME_READ = "readState";

    /************************ PREF KEY *************************/
    // 设置相关
    /**
     * 无图模式
     */
    public static final String SETTING_NO_IMAGE = "setting_no_image";
    /**
     * 夜间模式
     */
    public static final String SETTING_NIGHT_MODE = "setting_night_mode";
    /**
     * Push开关-新闻
     */
    public static final String SETTING_PUSH_NEWS = "setting_push_news";
    /**
     * 用户对象数据
     */
    public static final String ACCOUNT_OBJECT = "object";
    /**
     * 第一次启动某个Build版本
     */
    public static final String VERSION_LAUNCH_FIRST_TIME = "launch_first_time_%d";

    public static final String NOTICE_BABY_ID = "notice_baby_id";
    public static final String NOTICE_BABY_NAME = "notice_baby_name";
    public static final String NOTICE_COMMENT = "notice_comment";
    public static final String NOTICE_LIKE = "notice_like";
    public static final String NOTICE_FOLLOW = "notice_follow";
    public static final String NOTICE_HX = "notice_hx";
    public static final String NOTICE_AT = "notice_at";
    public static final String NOTICE_MSG_LOTTERY = "notice_msg_lottery";
    public static final String NOTICE_MSG_INVITE = "notice_msg_invite";

    /**
     * 是否需要引导用户到设置页面开启权限
     */
    public static final String PERMISSION_LIVE_SHOULD_GUIDE_USER = "permission_live_should_guide_user";
    /**
     * 收藏的文章ID
     */
    public static final String FAVORITE_ITEMS = "favorite_items";
    /**
     * 新用户获取积分请求检查
     */
    public static final String POINT_NEW_USER_CHECKED = "POINT_NEW_USER_CHECKED";
}
