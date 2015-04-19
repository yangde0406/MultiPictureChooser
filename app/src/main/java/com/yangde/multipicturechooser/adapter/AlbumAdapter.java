package com.yangde.multipicturechooser.adapter;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yangde.multipicturechooser.R;
import com.yangde.multipicturechooser.adapter.vo.AlbumItem;
import com.yangde.multipicturechooser.manager.ImageLoaderManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yangde on 2015/4/18.
 */
public class AlbumAdapter extends BaseAdapter {
    private Context context;
    private Map<Integer, AlbumItem> albumMap;
    private Cursor albumCursor;
    private int currAlumbId;

    public AlbumAdapter(Context context) {
        this.context = context;
        this.albumMap = new HashMap<Integer, AlbumItem>();
    }

    @Override
    public int getCount() {
        if (albumCursor == null) {
            return 0;
        }
        return albumCursor.getCount();
    }

    @Override
    public AlbumItem getItem(int position) {
        return albumMap.get(position);
    }

    @Override
    public long getItemId(int position) {
        return albumMap.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_album, null);
            holder.currFlagView = (ImageView) convertView.findViewById(R.id.alumb_curr_flag);
            holder.countView = (TextView) convertView.findViewById(R.id.alumb_count);
            holder.nameView = (TextView) convertView.findViewById(R.id.alubm_name);
            holder.imageView = (ImageView) convertView.findViewById(R.id.alumb_picture);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (albumCursor != null) {
            albumCursor.moveToPosition(position);

            AlbumItem albumItem;
            if (!albumMap.containsKey(position + 1)) {
                // 创建相册对象
                albumItem = new AlbumItem();
                albumItem.firstImageId = albumCursor.getInt(albumCursor.getColumnIndex(MediaStore.Images.ImageColumns._ID));
                albumItem.albumName = albumCursor.getString(albumCursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME));
                albumItem.id = albumCursor.getInt(albumCursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_ID));
                albumItem.imageCount = albumCursor.getInt(albumCursor.getColumnIndex("allbum_count"));
                albumMap.put(position + 1, albumItem);
            } else {
                albumItem = albumMap.get(position);
            }

            holder.nameView.setText(albumItem.albumName);
            holder.countView.setText(albumItem.imageCount + "张");
            ImageLoaderManager.getInstance(context).dispalyThumnailImage(albumItem.firstImageId, holder.imageView);
            holder.currFlagView.setVisibility(albumItem.id == this.currAlumbId ? View.VISIBLE : View.GONE);
        }
        return convertView;
    }

    public void setCurrAlumbId(int currAlumbId) {
        this.currAlumbId = currAlumbId;
        notifyDataSetChanged();
    }

    public Cursor getAlbumCursor() {
        return albumCursor;
    }

    public void setAlbumCursor(Cursor albumCursor) {
        this.albumCursor = albumCursor;
        notifyDataSetChanged();
    }

    public void setItem(int position, AlbumItem item) {
        albumMap.put(position, item);
    }

    class ViewHolder {
        TextView nameView;
        TextView countView;
        ImageView currFlagView;
        ImageView imageView;
    }
}
