package com.allen.user.alpsfm.adapter;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allen.user.alpsfm.MainActivity;
import com.allen.user.alpsfm.R;
import com.allen.user.alpsfm.fragment.AlbumDetailsFragment;
import com.allen.user.alpsfm.fragment.AnchorDetailsFragment;
import com.allen.user.alpsfm.fragment.AnchorListFragment;
import com.allen.user.alpsfm.utils.DisplayUtil;
import com.allen.user.alpsfm.utils.ImageLoader;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;
import com.ximalaya.ting.android.opensdk.model.announcer.AnnouncerCategory;
import com.ximalaya.ting.android.opensdk.model.announcer.AnnouncerCategoryList;
import com.ximalaya.ting.android.opensdk.model.announcer.AnnouncerList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 17-3-15.
 */

public class AnchorListViewAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private AnnouncerCategoryList mCategoryList;//主播分类集合
    private List<AnnouncerCategory> mCategorys;
    private Map<Integer, AnnouncerList> mAnnouncers;
    private int mImgWidth;
    private MainActivity mActivity;
    private FragmentManager mFragmentManager;

    public AnchorListViewAdapter(MainActivity activity, AnnouncerCategoryList mCategoryList) {
        this.mInflater = LayoutInflater.from(activity);
        this.mActivity = activity;
        this.mFragmentManager = activity.mFragmentManager;
        this.mCategoryList = mCategoryList;
        this.mCategorys = mCategoryList.getList();
        this.mAnnouncers = new HashMap<>();
        int width = DisplayUtil.getWindowWidth(activity);
        this.mImgWidth = (width - DisplayUtil.dip2px(activity, 40) - 10) / 3;
    }

    @Override
    public int getCount() {
        return (null != mCategorys) ? mCategorys.size() - 1 : 0;
    }

    @Override
    public Object getItem(int position) {
        return mAnnouncers.get(position);
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
            convertView = mInflater.inflate(R.layout.item_anchor, null);
            mViewHolder.mTxtAnchorCategory = (TextView) convertView.findViewById(R.id.item_txt_anchor_category);
            mViewHolder.mImgAchorIconFrist = (ImageView) convertView.findViewById(R.id.item_img_anchor_icon1);
            mViewHolder.mTxtAnchorNameFrist = (TextView) convertView.findViewById(R.id.item_txt_anchor_name1);
            mViewHolder.mTxtAnchorInfoFrist = (TextView) convertView.findViewById(R.id.item_txt_anchor_info1);
            mViewHolder.mImgAchorIconSecon = (ImageView) convertView.findViewById(R.id.item_img_anchor_icon2);
            mViewHolder.mTxtAnchorNameSecon = (TextView) convertView.findViewById(R.id.item_txt_anchor_name2);
            mViewHolder.mTxtAnchorInfoSecon = (TextView) convertView.findViewById(R.id.item_txt_anchor_info2);
            mViewHolder.mImgAchorIconThree = (ImageView) convertView.findViewById(R.id.item_img_anchor_icon3);
            mViewHolder.mTxtAnchorNameThree = (TextView) convertView.findViewById(R.id.item_txt_anchor_name3);
            mViewHolder.mTxtAnchorInfoThree = (TextView) convertView.findViewById(R.id.item_txt_anchor_info3);
            mViewHolder.mLayoutMore = (LinearLayout) convertView.findViewById(R.id.item_layout_anchor_more);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        if (null == mCategorys) {
            return convertView;
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mImgWidth, mImgWidth);
        mViewHolder.mImgAchorIconFrist.setLayoutParams(params);
        mViewHolder.mImgAchorIconFrist.setScaleType(ImageView.ScaleType.FIT_XY);
        mViewHolder.mImgAchorIconFrist.setPadding(2, 2, 1, 2);
        mViewHolder.mImgAchorIconSecon.setPadding(2, 2, 1, 2);
        mViewHolder.mImgAchorIconThree.setPadding(2, 2, 1, 2);
        mViewHolder.mImgAchorIconSecon.setLayoutParams(params);
        mViewHolder.mImgAchorIconSecon.setScaleType(ImageView.ScaleType.FIT_XY);
        mViewHolder.mImgAchorIconThree.setLayoutParams(params);
        mViewHolder.mImgAchorIconThree.setScaleType(ImageView.ScaleType.FIT_XY);

        final AnnouncerCategory category = mCategorys.get(position + 1);
        String vcategoryName = category.getVcategoryName();
        mViewHolder.mLayoutMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//更多
                if (null == mActivity.mAnchorListFragment) {
                    mActivity.mAnchorListFragment = new AnchorListFragment();
                }
                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                mActivity.hideContentAllFragment(fragmentTransaction);
                if (!mActivity.mAnchorListFragment.isAdded()) {
                    fragmentTransaction.add(R.id.content, mActivity.mAnchorListFragment);
                }
                mActivity.setContentVisible(true);
                fragmentTransaction.show(mActivity.mAnchorListFragment).commit();
                mActivity.mAnchorListFragment.setAnnouncerCategory(category);
            }
        });
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.VCATEGORY_ID, category.getId() + "");
        map.put(DTransferConstants.CALC_DIMENSION, "1");
        CommonRequest.getAnnouncerList(map, new IDataCallBack<AnnouncerList>() {
            @Override
            public void onSuccess(AnnouncerList announcerList) {
                List<Announcer> announcers = announcerList.getAnnouncerList();
                final Announcer announcer0 = announcers.get(0);
                final Announcer announcer1 = announcers.get(1);
                final Announcer announcer2 = announcers.get(2);
                mViewHolder.mImgAchorIconFrist.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                        mActivity.hideContentAllFragment(fragmentTransaction);
                        mActivity.setContentVisible(true);
                        //if (null == mActivity.mAnchorDetailsFragment) {
                        mActivity.mAnchorDetailsFragment = new AnchorDetailsFragment();
                        mActivity.mAnchorDetailsFragment.setAnnouncer(announcer0);
                        //}
                        if (!mActivity.mAnchorDetailsFragment.isAdded()) {
                            fragmentTransaction.add(R.id.content, mActivity.mAnchorDetailsFragment);
                        }
                        fragmentTransaction.show(mActivity.mAnchorDetailsFragment).commit();
                    }
                });
                mViewHolder.mImgAchorIconSecon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                        mActivity.hideContentAllFragment(fragmentTransaction);
                        mActivity.setContentVisible(true);
                        //if (null == mActivity.mAnchorDetailsFragment) {
                        mActivity.mAnchorDetailsFragment = new AnchorDetailsFragment();
                        mActivity.mAnchorDetailsFragment.setAnnouncer(announcer1);
                        // }
                        if (!mActivity.mAnchorDetailsFragment.isAdded()) {
                            fragmentTransaction.add(R.id.content, mActivity.mAnchorDetailsFragment);
                        }
                        fragmentTransaction.show(mActivity.mAnchorDetailsFragment).commit();

                    }
                });
                mViewHolder.mImgAchorIconThree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                        mActivity.hideContentAllFragment(fragmentTransaction);
                        mActivity.setContentVisible(true);
                        //if (null == mActivity.mAnchorDetailsFragment) {
                        mActivity.mAnchorDetailsFragment = new AnchorDetailsFragment();
                        mActivity.mAnchorDetailsFragment.setAnnouncer(announcer2);
                        // }
                        if (!mActivity.mAnchorDetailsFragment.isAdded()) {
                            fragmentTransaction.add(R.id.content, mActivity.mAnchorDetailsFragment);
                        }
                        fragmentTransaction.show(mActivity.mAnchorDetailsFragment).commit();
                    }
                });
                ImageLoader.loadImage(announcer0.getAvatarUrl(), mViewHolder.mImgAchorIconFrist);
                mViewHolder.mTxtAnchorNameFrist.setText(announcer0.getNickname());
                mViewHolder.mTxtAnchorInfoFrist.setText(announcer0.getAnnouncerPosition());
                ImageLoader.loadImage(announcer1.getAvatarUrl(), mViewHolder.mImgAchorIconSecon);
                mViewHolder.mTxtAnchorNameSecon.setText(announcer1.getNickname());
                mViewHolder.mTxtAnchorInfoSecon.setText(announcer1.getAnnouncerPosition());
                ImageLoader.loadImage(announcer2.getAvatarUrl(), mViewHolder.mImgAchorIconThree);
                mViewHolder.mTxtAnchorNameThree.setText(announcer2.getNickname());
                mViewHolder.mTxtAnchorInfoThree.setText(announcer2.getAnnouncerPosition());
                mAnnouncers.put(position, announcerList);

            }

            @Override
            public void onError(int i, String s) {
                Log.e("tog", "code = " + i + "Message = " + s);
            }
        });

        mViewHolder.mTxtAnchorCategory.setText(vcategoryName);
        return convertView;
    }


    class ViewHolder {
        private TextView mTxtAnchorCategory;
        private ImageView mImgAchorIconFrist;
        private TextView mTxtAnchorNameFrist;
        private TextView mTxtAnchorInfoFrist;
        private ImageView mImgAchorIconSecon;
        private TextView mTxtAnchorNameSecon;
        private TextView mTxtAnchorInfoSecon;
        private ImageView mImgAchorIconThree;
        private TextView mTxtAnchorNameThree;
        private TextView mTxtAnchorInfoThree;
        private LinearLayout mLayoutMore;

    }
}
