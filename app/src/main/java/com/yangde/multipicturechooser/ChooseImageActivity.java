package com.yangde.multipicturechooser;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.yangde.multipicturechooser.adapter.ImageItem;
import com.yangde.multipicturechooser.adapter.PictureAdapter;
import com.yangde.multipicturechooser.manager.ImageLoaderManager;

import java.util.ArrayList;


public class ChooseImageActivity extends Activity {
    private GridView gridView;
    public PictureAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_image);
        ImageLoaderManager.getInstance(this);

        gridView = (GridView) findViewById(R.id.choose_image_gridview);
        ArrayList<ImageItem> list = getImages();

        adapter = new PictureAdapter(this, list);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.getItem(position);
            }
        });
//        getSupportLoaderManager().initLoader(1, null, LoaderManager.getInstance(this));
        adapter.notifyDataSetChanged();
    }

    private ArrayList<ImageItem> getImages() {
        ContentResolver contentResolver = getContentResolver();
        String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.TITLE};
        Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);
        int getCount = cursor.getColumnCount();
        ArrayList<ImageItem> arrayList = new ArrayList<ImageItem>(getCount);
        cursor.moveToFirst();
        ImageItem item;
        while (cursor.moveToNext()) {
            item = new ImageItem();
            item.id = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
            item.name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE));
            arrayList.add(item);
//            System.out.println("__________>" + cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID)));
        }
        return arrayList;
    }
}
