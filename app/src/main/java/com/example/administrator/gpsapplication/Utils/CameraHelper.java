package com.example.administrator.gpsapplication.Utils;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;

/**
 * Created by Administrator on 2017/10/7.
 * 提供打开拍照界面方法
 * 以及提供预览照片的方法
 */

public class CameraHelper {

    public static void takePhoto(Activity activity) {
        PhotoPicker.builder()
                .setPhotoCount(9)
                .setShowCamera(true)
                .setShowGif(true)
                .setPreviewEnabled(false)
                .start(activity, PhotoPicker.REQUEST_CODE);
    }

    public static void previewPhoto(Activity activity, ArrayList<String> paths, int index) {
        PhotoPreview.builder()
                .setPhotos(paths)
                .setCurrentItem(index)
                .setShowDeleteButton(false)
                .start(activity); //点击完成按钮后去的位置
    }

    public static void useGlide(ImageView view, ArrayList<String> photoPaths, int index) {
        Glide.with(view.getContext())
                .load(photoPaths.get(index))
                .centerCrop()
                .crossFade()
                .into(view);
    }
}
