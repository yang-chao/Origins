package com.assassin.origins.ui.common;

import android.view.View;

/**
 * Created by user on 15/6/30.
 */
public class ChooseImageDialog extends BaseDialogFragment implements View.OnClickListener {
    @Override
    public void onClick(View view) {

    }

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//        View view = inflater.inflate(R.layout.dialog_choose_image, container, false);
//        ButterKnife.bind(this, view);
//        return view;
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        if (getDialog() == null || getDialog().getWindow() == null) {
//            return;
//        }
//        Window window = getDialog().getWindow();
//        window.getAttributes().windowAnimations = R.style.DialogBottom;
//        window.setBackgroundDrawableResource(android.R.color.white);
//        window.setGravity(Gravity.BOTTOM);
//        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
//    }
//
//    @OnClick({R.id.camera, R.id.gallery})
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.camera:
//                if (getContext() == null) {
//                    return;
//                }
//                //拍照
//                if (ContextCompat.checkSelfPermission(BaseApplication.getInstance(),
//                        Manifest.permission.CAMERA)
//                        != PackageManager.PERMISSION_GRANTED) {
//
//                    // Should we show an explanation?
//                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
//                            Manifest.permission.CAMERA)) {
//
//                        // Show an expanation to the user *asynchronously* -- don't block
//                        // this thread waiting for the user's response! After the user
//                        // sees the explanation, try again to request the permission.
//                        Toast.makeText(getContext(), R.string.permission_tip, Toast.LENGTH_LONG).show();
//                    } else {
//                        // No explanation needed, we can request the permission.
//                        ActivityCompat.requestPermissions(getActivity(),
//                                new String[]{Manifest.permission.CAMERA}, Constant.PermissionRequestCode.CAMERA);
//
//                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//                        // app-defined int constant. The callback method gets the
//                        // result of the request.
//                    }
//                } else {
//                    Crop.takePicture(getActivity());
//                }
//                break;
//            case R.id.gallery:
//                if (getContext() == null) {
//                    return;
//                }
//                //相册
//                if (ContextCompat.checkSelfPermission(BaseApplication.getInstance(),
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                        != PackageManager.PERMISSION_GRANTED) {
//
//                    // Should we show an explanation?
//                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
//                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//
//                        // Show an expanation to the user *asynchronously* -- don't block
//                        // this thread waiting for the user's response! After the user
//                        // sees the explanation, try again to request the permission.
//                        Toast.makeText(getContext(), R.string.permission_tip, Toast.LENGTH_LONG).show();
//                    } else {
//                        // No explanation needed, we can request the permission.
//                        ActivityCompat.requestPermissions(getActivity(),
//                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.PermissionRequestCode.PHOTO);
//
//                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//                        // app-defined int constant. The callback method gets the
//                        // result of the request.
//                    }
//                } else {
//                    Crop.pick(getActivity());
//                }
//                break;
//        }
//        dismiss();
//    }

}
