package com.allen.user.alpsfm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.allen.user.alpsfm.R;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioCategory;

import java.util.List;

/**
 * Created by user on 17-3-22.
 */

public class BrocdcastGridAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<RadioCategory> mDates;

    public BrocdcastGridAdapter(Context context, List<RadioCategory> mDates) {
        mInflater = LayoutInflater.from(context);
        this.mDates = mDates;
    }

    @Override
    public int getCount() {
        return (null != mDates) ? mDates.size() / 4 * 4 + 4 : 0;
    }

    @Override
    public Object getItem(int position) {
        if (position < mDates.size()) {
            return mDates.get(position);
        } else return "";
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
            convertView = mInflater.inflate(R.layout.item_broadcast_gridview, null);
            mViewHolder.mTxtName = (TextView) convertView.findViewById(R.id.item_txt_broad_grid_name);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        if (position < mDates.size()) {
            mViewHolder.mTxtName.setText(mDates.get(position).getRadioCategoryName() + "");
        } else {
            mViewHolder.mTxtName.setText("");
        }
        return convertView;
    }

    private class ViewHolder {
        private TextView mTxtName;
    }
}
