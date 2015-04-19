package com.yangde.multipicturechooser.fragment;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yangde.multipicturechooser.ChooseImageActivity;
import com.yangde.multipicturechooser.R;
import com.yangde.multipicturechooser.adapter.AlbumAdapter;
import com.yangde.multipicturechooser.adapter.vo.AlbumItem;
import com.yangde.multipicturechooser.consts.LoadeImageConsts;

public class SelectAlbumFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String[] LOADING_COLUMN = {
            MediaStore.Images.ImageColumns._ID, // ID “559497”
            MediaStore.Images.Media.BUCKET_ID, // dir id
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME, // 目录名字 "Camera"
            "count('bucket_display_name') as allbum_count" // 相册包含相片总数
    };
    private static final float MASK_ALPHA = 0.5f;
    private int ITEM_HEIGHT;
    private boolean isListViewShow;

    private AlbumAdapter adapter;
    private ListView albumListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_album, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ITEM_HEIGHT = getResources().getDimensionPixelSize(R.dimen.album_item_heigth);

        getView().setBackgroundColor(Color.argb(0, 0, 0, 0));
        getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isListViewShow) {
                    hideList();
                }
            }
        });

        albumListView = (ListView) getView().findViewById(R.id.select_album_listview);
        albumListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlbumItem item = adapter.getItem(position);
                ((ChooseImageActivity) getActivity()).refreshGridViewByAlbumId(item.id);
                adapter.setCurrAlumbId(item.id);
                hideList();
            }
        });
        adapter = new AlbumAdapter(getActivity());
        albumListView.setAdapter(adapter);
        getLoaderManager().initLoader(LoadeImageConsts.LOADER_ALBUM_CURSOR, null, this);
    }

    public void setFirstItem(AlbumItem item) {
        adapter.setItem(0, item);
    }

    public void showOrHideList() {
        if (isListViewShow) {
            hideList();
        } else {
            showList();
        }
    }

    private void showList() {
        if (getView().getAnimation() == null && !isListViewShow) {
            getView().setClickable(true);
            isListViewShow = true;
            ViewGroup.LayoutParams params = albumListView.getLayoutParams();
            getView().startAnimation(new ShowHideAlbumAnimation(0, MASK_ALPHA, params.height, getCurHeight(), false));
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void hideList() {
        if (getView().getAnimation() == null && isListViewShow) {
            getView().setClickable(false);
            isListViewShow = false;
            ViewGroup.LayoutParams params = albumListView.getLayoutParams();
            getView().startAnimation(new ShowHideAlbumAnimation(MASK_ALPHA, 0, params.height, getCurHeight(), false));
        }
    }

    private int getCurHeight() {
        if (isListViewShow) {
            int max = ITEM_HEIGHT * 4 + ITEM_HEIGHT / 2;
            int height = 0;
            if (adapter != null) {
                height = (int) Math.min(max, ITEM_HEIGHT * adapter.getCount());
            }
            return height;
        } else {
            return 0;
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // 1=1) group by(bucket_id :用于按照结果（MediaStore.Images.Media.BUCKET_ID = bucket_display_name）去重，选出所有相册
        return new CursorLoader(getActivity(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, LoadeImageConsts.LOADING_COLUMN, "1=1) group by (bucket_display_name", null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.setAlbumCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Cursor cursor = adapter.getAlbumCursor();
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        adapter.setAlbumCursor(null);
    }

    public final class ShowHideAlbumAnimation extends Animation {

        private float fromAlpha;
        private float toAlpha;
        private int fromHeight;
        private int toHeight;
        private boolean onlyChangListHeight;

        public ShowHideAlbumAnimation(float fromAlpha, float toAlpha, int fromHeight, int toHeight, boolean onlyChangListHeight) {
            setDuration(300);
            setFillAfter(false);
            setRepeatCount(0);
            this.fromHeight = fromHeight;
            this.fromAlpha = fromAlpha;
            this.toAlpha = toAlpha;
            this.toHeight = toHeight;
            this.onlyChangListHeight = onlyChangListHeight;

            this.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (!ShowHideAlbumAnimation.this.onlyChangListHeight) {
                        if (ShowHideAlbumAnimation.this.toAlpha == 0) {
                            getView().setClickable(false);
                        } else {
                            getView().setClickable(true);
                        }
                        getView().setBackgroundColor(Color.argb((int) (ShowHideAlbumAnimation.this.toAlpha * 255), 0, 0, 0));
                    }
                    ViewGroup.LayoutParams params = albumListView.getLayoutParams();
                    params.height = ShowHideAlbumAnimation.this.toHeight;
                    albumListView.setLayoutParams(params);
                    getView().clearAnimation();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            ViewGroup.LayoutParams params = albumListView.getLayoutParams();
            params.height = (int) (fromHeight + (toHeight - fromHeight) * interpolatedTime);
            albumListView.setLayoutParams(params);

            if (!ShowHideAlbumAnimation.this.onlyChangListHeight) {
                float alpha = (fromAlpha + (toAlpha - fromAlpha) * interpolatedTime);
                getView().setBackgroundColor(Color.argb((int) (alpha * 255), 0, 0, 0));
            }
        }
    }
}
