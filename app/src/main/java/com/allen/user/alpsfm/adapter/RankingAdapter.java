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
import com.ximalaya.ting.android.opensdk.model.ranks.Rank;
import com.ximalaya.ting.android.opensdk.model.ranks.RankItem;
import com.ximalaya.ting.android.opensdk.model.ranks.RankList;

import java.util.List;

/**
 * Created by user on 17-3-14.
 */

public class RankingAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private RankList mRankList;
    private List<Rank> mDates;

    public RankingAdapter(Context context, RankList mRankList) {
        this.mInflater = LayoutInflater.from(context);
        this.mRankList = mRankList;
        this.mDates = mRankList.getRankList();
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
        Rank rank = mDates.get(position);
        ViewHolder mViewHolder;
        if (convertView == null) {
            mViewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_ranking_list, null);
            mViewHolder.mImgIcon = (ImageView) convertView.findViewById(R.id.item_img_ranking_icon);
            mViewHolder.mTxtTitle = (TextView) convertView.findViewById(R.id.item_txt_ranking_title);
            mViewHolder.mTxtSubFirstTitle = (TextView) convertView.findViewById(R.id.item_txt_ranking_sub_title1);
            mViewHolder.mTxtSubSecondTitle = (TextView) convertView.findViewById(R.id.item_txt_ranking_sub_title2);
            mViewHolder.mBottomLine = convertView.findViewById(R.id.item_view_bottom_line);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        if (null == rank) {
            return convertView;
        }
        ImageLoader.loadImage(rank.getCoverUrl(), mViewHolder.mImgIcon);
        mViewHolder.mTxtTitle.setText(rank.getRankTitle());
        List<RankItem> itemList = rank.getRankItemList();

        if (null != itemList) {
            mViewHolder.mTxtSubFirstTitle.setText("1  " + itemList.get(0).getTitle());
            if (itemList.size() > 1) {
                mViewHolder.mBottomLine.setVisibility(View.VISIBLE);
                mViewHolder.mTxtSubSecondTitle.setVisibility(View.VISIBLE);
                mViewHolder.mTxtSubSecondTitle.setText("2  " + itemList.get(1).getTitle());
            } else {
                mViewHolder.mBottomLine.setVisibility(View.GONE);
                mViewHolder.mTxtSubSecondTitle.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    class ViewHolder {
        private ImageView mImgIcon;
        private TextView mTxtTitle;
        private TextView mTxtSubFirstTitle;
        private TextView mTxtSubSecondTitle;
        private View mBottomLine;

        public ViewHolder() {
        }

    }
}
