package com.allen.user.alpsfm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.allen.user.alpsfm.R;
import com.allen.user.alpsfm.utils.ImageLoader;
import com.pkmmte.view.CircularImageView;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;
import com.ximalaya.ting.android.opensdk.model.announcer.AnnouncerList;

import java.util.List;

/**
 * Created by user on 17-3-16.
 */

public class AnchorListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private AnnouncerList mAnnouncerList;
    private List<Announcer> mDates;


    public AnchorListAdapter(Context context, List<Announcer> dates) {
        this.mInflater = LayoutInflater.from(context);
        this.mDates = dates;
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
            convertView = mInflater.inflate(R.layout.item_anchors_item, null);
            mViewHolder.mAnchorIcon = (CircularImageView) convertView.findViewById(R.id.item_img_anchor_list_icon);
            mViewHolder.mTxtAnchorName = (TextView) convertView.findViewById(R.id.item_txt_anchor_list_name);
            mViewHolder.mTxtAnchorInfo = (TextView) convertView.findViewById(R.id.item_txt_anchor_list_info);
            mViewHolder.mTxtCount = (TextView) convertView.findViewById(R.id.item_txt_anchor_list_sounds_count);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        Announcer announcer = mDates.get(position);
        if (announcer == null) {
            return convertView;
        }
        ImageLoader.loadImage(announcer.getAvatarUrl(), mViewHolder.mAnchorIcon);
        mViewHolder.mTxtAnchorName.setText(announcer.getNickname() + "");
        if ("".equals(announcer.getAnnouncerPosition())) {
            mViewHolder.mTxtAnchorInfo.setText(announcer.getVsignature());
        } else {
            mViewHolder.mTxtAnchorInfo.setText(announcer.getAnnouncerPosition() + "");
        }
        mViewHolder.mTxtCount.setText(announcer.getReleasedTrackCount() + "");
        return convertView;
    }

    class ViewHolder {
        private CircularImageView mAnchorIcon;
        private TextView mTxtAnchorName;
        private TextView mTxtAnchorInfo;
        private TextView mTxtCount;
    }
}
