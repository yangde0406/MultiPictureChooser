package com.yangde.multipicturechooser.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.yangde.multipicturechooser.R;

/**
 * Created by yangde on 15/4/17.
 */
public class ImageLoaderManager {
    private static ImageLoaderManager instance;
    private Context context;
    private ImageLoader thumbnailImageLoader;

    private ImageLoaderManager(Context context) {
        this.context = context;
        this.thumbnailImageLoader = new ImageLoader(context, ImageLoader.ImageLoadType.LOADING_TYPE_THUMBNAILIMAGE);
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
}
