package com.allen.user.alpsfm.fragment;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.allen.user.alpsfm.MainActivity;
import com.allen.user.alpsfm.R;
import com.allen.user.alpsfm.adapter.AnchorListAdapter;
import com.allen.user.alpsfm.utils.Utils;
import com.allen.user.alpsfm.view.SwipyRefreshLayout;
import com.allen.user.alpsfm.view.SwipyRefreshLayoutDirection;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;
import com.ximalaya.ting.android.opensdk.model.announcer.AnnouncerCategory;
import com.ximalaya.ting.android.opensdk.model.announcer.AnnouncerList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by user on 17-3-16.
 */

public class AnchorListFragment extends Fragment implements AdapterView.OnItemClickListener {
    @BindView(R.id.toolbar_title)
    TextView mTitle;
    @BindView(R.id.anchor_radiogroup)
    RadioGroup mRadioGroup;
    @BindView(R.id.anchor_list_lv)
    ListView mListView;
    @BindView(R.id.fragment_list_radio_button_hot)
    RadioButton mButtonHot;
    @BindView(R.id.fragment_list_radio_button_new)
    RadioButton mButtonNew;
    @BindView(R.id.refresh_layout)
    SwipyRefreshLayout mRefreshLayout;
    @BindView(R.id.fragment_isNetworkAvailable)
    RelativeLayout mNetworkAvailable;
    @BindView(R.id.img_loading)
    ImageView mImgLoading;
    @BindView(R.id.relative_loading)
    RelativeLayout mLayoutLoading;
    @BindView(R.id.btn_try_agin)
    Button mBtnTry;
    private AnnouncerCategory mCategory;
    private AnchorListAdapter mAdapter;
    private String mCalcDimension = "1";
    private MainActivity mActivity;
    private FragmentManager mFragmentManager;
    private Resources mResources;
    private Drawable mUnSelectLeft;
    private Drawable mUnSelectRight;
    private Drawable mSelectedLeft;
    private Drawable mSelectedRight;
    private List<Announcer> mDates;
    private int mPage = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_anchor_list, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        mFragmentManager = mActivity.mFragmentManager;
        mResources = mActivity.getResources();
        initDate();
        mRefreshLayout.setVisibility(View.GONE);
        mRadioGroup.check(R.id.fragment_list_radio_button_hot);
        loadDate();
        initListener();
    }

    private void initDate() {
        mSelectedLeft = mResources.getDrawable(R.drawable.anchor_radio_button_selected_left);
        mSelectedRight = mResources.getDrawable(R.drawable.anchor_radio_button_selected_right);
        mUnSelectLeft = mResources.getDrawable(R.drawable.anchor_radio_button_unselect_left);
        mUnSelectRight = mResources.getDrawable(R.drawable.anchor_radio_button_unselect_right);
        mRadioGroup.check(R.id.fragment_list_radio_button_hot);
        mButtonHot.setTextColor(Color.WHITE);
        mButtonHot.setBackground(mSelectedLeft);
        mRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTTOM);
        mRefreshLayout.setColorSchemeColors(Color.BLUE, Color.RED, Color.GREEN);
    }

    private void initListener() {
        mBtnTry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDate();

            }
        });
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.fragment_list_radio_button_hot://最火
                        mCalcDimension = "1";
                        mButtonHot.setTextColor(Color.WHITE);
                        mButtonHot.setBackground(mSelectedLeft);
                        mButtonNew.setBackground(mUnSelectRight);
                        mButtonNew.setTextColor(Color.RED);
                        loadDate();
                        break;
                    case R.id.fragment_list_radio_button_new://最新
                        mCalcDimension = "2";
                        mButtonHot.setTextColor(Color.RED);
                        mButtonNew.setTextColor(Color.WHITE);
                        mButtonHot.setBackground(mUnSelectLeft);
                        mButtonNew.setBackground(mSelectedRight);
                        loadDate();
                        break;
                }
            }
        });
        mListView.setOnItemClickListener(this);
        mRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if (direction == SwipyRefreshLayoutDirection.BOTTOM) {
                    loadMore();
                }
            }
        });
        mNetworkAvailable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDate();
            }
        });
    }

    private void loadMore() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.VCATEGORY_ID, mCategory.getId() + "");
        map.put(DTransferConstants.CALC_DIMENSION, mCalcDimension);
        map.put(DTransferConstants.PAGE, ++mPage + "");
        map.put(DTransferConstants.PAGE_SIZE, 20 + "");
        CommonRequest.getAnnouncerList(map, new IDataCallBack<AnnouncerList>() {
            @Override
            public void onSuccess(AnnouncerList announcerList) {
                mRefreshLayout.setRefreshing(false);
                if (null == announcerList || null == announcerList.getAnnouncerList() || announcerList.getAnnouncerList().size() < 1) {
                    Toast.makeText(mActivity, "沒有更多了", Toast.LENGTH_SHORT).show();
                    return;
                }
                mDates.addAll(announcerList.getAnnouncerList());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int i, String s) {
                mRefreshLayout.setRefreshing(false);
                Toast.makeText(mActivity, "网络异常", Toast.LENGTH_SHORT).show();
                Log.i("tog", "getAnnouncerList -- code = " + i + "Message = " + s);
            }
        });

    }

    public void setAnnouncerCategory(AnnouncerCategory category) {
        this.mCategory = category;
        if (null != mActivity) {
            mRadioGroup.check(R.id.fragment_list_radio_button_hot);
            mRefreshLayout.setVisibility(View.GONE);
            loadDate();
        }
    }

    private void loadDate() {
        mPage = 1;
        if (!Utils.isNetworkAvailable(mActivity)) {
            mNetworkAvailable.setVisibility(View.VISIBLE);
            mLayoutLoading.setVisibility(View.GONE);
            mRefreshLayout.setVisibility(View.GONE);
            mListView.setVisibility(View.GONE);
            return;
        } else {
            mNetworkAvailable.setVisibility(View.GONE);
            mLayoutLoading.setVisibility(View.VISIBLE);
            mRefreshLayout.setVisibility(View.GONE);
            mImgLoading.startAnimation(mActivity.mAnimation);
        }
        getAnnouncerList();

    }

    private void getAnnouncerList() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.VCATEGORY_ID, mCategory.getId() + "");
        map.put(DTransferConstants.CALC_DIMENSION, mCalcDimension);
        map.put(DTransferConstants.PAGE, 1 + "");
        map.put(DTransferConstants.PAGE_SIZE, 20 + "");
        CommonRequest.getAnnouncerList(map, new IDataCallBack<AnnouncerList>() {
            @Override
            public void onSuccess(AnnouncerList announcerList) {
                mNetworkAvailable.setVisibility(View.GONE);
                mLayoutLoading.setVisibility(View.GONE);
                mTitle.setText(mCategory.getVcategoryName());
                mRefreshLayout.setVisibility(View.VISIBLE);
                mListView.setVisibility(View.VISIBLE);
                if (null == announcerList || null == announcerList.getAnnouncerList()) {
                    return;
                }
                mDates = announcerList.getAnnouncerList();
                mAdapter = new AnchorListAdapter(mActivity, mDates);
                mListView.setAdapter(mAdapter);
            }

            @Override
            public void onError(int i, String s) {
                mLayoutLoading.setVisibility(View.GONE);
                mNetworkAvailable.setVisibility(View.VISIBLE);
                mRefreshLayout.setVisibility(View.GONE);
                mListView.setVisibility(View.GONE);
                Log.i("tog", "getAnnouncerList -- code = " + i + "Message = " + s);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        mActivity.hideContentAllFragment(fragmentTransaction);
        mActivity.setContentVisible(true);
        if (null == mActivity.mAnchorDetailsFragment) {
            mActivity.mAnchorDetailsFragment = new AnchorDetailsFragment();
        }
        if (!mActivity.mAnchorDetailsFragment.isAdded()) {
            fragmentTransaction.add(R.id.content, mActivity.mAnchorDetailsFragment);
        }
        fragmentTransaction.show(mActivity.mAnchorDetailsFragment).commit();
        Announcer announcer = (Announcer) mAdapter.getItem(position);
        MainActivity.mStack.clear();
        MainActivity.mStack.push(mActivity.mAnchorListFragment);
        mActivity.mAnchorDetailsFragment.setAnnouncer(announcer);
    }
}
