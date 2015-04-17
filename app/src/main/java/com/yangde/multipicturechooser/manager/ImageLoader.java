package com.yangde.multipicturechooser.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yangde on 15/4/17.
 */
public class ImageLoader {
    enum ImageLoadType {
        LOADING_TYPE_BIGIMAGE, LOADING_TYPE_THUMBNAILIMAGE
    }

    private Map<Integer, SoftReference<Bitmap>> imageCache = new HashMap<Integer, SoftReference<Bitmap>>();
    private Handler handler = new Handler();

    private Context context;
    private ImageLoadType loadType;

    public ImageLoader(Context context, ImageLoadType loadType) {
        this.context = context;
        this.loadType = loadType;
    }

    public void loadingImage(final Integer uri, final LoadedCallBack callBack) {
        if (callBack == null) {
            new IllegalAccessException("回调函数不能为空");
        }
        // 通过软引用获取图片
        if (imageCache.containsKey(uri)) {
            SoftReference<Bitmap> bitmapSoftReference = imageCache.get(uri);
            if (bitmapSoftReference.get() != null) {
                callBack.imageLoaded(null, bitmapSoftReference.get());
                return;
            }
        }

        // 启动子线程获取图片
        new Thread() {
            @Override
            public void run() {
                Log.d("Load Thread-->", "id=" + Thread.currentThread().getId());
                final Bitmap bitmap = loadingImageFromDisk(uri);

                // 回到主线程设置图片的显示
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        imageCache.put(uri, new SoftReference<Bitmap>(bitmap));
                        callBack.imageLoaded(null, bitmap);
                    }
                });
            }
        }.start();
    }

    private Bitmap loadingImageFromDisk(Integer uri) {
        if (loadType == ImageLoadType.LOADING_TYPE_THUMBNAILIMAGE) {
            return MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(), uri, MediaStore.Images.Thumbnails.MICRO_KIND, null);
        } else if (loadType == ImageLoadType.LOADING_TYPE_BIGIMAGE) {

        }
        return null;
    }

    public interface LoadedCallBack {
        public void startLoad();

        public abstract void imageLoaded(Bitmap bigBitmap, Bitmap thumbnailBitmap);
    }
}
