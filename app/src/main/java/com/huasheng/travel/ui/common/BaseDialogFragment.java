package com.huasheng.travel.ui.common;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by yc on 16/1/29.
 */
public class BaseDialogFragment extends DialogFragment {

    @Override
    public int show(FragmentTransaction transaction, String tag) {
        try {
            return super.show(transaction, tag);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return -1;
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
    public void onResume() {
        super.onResume();
        String pageName = getPageName();
        if (TextUtils.isEmpty(pageName)) {
//            pageName = Event.getPageName(getClass());
        }
        MobclickAgent.onPageStart(pageName);
    }

    @Override
    public void onPause() {
        super.onPause();
        String pageName = getPageName();
        if (TextUtils.isEmpty(pageName)) {
//            pageName = Event.getPageName(getClass());
        }
        MobclickAgent.onPageEnd(pageName);
    }
}
