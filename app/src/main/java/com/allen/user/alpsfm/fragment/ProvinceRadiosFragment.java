package com.allen.user.alpsfm.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.allen.user.alpsfm.MainActivity;
import com.allen.user.alpsfm.R;
import com.allen.user.alpsfm.adapter.ProViewPagerAdapter;
import com.allen.user.alpsfm.utils.Utils;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.live.provinces.Province;
import com.ximalaya.ting.android.opensdk.model.live.provinces.ProvinceList;
import com.ximalaya.ting.android.opensdk.model.live.radio.City;
import com.ximalaya.ting.android.opensdk.model.live.radio.CityList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by user on 17-3-22.
 */

public class ProvinceRadiosFragment extends Fragment {
    @BindView(R.id.toolbar_title)
    TextView mTitle;
    @BindView(R.id.fragment_isNetworkAvailable)
    RelativeLayout mNetworkAvailable;
    @BindView(R.id.img_loading)
    ImageView mImgLoading;
    @BindView(R.id.relative_loading)
    RelativeLayout mLayoutLoading;
    @BindView(R.id.province_radios_tablayout)
    TabLayout mTablayout;
    @BindView(R.id.province_radios_viewpager)
    ViewPager mViewpager;
    @BindView(R.id.layout_province_content)
    LinearLayout mProvinceContent;
    private MainActivity mActivity;
    private List<ProRadiosListFragment> mFragments;
    private ProViewPagerAdapter mAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_province_radios, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTablayout.setTabTextColors(Color.BLACK, Color.RED);
        mTablayout.setupWithViewPager(mViewpager);
        mTablayout.setSelectedTabIndicatorColor(Color.RED);
        mActivity = (MainActivity) getActivity();
        mTitle.setText("省市台");
        getProvinces();
    }

    private void getProvinces() {
        if (!Utils.isNetworkAvailable(mActivity)) {
            mNetworkAvailable.setVisibility(View.VISIBLE);
            mLayoutLoading.setVisibility(View.GONE);
            mProvinceContent.setVisibility(View.GONE);
        } else {
            mNetworkAvailable.setVisibility(View.GONE);
            mLayoutLoading.setVisibility(View.VISIBLE);
            mProvinceContent.setVisibility(View.GONE);
        }
        mFragments = new ArrayList<>();
        Map<String, String> map = new HashMap<String, String>();
        CommonRequest.getProvinces(map, new IDataCallBack<ProvinceList>() {
            @Override
            public void onSuccess(ProvinceList provinceList) {
                mLayoutLoading.setVisibility(View.GONE);
                mProvinceContent.setVisibility(View.VISIBLE);
                List<Province> provinces = provinceList.getProvinceList();
                for (Province province : provinces) {
                    long provinceCode = province.getProvinceCode();
                    String provinceName = province.getProvinceName();
                    mFragments.add(new ProRadiosListFragment(provinceName, provinceCode + ""));
                }
                mAdapter = new ProViewPagerAdapter(mActivity.mFragmentManager, mFragments);
                mViewpager.setAdapter(mAdapter);
            }

            @Override
            public void onError(int i, String s) {
                mLayoutLoading.setVisibility(View.GONE);
                mProvinceContent.setVisibility(View.VISIBLE);
            }
        });
    }
}
