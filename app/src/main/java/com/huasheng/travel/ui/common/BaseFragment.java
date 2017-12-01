package com.huasheng.travel.ui.common;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by YangChao on 15-12-17 上午10:39.
 */
public class BaseFragment extends Fragment {

    private boolean mUserVisibleHint = true;

    public boolean onBackPressed() {
        return false;
    }

    public boolean onFragmentBackPressed() {
        return false;
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

    @Override
    public boolean getUserVisibleHint() {
        return mUserVisibleHint;
    }

    @Override
    public void setUserVisibleHint(boolean userVisibleHint) {
        super.setUserVisibleHint(userVisibleHint);
        mUserVisibleHint = userVisibleHint;
    }
}
