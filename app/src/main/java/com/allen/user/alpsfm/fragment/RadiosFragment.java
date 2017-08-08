package com.allen.user.alpsfm.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import android.widget.TextView;
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
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioCategory;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by user on 17-3-22.
 */

public class RadiosFragment extends Fragment implements AdapterView.OnItemClickListener {
    @BindView(R.id.toolbar_title)
    TextView mTitle;
    @BindView(R.id.fragment_isNetworkAvailable)
    RelativeLayout mNetworkAvailable;
    @BindView(R.id.img_loading)
    ImageView mImgLoading;
    @BindView(R.id.relative_loading)
    RelativeLayout mLayoutLoading;
    @BindView(R.id.fragment_broad_radios_list)
    ListView mRadiosList;
    @BindView(R.id.fragment_radios_content)
    FrameLayout mLayoutContent;
    @BindView(R.id.radios_refresh_layout)
    SwipyRefreshLayout mRefreshLayout;
    private MainActivity mActivity;
    @BindView(R.id.btn_try_agin)
    Button mBtnTry;
    private RADIOTYPE mType;
    private String mCode;
    private int mPageNum = 1;
    private int mIntType = 0;
    private BroadCityListAdapter mAdapter;
    private List<Radio> mRadios;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Radio radio = (Radio) mAdapter.getItem(position);
        mActivity.setContentVisible(false);
        mActivity.switchState(MainActivity.STATE.PLAYER);
        mActivity.mStack.push(mActivity.mRadiosFragment);
        mActivity.playRadio(radio);
    }

    enum RADIOTYPE {
        LOCAL, COUNTRY, INTERNET
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_radio_list, null);
        ButterKnife.bind(this, view);
        mActivity = (MainActivity) getActivity();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTTOM);
        mRefreshLayout.setColorSchemeColors(Color.BLUE, Color.RED, Color.GREEN);
        initTitle();
        getRadios();
        initListener();
    }

    private void initTitle() {
        switch (mType) {
            case LOCAL:
                mIntType = 2;
                mTitle.setText("本地台");
                break;
            case COUNTRY:
                mIntType = 1;
                mTitle.setText("国家台");
                break;
            case INTERNET:
                mIntType = 3;
                mTitle.setText("网络台");
                break;
        }
    }

    private void initListener() {
        mRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if (direction == SwipyRefreshLayoutDirection.BOTTOM) {
                    loadMoreRadios();
                }
            }
        });
        mRadiosList.setOnItemClickListener(this);
        mBtnTry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRadios();
            }
        });
    }

    public void setParams(RADIOTYPE type, String code) {
        this.mType = type;
        this.mCode = code;
        mPageNum = 1;
        if (null != mActivity) {
            initTitle();
            getRadios();
        }
    }

    private void getRadios() {
        if (!Utils.isNetworkAvailable(mActivity)) {
            mNetworkAvailable.setVisibility(View.VISIBLE);
            mLayoutLoading.setVisibility(View.GONE);
            mLayoutContent.setVisibility(View.GONE);
            return;
        } else {
            mNetworkAvailable.setVisibility(View.GONE);
            mLayoutContent.setVisibility(View.GONE);
            mLayoutLoading.setVisibility(View.VISIBLE);
            mImgLoading.startAnimation(mActivity.mAnimation);
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.RADIOTYPE, mIntType + "");
        map.put(DTransferConstants.PROVINCECODE, mCode + "");
        map.put(DTransferConstants.PAGE, 1 + "");
        CommonRequest.getRadios(map, new IDataCallBack<RadioList>() {
            @Override
            public void onSuccess(RadioList radioList) {
                mLayoutLoading.setVisibility(View.GONE);
                mLayoutContent.setVisibility(View.VISIBLE);
                mRadios = radioList.getRadios();
                mAdapter = new BroadCityListAdapter(mActivity, mRadios);
                mRadiosList.setAdapter(mAdapter);
            }

            @Override
            public void onError(int i, String s) {
                mNetworkAvailable.setVisibility(View.VISIBLE);
                mLayoutLoading.setVisibility(View.GONE);
                mLayoutContent.setVisibility(View.GONE);
                Toast.makeText(mActivity, "加载失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMoreRadios() {
        if (!Utils.isNetworkAvailable(mActivity)) {
            mRefreshLayout.setRefreshing(false);
            Toast.makeText(mActivity, "加载失败", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        int type = 0;
        switch (mType) {
            case LOCAL:
                type = 2;
                break;
            case COUNTRY:
                type = 1;
                break;
            case INTERNET:
                type = 3;
                break;
        }
        map.put(DTransferConstants.RADIOTYPE, type + "");
        map.put(DTransferConstants.PROVINCECODE, mCode + "");
        map.put(DTransferConstants.PAGE, ++mPageNum + "");
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

}
