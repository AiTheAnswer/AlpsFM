package com.allen.user.alpsfm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.allen.user.alpsfm.R;
import com.allen.user.alpsfm.utils.ImageLoader;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;

import java.util.List;

/**
 * Created by user on 17-3-22.
 */

public class BroadCityListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<Radio> mDates;

    public BroadCityListAdapter(Context context, List<Radio> mDates) {
        this.mDates = mDates;
        this.mInflater = LayoutInflater.from(context);
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
        if(convertView == null){
            mViewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_broad_city_list,null);
            mViewHolder.mImgRadioIcon = (ImageView) convertView.findViewById(R.id.item_img_broad_city_list_radio_icon);
            mViewHolder.mTxtRadioTitle = (TextView) convertView.findViewById(R.id.item_txt_broad_city_list_radio_title);
            mViewHolder.mTxtRadioName = (TextView) convertView.findViewById(R.id.item_txt_broad_city_list_radio_name);
            mViewHolder.mTxtCount = (TextView) convertView.findViewById(R.id.item_txt_broad_city_list_radio_play_count);
            mViewHolder.mImgPlayOrPause = (ImageView) convertView.findViewById(R.id.item_img_broad_city_list_radio_play_falg);
            convertView.setTag(mViewHolder);
        }else{
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        Radio radio = mDates.get(position);
        if(null != radio){
            ImageLoader.loadImage(radio.getCoverUrlLarge(),mViewHolder.mImgRadioIcon);
            mViewHolder.mTxtRadioTitle.setText(radio.getRadioName());
            mViewHolder.mTxtRadioName.setText(radio.getProgramName());
            mViewHolder.mTxtCount.setText(radio.getRadioPlayCount()+" 人");
        }
        return convertView;
    }

    private class ViewHolder {
        private ImageView mImgRadioIcon;
        private TextView mTxtRadioTitle;
        private TextView mTxtRadioName;
        private TextView mTxtCount;
        private ImageView mImgPlayOrPause;
    }
}
