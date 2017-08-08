package com.allen.user.alpsfm.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.allen.user.alpsfm.MainActivity;
import com.allen.user.alpsfm.R;
import com.allen.user.alpsfm.adapter.BroadCityListAdapter;
import com.allen.user.alpsfm.utils.Utils;
import com.allen.user.alpsfm.view.SwipyRefreshLayout;
import com.allen.user.alpsfm.view.SwipyRefreshLayoutDirection;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.live.provinces.Province;
import com.ximalaya.ting.android.opensdk.model.live.provinces.ProvinceList;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by user on 17-3-22.
 */

public class ProRadiosListFragment extends Fragment implements AdapterView.OnItemClickListener {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fragment_isNetworkAvailable)
    RelativeLayout mNetworkAvailable;
    @BindView(R.id.img_loading)
    ImageView mImgLoading;
    @BindView(R.id.relative_loading)
    RelativeLayout mLayoutLoading;
    @BindView(R.id.fragment_broad_radios_list)
    ListView mListView;
    @BindView(R.id.btn_try_agin)
    Button mBtnTry;
    @BindView(R.id.radios_refresh_layout)
    SwipyRefreshLayout mRefreshLayout;
    @BindView(R.id.fragment_radios_content)
    FrameLayout mLayoutContent;

    private MainActivity mActivity;
    private String mProvinceCode;
    private List<Radio> mRadios;
    private BroadCityListAdapter mAdapter;
    private int mPage = 1;

    public String mProvinceName;

    public ProRadiosListFragment(String provinceName, String provinceCode) {
        this.mProvinceName = provinceName;
        this.mProvinceCode = provinceCode;
    }

    public String getProvinceName() {
        return mProvinceName;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_radio_list, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        mToolbar.setVisibility(View.GONE);
        mRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTTOM);
        mRefreshLayout.setColorSchemeColors(Color.BLUE, Color.RED, Color.GREEN);
        loadDate();
        initListener();
    }

    private void initListener() {
        mRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if (direction == SwipyRefreshLayoutDirection.BOTTOM) {
                    loadMore();
                }
            }
        });
        mListView.setOnItemClickListener(this);
        mBtnTry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDate();
            }
        });
    }

    private void loadMore() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.RADIOTYPE, 2 + "");
        map.put(DTransferConstants.PROVINCECODE, mProvinceCode);
        map.put(DTransferConstants.PAGE, mPage + "");
        map.put(DTransferConstants.PAGE_SIZE, 20 + "");
        CommonRequest.getRadios(map, new IDataCallBack<RadioList>() {
            @Override
            public void onSuccess(RadioList radioList) {
                mRefreshLayout.setRefreshing(false);
                if (null == radioList || null == radioList.getRadios() || radioList.getRadios().size() == 0) {
                    Toast.makeText(mActivity, "没有更多了", Toast.LENGTH_SHORT).show();
                    return;
                }
                mRadios.addAll(radioList.getRadios());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int i, String s) {
                mRefreshLayout.setRefreshing(false);
                Toast.makeText(mActivity, "加载失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDate() {
        if (!Utils.isNetworkAvailable(mActivity)) {
            mNetworkAvailable.setVisibility(View.VISIBLE);
            mLayoutLoading.setVisibility(View.GONE);
            mLayoutContent.setVisibility(View.GONE);
            mRefreshLayout.setRefreshing(false);
        } else {
            mNetworkAvailable.setVisibility(View.GONE);
            mLayoutLoading.setVisibility(View.VISIBLE);
            mImgLoading.startAnimation(mActivity.mAnimation);
            mLayoutContent.setVisibility(View.GONE);
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.RADIOTYPE, 2 + "");
        map.put(DTransferConstants.PROVINCECODE, mProvinceCode);
        map.put(DTransferConstants.PAGE, 1 + "");
        CommonRequest.getRadios(map, new IDataCallBack<RadioList>() {
            @Override
            public void onSuccess(RadioList radioList) {
                mRefreshLayout.setRefreshing(false);
                mLayoutLoading.setVisibility(View.GONE);
                mLayoutContent.setVisibility(View.VISIBLE);
                mRadios = radioList.getRadios();
                mAdapter = new BroadCityListAdapter(mActivity, mRadios);
                mListView.setAdapter(mAdapter);
            }

            @Override
            public void onError(int i, String s) {
                mLayoutLoading.setVisibility(View.GONE);
                mLayoutContent.setVisibility(View.GONE);
                mNetworkAvailable.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Radio radio = (Radio) mAdapter.getItem(position);
        mActivity.setContentVisible(false);
        mActivity.switchState(MainActivity.STATE.PLAYER);
        mActivity.playRadio(radio);
        mActivity.mStack.push(mActivity.mProvinceRadiosFragment);
    }
}
