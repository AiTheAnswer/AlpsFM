package com.allen.user.alpsfm.adapter;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.allen.user.alpsfm.MainActivity;
import com.allen.user.alpsfm.R;
import com.allen.user.alpsfm.utils.CategoryId2CategoryName;
import com.allen.user.alpsfm.utils.ImageLoader;
import com.allen.user.alpsfm.utils.MySharedPreferences;
import com.allen.user.alpsfm.utils.Utils;
import com.pkmmte.view.CircularImageView;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackHotList;

import java.util.List;


/**
 * Created by user on 17-2-28.
 */

public class RecommendListAdapter extends BaseAdapter {
    private MainActivity mActivity;
    private LayoutInflater mInflater;
    private TrackHotList mTrackHotList;
    private List<Track> mTracks;
    private SharedPreferences mPreferences;

    public RecommendListAdapter(MainActivity mActivity, TrackHotList trackHotList) {
        this.mActivity = mActivity;
        this.mTrackHotList = trackHotList;
        mInflater = LayoutInflater.from(mActivity);
        mTracks = trackHotList.getTracks();
        mPreferences = MySharedPreferences.getPreferences(mActivity);
    }

    @Override
    public int getCount() {
        return (null != mTracks) ? mTracks.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return mTracks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final MyViewHolder myViewHolder;
        final Track track = mTracks.get(position);
        if (null == convertView) {
            myViewHolder = new MyViewHolder();
            convertView = mInflater.inflate(R.layout.recommend_recyclerview_item, null, false);
            myViewHolder.mTxtTitle = (TextView) convertView.findViewById(R.id.recommend_item_title);
            myViewHolder.mTxtAlbumName = (TextView) convertView.findViewById(R.id.recommend_album_name);
            myViewHolder.mTxtCategory = (TextView) convertView.findViewById(R.id.recommend_category);
            myViewHolder.mTxtLabel = (TextView) convertView.findViewById(R.id.recommend_label);
            myViewHolder.mTxtLength = (TextView) convertView.findViewById(R.id.recommend_length);
            myViewHolder.mImgIcon = (CircularImageView) convertView.findViewById(R.id.recommend_item_icon);
            myViewHolder.mImgPauseIcon = (CircularImageView) convertView.findViewById(R.id.recommend_pause_icon);
            convertView.setTag(myViewHolder);
        } else {
            myViewHolder = (MyViewHolder) convertView.getTag();
        }
        if (null != track) {
            //文章的标题
            myViewHolder.mTxtTitle.setText(track.getTrackTitle());
            //专辑名字
            myViewHolder.mTxtAlbumName.setText(track.getAlbum().getAlbumTitle() + "");
            //专辑类别
            myViewHolder.mTxtCategory.setText(CategoryId2CategoryName.getCategoryName(mTrackHotList.getCategoryId()));
            String[] split = track.getTrackTags().split(",");
            //文章的标签
            if (split[0] != "") {
                myViewHolder.mTxtLabel.setText(split[0]);
                myViewHolder.mTxtLabel.setVisibility(View.VISIBLE);
            } else {
                myViewHolder.mTxtLabel.setVisibility(View.GONE);
            }
            //声音的长度
            myViewHolder.mTxtLength.setText(Utils.secToTime(track.getDuration()));
            //加载图片
            ImageLoader.loadImage(track.getCoverUrlMiddle(), myViewHolder.mImgIcon);
        }
        myViewHolder.mImgIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (track.getDataId() == mActivity.mDateId) {
                    mActivity.playerPause();
                } else {
                    mActivity.playList(mTrackHotList, position);
                }
            }
        });
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.playList(mTrackHotList, position);
                mActivity.switchState(MainActivity.STATE.PLAYER);
            }
        });
        int id;
        if (track.getDataId() == mActivity.mDateId) {
            id = R.mipmap.flag_player_pause;
        } else {
            id = R.mipmap.flag_player_play;
        }
        myViewHolder.mImgPauseIcon.setImageDrawable(mActivity.getResources().getDrawable(id));
        return convertView;
    }


    private class MyViewHolder {
        //内容标题
        TextView mTxtTitle;
        //时长
        TextView mTxtLength;
        //专辑名称
        TextView mTxtAlbumName;
        //专辑类别
        TextView mTxtCategory;
        //专辑标签
        TextView mTxtLabel;
        //声音的图标
        CircularImageView mImgIcon;
        //播放暂停的标示图片
        CircularImageView mImgPauseIcon;


    }
}
