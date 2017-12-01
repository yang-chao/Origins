package com.huasheng.travel.ui.common;

/**
 * 请求类型页面 UI 相关。
 * <p/>
 * Created by YangChao on 15-11-4 下午5:07.
 */
public interface RequestUIInterface {

    void showLoading(boolean show);

    void showEmpty(boolean show);

    void setEmptyText(int resId);

    void setEmptyIcon(int resId);

    void setEmptyTextResId(int resId);

    void setEmptyIconResId(int resId);
}
