package com.yangde.multipicturechooser.consts;

import android.provider.MediaStore;

/**
 * Created by Yangde on 2015/4/18.
 */
public class LoadeImageConsts {
    public static final int LOADER_IMAGE_CURSOR = 1;
    public static final int LOADER_ALBUM_CURSOR = 2;

    public static final String[] LOADING_COLUMN = {
            MediaStore.Images.ImageColumns._ID, // ID “559497”
            MediaStore.Images.Media.DATA,// “/storage/emulated/0/DCIM/Camera/IMG_20141206_203606.jpg”
            MediaStore.Images.ImageColumns.DISPLAY_NAME,// 图片名称 “IMG_20141206_203606.jpg”
            MediaStore.Images.Media.BUCKET_ID, // dir id
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME, // 目录名字 "Camera"
            "count('bucket_id') as allbum_count"
    };
}
