package com.allen.user.alpsfm.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.allen.user.alpsfm.MainActivity;
import com.allen.user.alpsfm.R;
import com.allen.user.alpsfm.utils.ImageLoader;
import com.pkmmte.view.CircularImageView;
import com.ximalaya.ting.android.opensdk.model.ranks.RankTrackList;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

/**
 * Created by user on 17-3-15.
 */

public class RankTrackAdapter extends BaseAdapter {
    private RankTrackList mRankTrackList;
    private LayoutInflater mInflater;
    private List<Track> mDates;
    private MainActivity mActivity;

    public RankTrackAdapter(MainActivity activity, RankTrackList mRankTrackList) {
        this.mInflater = LayoutInflater.from(activity);
        this.mActivity = activity;
        this.mActivity = activity;
        if (null != mRankTrackList) {
            this.mRankTrackList = mRankTrackList;
            mDates = mRankTrackList.getTrackList();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        if (null == convertView) {
            mViewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_rank_track, null);
            mViewHolder.mTxtNum = (TextView) convertView.findViewById(R.id.item_txt_rank_track_num);
            mViewHolder.mTrackIcon = (CircularImageView) convertView.findViewById(R.id.item_img_rank_track_icon);
            mViewHolder.mFlagIcon = (CircularImageView) convertView.findViewById(R.id.item_img_rank_track_play_state);
            mViewHolder.mTrackTitle = (TextView) convertView.findViewById(R.id.item_txt_rank_track_title);
            mViewHolder.mTrackAlbum = (TextView) convertView.findViewById(R.id.item_txt_rank_track_album_name);
            mViewHolder.mFlagIcon = (CircularImageView) convertView.findViewById(R.id.item_img_rank_track_play_state);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        final Track track = mDates.get(position);
        if (null == track) {
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

        ImageLoader.loadImage(track.getCoverUrlMiddle(), mViewHolder.mTrackIcon);
        mViewHolder.mTrackTitle.setText(track.getTrackTitle());
        mViewHolder.mTrackAlbum.setText(track.getAlbum().getAlbumTitle() + "");
        int id;
        if (track.getDataId() == mActivity.mDateId) {
            id = R.mipmap.flag_player_pause;
        } else {
            id = R.mipmap.flag_player_play;
        }
        mViewHolder.mFlagIcon.setImageDrawable(mActivity.getResources().getDrawable(id));
        mViewHolder.mTrackIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (track.getDataId() == mActivity.mDateId) {
                    mActivity.playerPause();
                } else {
                    mActivity.playList(mDates, position);
                }
            }
        });

        return convertView;
    }

    class ViewHolder {
        private TextView mTxtNum;
        private CircularImageView mTrackIcon;
        private CircularImageView mFlagIcon;
        private TextView mTrackTitle;
        private TextView mTrackAlbum;

        public ViewHolder() {

        }
    }
}
