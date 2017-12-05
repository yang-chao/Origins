package com.assassin.origins.ui.common;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.assassin.origins.R;
import com.assassin.origins.api.model.Share;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yc on 16/1/13.
 */
public class ShareDialogFragment extends BaseDialogFragment {
    public static final String PARAM_SHARE = "param_share";
    private Share mShare;
    private ShareListener mShareListener;
    private OnDialogDismissListener mOnDialogDismissListener;

    public interface ShareListener {
        void onPreShare(Share share);
    }

    public interface OnDialogDismissListener  {
        void onDismiss();
    }

    public void setShareListener(ShareListener shareListener) {
        mShareListener = shareListener;
    }

    public void setOnDialogDismissListener(OnDialogDismissListener onDialogDismissListener) {
        mOnDialogDismissListener = onDialogDismissListener;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mShare = (Share) bundle.getSerializable(PARAM_SHARE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_share, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() == null || getDialog().getWindow() == null) {
            return;
        }
        Window window = getDialog().getWindow();
        window.getAttributes().windowAnimations = R.style.DialogBottom;
        window.setBackgroundDrawableResource(android.R.color.white);
        window.setGravity(Gravity.BOTTOM);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @OnClick({R.id.wx_circle, R.id.wx, R.id.qq, R.id.qzone, R.id.weibo, R.id.copy, R.id.cancel})
    public void onClick(View v) {
        if (v.getId() == R.id.cancel) {
            dismiss();
            return;
        }

        if (mShare == null) {
            return;
        }

        String platform;
        switch (v.getId()) {
            case R.id.wx_circle:
                platform = Share.PLATFORM_WX_CIRCLE;
                break;
            case R.id.wx:
                platform = Share.PLATFORM_WX;
                break;
            case R.id.qq:
                platform = Share.PLATFORM_QQ;
                break;
            case R.id.qzone:
                platform = Share.PLATFORM_QZ;
                break;
            case R.id.weibo:
                platform = Share.PLATFORM_WB;
                break;
            case R.id.copy:
                platform = Share.PLATFORM_COPY;
                break;
            default:
                platform = Share.PLATFORM_ALL;
                break;
        }
        mShare.setPlatform(platform);
        if (mShareListener != null) {
            mShareListener.onPreShare(mShare);
        }
//        SocialHelper.share(getActivity(), mShare, mShareListener);
        dismiss();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mOnDialogDismissListener != null){
            mOnDialogDismissListener.onDismiss();
        }
    }
}
