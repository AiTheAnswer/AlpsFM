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
import com.ximalaya.ting.android.opensdk.model.category.Category;

import org.xutils.x;

import java.util.List;

/**
 * Created by user on 17-3-8.
 */

public class ClassificationGridAdapter extends BaseAdapter {
    private List<Category> mCategories;
    private Context mContext;
    private LayoutInflater mInflater;

    public ClassificationGridAdapter(List<Category> mCategories, Context mContext) {
        this.mCategories = mCategories;
        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return (null == mCategories) ? 0 : mCategories.size();
    }

    @Override
    public Object getItem(int position) {
        return mCategories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        if (null == convertView) {
            mViewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_classification_gridview, null);
            mViewHolder.mImgIcon = (ImageView) convertView.findViewById(R.id.item_img_icon_grid_classification);
            mViewHolder.mTxtName = (TextView) convertView.findViewById(R.id.item_txt_name_grid_classification);
            mViewHolder.mTopView = convertView.findViewById(R.id.item_view_top_grid_classification);
            mViewHolder.mBottomView = convertView.findViewById(R.id.item_view_bottom_grid_classification);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        Category category = mCategories.get(position);
        //设置图标
        ImageLoader.loadImage(category.getCoverUrlLarge(), mViewHolder.mImgIcon);
        //设置名字
        mViewHolder.mTxtName.setText(category.getCategoryName());
        mViewHolder.mTopView.setVisibility(View.GONE);
        if ((position) % 6 == 0 || (position - 1) % 6 == 0) {
            mViewHolder.mTopView.setVisibility(View.VISIBLE);
        } else {
            mViewHolder.mTopView.setVisibility(View.GONE);
        }
        if (position == getCount() - 1 || position == getCount()) {
            mViewHolder.mBottomView.setVisibility(View.VISIBLE);
        } else {
            mViewHolder.mBottomView.setVisibility(View.GONE);
        }
        return convertView;
    }

    class ViewHolder {
        //图标
        private ImageView mImgIcon;
        //名字
        private TextView mTxtName;
        private View mTopView;
        private View mBottomView;

        public ViewHolder() {
        }
    }
}
