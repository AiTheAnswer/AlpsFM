package com.allen.user.alpsfm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.allen.user.alpsfm.R;
import com.allen.user.alpsfm.model.PlayRecord;
import com.allen.user.alpsfm.utils.ImageLoader;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.album.SubordinatedAlbum;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;
import java.util.Map;

/**
 * Created by user on 17-3-27.
 */

public class PlayRecordAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Context mContext;
    private List<PlayRecord> mDates;

    public PlayRecordAdapter(Context context, List<PlayRecord> mDates) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
        this.mDates = mDates;
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
            convertView = mInflater.inflate(R.layout.item_playrecord_lst, null);
            mViewHolder.mImgAlbumIcon = (ImageView) convertView.findViewById(R.id.item_img_playrecord_album_icon);
            mViewHolder.mTxtAlbumName = (TextView) convertView.findViewById(R.id.item_txt_playrecord_album_title);
            mViewHolder.mTxtTrackName = (TextView) convertView.findViewById(R.id.item_txt_palyrecord_track_name);
            convertView.setTag(mViewHolder);
        }
        mViewHolder = (ViewHolder) convertView.getTag();
        PlayRecord playRecord = mDates.get(position);
        ImageLoader.loadImage(playRecord.getUrl(), mViewHolder.mImgAlbumIcon);
        mViewHolder.mTxtAlbumName.setText(playRecord.getAlbumName());
        mViewHolder.mTxtTrackName.setText(playRecord.getTrackName());
        return convertView;
    }

    class ViewHolder {
        private TextView mTxtAlbumName;
        private TextView mTxtTrackName;
        private ImageView mImgAlbumIcon;
    }
}
