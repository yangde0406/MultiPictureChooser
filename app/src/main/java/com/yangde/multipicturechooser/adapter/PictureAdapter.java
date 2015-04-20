package com.yangde.multipicturechooser.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.yangde.multipicturechooser.R;
import com.yangde.multipicturechooser.adapter.vo.ImageItem;
import com.yangde.multipicturechooser.manager.ImageLoaderManager;
import com.yangde.multipicturechooser.utils.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yangde on 15/4/15.
 */
public class PictureAdapter extends BaseAdapter {
    private int itemSize;
    private LayoutInflater inflater;
    private CompoundButton.OnCheckedChangeListener listener;
    private Map<Integer, ImageItem> dataMap;
    private Context context;
    private RelativeLayout.LayoutParams params;
    private Cursor loadCursor;
    private ImageLoaderManager loaderManager;

    public PictureAdapter(Activity activity) {
        inflater = activity.getLayoutInflater();
        dataMap = new HashMap<Integer, ImageItem>();
        context = activity;
        loaderManager = ImageLoaderManager.getInstance(context);

        // 计算每个项的高度：高度=宽度
        int[] point = new int[2];
        Utils.GetScreenSize(context, point);
        int spaceSize = context.getResources().getDimensionPixelSize(R.dimen.gridview_space_size) * 2;
        itemSize = (point[0] - spaceSize) / 3;

        listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ViewHolder holder = (ViewHolder) buttonView.getTag();
                if (loaderManager.addSelect(holder.imageItem)) {
                    holder.imageItem.isChecked = isChecked;
                } else {
                    holder.imageItem.isChecked = false;
                    notifyDataSetChanged();
                    Toast.makeText(context, String.format("您最多只能选择%d张图片", loaderManager.getMaxSelectSize()), Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    @Override
    public int getCount() {
        if (loadCursor == null) {
            return 0;
        }
        return loadCursor.getCount();
    }

    @Override
    public ImageItem getItem(int position) {
        return dataMap.get(position + 1);
    }

    @Override
    public long getItemId(int position) {
        ImageItem item = dataMap.get(position);
        if (item == null) return 0;
        return dataMap.get(position + 1).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_picture, null);
            holder = new ViewHolder();
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.picture_checkbox);
            holder.imageView = (ImageView) convertView.findViewById(R.id.picture_imageview);
            holder.checkBox.setOnCheckedChangeListener(listener);
            params = (RelativeLayout.LayoutParams) holder.imageView.getLayoutParams();
            params.height = itemSize;
            holder.imageView.setLayoutParams(params);

            holder.checkBox.setTag(holder);
            holder.checkBox.setOnCheckedChangeListener(listener);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (loadCursor != null) {
            if (position == 0) {
                holder.checkBox.setChecked(false);
                holder.checkBox.setVisibility(View.GONE);
                holder.imageView.setImageResource(R.drawable.take_photo);
            } else {
                loadCursor.moveToPosition(position - 1);
                ImageItem item;
                if (!dataMap.containsKey(position)) {
                    item = new ImageItem();
                    item.id = loadCursor.getInt(loadCursor.getColumnIndex(MediaStore.Images.Media._ID));
                    item.name = loadCursor.getString(loadCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                    item.realPath = loadCursor.getString(loadCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    item.isChecked = false;
                    item.albumId = loadCursor.getInt(loadCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));

                    dataMap.put(position, item);
                } else {
                    item = dataMap.get(position);
                }
                holder.imageItem = item;
                holder.checkBox.setChecked(item.isChecked);
                holder.checkBox.setVisibility(View.VISIBLE);
                ImageLoaderManager.getInstance(context).dispalyThumnailImage(item.id, holder.imageView);
            }
        }
        return convertView;
    }

    public void setLoadCursor(Cursor loadCursor) {
        dataMap.clear();
        this.loadCursor = loadCursor;
        notifyDataSetChanged();
    }

    public Cursor getLoadCursor() {
        return loadCursor;
    }

    class ViewHolder {
        ImageItem imageItem;
        ImageView imageView;
        CheckBox checkBox;
    }
}
