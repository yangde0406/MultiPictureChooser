package com.yangde.multipicturechooser;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.yangde.multipicturechooser.adapter.PictureAdapter;
import com.yangde.multipicturechooser.adapter.vo.AlbumItem;
import com.yangde.multipicturechooser.adapter.vo.ImageItem;
import com.yangde.multipicturechooser.consts.LoadeImageConsts;
import com.yangde.multipicturechooser.fragment.SelectAlbumFragment;
import com.yangde.multipicturechooser.manager.ImageLoaderManager;


public class ChooseImageActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String[] LOADING_COLUMN = {MediaStore.Images.ImageColumns._ID, // ID “559497”
            MediaStore.Images.Media.DATA,// “/storage/emulated/0/DCIM/Camera/IMG_20141206_203606.jpg”
            MediaStore.Images.ImageColumns.DISPLAY_NAME,// 图片名称 “IMG_20141206_203606.jpg”
            MediaStore.Images.Media.BUCKET_ID, // dir id 目录
    };
    private Integer albumId;

    private GridView gridView;
    private PictureAdapter adapter;
    private SelectAlbumFragment albumFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_image);
        ImageLoaderManager.getInstance(this).setMaxSelectSize(6);

        gridView = (GridView) findViewById(R.id.choose_image_gridview);
        adapter = new PictureAdapter(this);
        albumFragment = (SelectAlbumFragment) getSupportFragmentManager().findFragmentById(R.id.choose_image_album);
        refreshGridViewByAlbumId(LoadeImageConsts.LOADER_IMAGE_CURSOR);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageItem item = adapter.getItem(position);
            }
        });

        initHeader();

    }

    public void refreshGridViewByAlbumId(int id) {
        this.albumId = id;
        getSupportLoaderManager().initLoader(id, null, this);
    }

    private void initHeader() {
        Button backButton = (Button) findViewById(R.id.header_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView titleView = (TextView) findViewById(R.id.header_title);
        titleView.setText("选择图片");

        Button confirmButton = (Button) findViewById(R.id.header_right_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo confirm
            }
        });
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = null;
        String[] selectionArgs = null;
        if (albumId != null && albumId < 0) {
            selection = "bucket_id=?";
            selectionArgs = new String[]{"" + id};
        }
        return new CursorLoader(this, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, LOADING_COLUMN, selection, selectionArgs, MediaStore.Images.Media.DEFAULT_SORT_ORDER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // 第一次默认取全部图片，所以相册第一项为所有图片
        if (loader.getId() == LoadeImageConsts.LOADER_IMAGE_CURSOR) {
            AlbumItem item = new AlbumItem();
            item.id = loader.getId();
            item.imageCount = cursor.getCount();
            item.albumName = "所有图片";
            cursor.moveToFirst();
            item.firstImageId = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
            albumFragment.setFirstItem(item);
            adapter.setLoadCursor(cursor);
        } else {
            adapter.setLoadCursor(cursor);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (adapter.getLoadCursor() != null) {
            adapter.getLoadCursor().close();
            adapter.setLoadCursor(null);
        }
    }
}
