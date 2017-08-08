package com.allen.user.alpsfm.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.allen.user.alpsfm.MainActivity;
import com.allen.user.alpsfm.R;
import com.allen.user.alpsfm.utils.ImageLoader;
import com.allen.user.alpsfm.utils.Utils;
import com.pkmmte.view.CircularImageView;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import org.xutils.x;

import java.util.List;

/**
 * Created by user on 17-3-10.
 */

public class AlbumTrackAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<Track> mDates;
    private MainActivity mActivity;

    public AlbumTrackAdapter(MainActivity activity, List<Track> mDates) {
        this.mDates = mDates;
        mActivity = activity;
        this.mInflater = LayoutInflater.from(activity);
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
        final ViewHolder mViewHolder;
        if (null == convertView) {
            mViewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_album_track, null);
            mViewHolder.mTrackIcon = (CircularImageView) convertView.findViewById(R.id.item_img_album_track_icon);
            mViewHolder.mIsPlaying = (CircularImageView) convertView.findViewById(R.id.item_img_albums_track_isplay);
            mViewHolder.mTxtTrackTitle = (TextView) convertView.findViewById(R.id.item_txt_album_track_title);
            mViewHolder.mTxtTrackPlayCount = (TextView) convertView.findViewById(R.id.item_txt_album_track_play_count);
            mViewHolder.mTxtTrackDuration = (TextView) convertView.findViewById(R.id.item_txt_album_track_duration);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        final Track track = mDates.get(position);
        if (null == track) {
            return convertView;
        }
        //加载图片
        ImageLoader.loadImage(track.getCoverUrlMiddle(), mViewHolder.mTrackIcon);
        int id;
        if (track.getDataId() == mActivity.mDateId) {
            id = R.mipmap.flag_player_pause;
        } else {
            id = R.mipmap.flag_player_play;
        }
        mViewHolder.mIsPlaying.setImageDrawable(mActivity.getResources().getDrawable(id));
        mViewHolder.mIsPlaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (track.getDataId() == mActivity.mDateId) {
                    mActivity.playerPause();
                } else {
                    mActivity.playList(mDates, position);
                }
            }
        });
        mViewHolder.mTxtTrackTitle.setText(track.getTrackTitle());
        mViewHolder.mTxtTrackPlayCount.setText(track.getPlayCount() + "次");
        mViewHolder.mTxtTrackDuration.setText(Utils.secToTime(track.getDuration()) + "");
        return convertView;
    }

    class ViewHolder {
        private CircularImageView mTrackIcon;
        private CircularImageView mIsPlaying;
        private TextView mTxtTrackTitle;
        private TextView mTxtTrackPlayCount;
        private TextView mTxtTrackDuration;

        private ViewHolder() {
        }
    }
}
