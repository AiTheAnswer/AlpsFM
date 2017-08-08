package com.allen.user.alpsfm.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.allen.user.alpsfm.R;
import com.allen.user.alpsfm.utils.ImageLoader;
import com.pkmmte.view.CircularImageView;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.ranks.RankAlbumList;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by user on 17-3-15.
 */

public class RankAlbumAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private RankAlbumList mRankAlbumList;
    private List<Album> mDates;

    public RankAlbumAdapter(Context context, RankAlbumList rankAlbumList) {
        this.mRankAlbumList = rankAlbumList;
        mInflater = LayoutInflater.from(context);
        if (null != rankAlbumList) {
            mDates = rankAlbumList.getRankAlbumList();
        }
    }

    @Override
    public int getCount() {
        return (null != mDates) ? mDates.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return mDates.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        if (convertView == null) {
            mViewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_rank_album, null);
            mViewHolder.mTxtNum = (TextView) convertView.findViewById(R.id.item_txt_rank_album_num);
            mViewHolder.mImgAlbumIcon = (ImageView) convertView.findViewById(R.id.item_img_rank_album_icon);
            mViewHolder.mTxtAlbumTitle = (TextView) convertView.findViewById(R.id.item_txt_rank_album_title);
            mViewHolder.mTxtAlbumInfo = (TextView) convertView.findViewById(R.id.item_txt_rank_album_info);
            mViewHolder.mTxtAlbumSize = (TextView) convertView.findViewById(R.id.item_txt_rank_album_size);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        Album album = mDates.get(position);
        if (null == album) {
            return convertView;
        }
        mViewHolder.mTxtNum.setText(position + 1 + "");
        switch (position) {
            case 0:
                mViewHolder.mTxtNum.setTextColor(Color.RED);
                break;
            case 1:
                mViewHolder.mTxtNum.setTextColor(Color.GREEN);
                break;
            case 2:
                mViewHolder.mTxtNum.setTextColor(Color.BLUE);
                break;
            default:
                mViewHolder.mTxtNum.setTextColor(Color.BLACK);
                break;
        }

        ImageLoader.loadImage(album.getCoverUrlMiddle(), mViewHolder.mImgAlbumIcon);
        mViewHolder.mTxtAlbumTitle.setText(album.getAlbumTitle());
        mViewHolder.mTxtAlbumInfo.setText(album.getAlbumIntro());
        mViewHolder.mTxtAlbumSize.setText(album.getIncludeTrackCount() + "集");
        return convertView;
    }

    class ViewHolder {
        private TextView mTxtNum;
        private ImageView mImgAlbumIcon;
        private TextView mTxtAlbumTitle;
        private TextView mTxtAlbumInfo;
        private TextView mTxtAlbumSize;

        public ViewHolder() {
        }
    }
}
