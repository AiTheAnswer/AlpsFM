package com.allen.user.alpsfm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.allen.user.alpsfm.R;
import com.allen.user.alpsfm.fragment.AlbumListFragment;
import com.allen.user.alpsfm.utils.ImageLoader;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;

import org.xutils.x;

import java.util.List;

/**
 * Created by user on 17-3-9.
 */

public class AlbumListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private AlbumList albumList;
    private List<Album> mAlbums;

    public AlbumListAdapter(AlbumList albumList, Context mContext) {
        this.albumList = albumList;
        if (null != albumList) {
            mAlbums = albumList.getAlbums();
        }
        mInflater = LayoutInflater.from(mContext);

    }

    public AlbumListAdapter(List<Album> albums, Context context) {
        this.mAlbums = albums;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return (null != mAlbums) ? mAlbums.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return mAlbums.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHodler mViewHodler;
        //专辑实体对象
        Album album = mAlbums.get(position);
        if (convertView == null) {
            mViewHodler = new ViewHodler();
            convertView = mInflater.inflate(R.layout.item_albums_item, null);
            mViewHodler.mImgAlbumIcon = (ImageView) convertView.findViewById(R.id.item_img_albums_icon);
            mViewHodler.mTxtAlbumTitle = (TextView) convertView.findViewById(R.id.item_txt_albums_title);
            mViewHodler.mTxtAlbumInfo = (TextView) convertView.findViewById(R.id.item_txt_albums_info);
            mViewHodler.mTxtPlayCount = (TextView) convertView.findViewById(R.id.item_txt_albums_play_count);
            mViewHodler.mTxtSoundsCount = (TextView) convertView.findViewById(R.id.item_txt_albums_sounds_count);
            convertView.setTag(mViewHodler);
        } else {
            mViewHodler = (ViewHodler) convertView.getTag();
        }
        if (null != album) {
            //设置图标
            ImageLoader.loadImage(album.getCoverUrlMiddle(), mViewHodler.mImgAlbumIcon);
            //设置图标
            mViewHodler.mTxtAlbumTitle.setText(album.getAlbumTitle());
            //设置最后一次更新声音的标题
            mViewHodler.mTxtAlbumInfo.setText(album.getLastUptrack().getTrackTitle());
            //设置专辑播放的次数
            mViewHodler.mTxtPlayCount.setText(album.getPlayCount() + "");
            //设置专辑声音的集数
            mViewHodler.mTxtSoundsCount.setText(album.getIncludeTrackCount() + "集");
        }

        return convertView;
    }

    class ViewHodler {
        //专辑图标
        private ImageView mImgAlbumIcon;
        //专辑标题
        private TextView mTxtAlbumTitle;
        //专辑简介
        private TextView mTxtAlbumInfo;
        //专辑播放次数
        private TextView mTxtPlayCount;
        //专辑下共有多少集
        private TextView mTxtSoundsCount;

        public ViewHodler() {
        }
    }
}
