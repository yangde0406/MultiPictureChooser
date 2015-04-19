package com.yangde.multipicturechooser.manager;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.yangde.multipicturechooser.R;
import com.yangde.multipicturechooser.adapter.vo.AlbumItem;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yangde on 15/4/17.
 */
public class ImageLoaderManager {
    public static final String[] LOADING_COLUMN = {
            MediaStore.Images.ImageColumns._ID, // ID “559497”
            MediaStore.Images.Media.DATA,// “/storage/emulated/0/DCIM/Camera/IMG_20141206_203606.jpg”
            MediaStore.Images.ImageColumns.DISPLAY_NAME,// 图片名称 “IMG_20141206_203606.jpg”
            MediaStore.Images.Media.BUCKET_ID, // dir id 目录
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME // 目录名字 "Camera"
    };
    private static ImageLoaderManager instance;
    private Context context;
    private ImageLoader thumbnailImageLoader;
    private Map<String, AlbumItem> albumItemMap;

    private ImageLoaderManager(Context context) {
        this.context = context;
        this.albumItemMap = new HashMap<String, AlbumItem>();
        this.thumbnailImageLoader = new ImageLoader(context, ImageLoader.ImageLoadType.LOADING_TYPE_THUMBNAILIMAGE);
//        initImages();
    }

    public static ImageLoaderManager getInstance(Context context) {
        if (instance == null) {
            instance = new ImageLoaderManager(context);
        }
        return instance;
    }

    public void dispalyThumnailImage(Integer uri, final ImageView imageView) {
        imageView.setImageResource(R.drawable.image_loading_default);
        thumbnailImageLoader.loadingImage(uri, new ImageLoader.LoadedCallBack() {
            @Override
            public void startLoad() {

            }

            @Override
            public void imageLoaded(Bitmap bigBitmap, Bitmap thumbnailBitmap) {
                imageView.setImageBitmap(thumbnailBitmap);
            }
        });
    }

    public void initImages() {
        ContentResolver contentResolver = context.getContentResolver();
        String[] projection = {MediaStore.Images.Media.BUCKET_ID};
        Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, "", null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);
        cursor.moveToFirst();
        int j = cursor.getPosition();
        Map<String, AlbumItem> albumItem;
        while (cursor.moveToNext()) {
//            int i = cursor.getPosition();
//            int id = cursor.getInt(0);
            String data = cursor.getString(0);
//            String displayName = cursor.getString(2);
//            String dirId = cursor.getString(3);
//            String dir = cursor.getString(4);

        }
    }
}
