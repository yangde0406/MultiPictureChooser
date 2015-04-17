package com.yangde.multipicturechooser.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.yangde.multipicturechooser.R;
import com.yangde.multipicturechooser.manager.ImageLoaderManager;
import com.yangde.multipicturechooser.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangde on 15/4/15.
 */
public class PictureAdapter extends BaseAdapter {
    private int itemSize;
    private LayoutInflater inflater;
    private CompoundButton.OnCheckedChangeListener listener;
    private List<ImageItem> dataList;
    private Context context;
    private RelativeLayout.LayoutParams params;
    private Cursor loadCursor;

    public PictureAdapter(Activity activity, ArrayList<ImageItem> list) {
        inflater = activity.getLayoutInflater();
        listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        };
        dataList = list;
        context = activity;

        // 计算每个项的高度：高度=宽度
        int[] point = new int[2];
        Utils.GetScreenSize(context, point);
        int spaceSize = context.getResources().getDimensionPixelSize(R.dimen.gridview_space_size) * 2;
        itemSize = (point[0] - spaceSize) / 3;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
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
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ImageLoaderManager.getInstance(context).dispalyThumnailImage(dataList.get(position).id, holder.imageView);

        return convertView;
    }

    class ViewHolder {
        ImageView imageView;
        CheckBox checkBox;
    }
}
