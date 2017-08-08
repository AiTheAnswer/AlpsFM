package com.allen.user.alpsfm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.allen.user.alpsfm.R;

import java.util.List;

/**
 * Created by user on 17-3-28.
 */

public class SuggestWordAdapter extends BaseAdapter {
    private List<String> mDates;
    private LayoutInflater mInflater;

    public SuggestWordAdapter(List<String> mDates, Context context) {
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
        if (convertView == null) {
            mViewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_search_sugges_ward, null);
            mViewHolder.mWard = (TextView) convertView.findViewById(R.id.item_txt_search_suggest_wards);
            convertView.setTag(mViewHolder);
        }
        mViewHolder = (ViewHolder) convertView.getTag();
        mViewHolder.mWard.setText(mDates.get(position));
        return convertView;
    }

    class ViewHolder {
        private TextView mWard;
    }
}
