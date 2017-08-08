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
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioListByCategory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by user on 17-3-22.
 */

public class RadiosByCateFragment extends Fragment implements AdapterView.OnItemClickListener {
    @BindView(R.id.toolbar_title)
    TextView mTitle;
    @BindView(R.id.fragment_isNetworkAvailable)
    RelativeLayout mNetworkAvailable;
    @BindView(R.id.img_loading)
    ImageView mImgLoading;
    @BindView(R.id.relative_loading)
    RelativeLayout mLayoutLoading;
    @BindView(R.id.fragment_broad_radios_list)
    ListView mListview;
    @BindView(R.id.radios_refresh_layout)
    SwipyRefreshLayout mRefreshLayout;
    @BindView(R.id.fragment_radios_content)
    FrameLayout mRadiosContent;
    @BindView(R.id.btn_try_agin)
    Button mBtnTry;
    @BindView(R.id.include_no_content)
    RelativeLayout mNoContent;
    private RadioCategory mRadioCategory;
    private MainActivity mActivity;
    private List<Radio> mRadios;
    private BroadCityListAdapter mAdapter;
    private int mPage = 1;


    public void setParams(RadioCategory radioCategory) {
        this.mRadioCategory = radioCategory;
        if (null != mActivity) {
            loadDate();
        }
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
        mRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTTOM);
        mRefreshLayout.setColorSchemeColors(Color.BLUE, Color.RED, Color.GREEN);
        initListener();
        loadDate();
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
        mListview.setOnItemClickListener(this);
        mBtnTry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDate();
            }
        });
    }

    private void loadMore() {
        mTitle.setText(mRadioCategory.getRadioCategoryName());
        Map<String, String> maps = new HashMap<String, String>();
        maps.put(DTransferConstants.RADIO_CATEGORY_ID, mRadioCategory.getId() + "");
        maps.put(DTransferConstants.PAGE, ++mPage + "");
        maps.put(DTransferConstants.PAGE_SIZE, 20 + "");
        CommonRequest.getRadiosByCategory(maps, new IDataCallBack<RadioListByCategory>() {
            @Override
            public void onSuccess(RadioListByCategory listByCategory) {
                mRefreshLayout.setRefreshing(false);
                if (null == listByCategory || null == listByCategory.getRadios() || listByCategory.getRadios().size() == 0) {
                    Toast.makeText(mActivity, "没有更多了", Toast.LENGTH_SHORT).show();
                    return;
                }
                mRadios.addAll(listByCategory.getRadios());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int code, String message) {
                mRefreshLayout.setRefreshing(false);
                Toast.makeText(mActivity, "加载失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDate() {
        if (!Utils.isNetworkAvailable(mActivity)) {
            mNetworkAvailable.setVisibility(View.VISIBLE);
            mLayoutLoading.setVisibility(View.GONE);
            mRadiosContent.setVisibility(View.GONE);
            mRefreshLayout.setRefreshing(false);
        } else {
            mNetworkAvailable.setVisibility(View.GONE);
            mLayoutLoading.setVisibility(View.VISIBLE);
            mImgLoading.startAnimation(mActivity.mAnimation);
            mRadiosContent.setVisibility(View.GONE);
            mNoContent.setVisibility(View.GONE);
        }
        mTitle.setText(mRadioCategory.getRadioCategoryName());
        Map<String, String> maps = new HashMap<String, String>();
        maps.put(DTransferConstants.RADIO_CATEGORY_ID, mRadioCategory.getId() + "");
        CommonRequest.getRadiosByCategory(maps, new IDataCallBack<RadioListByCategory>() {
            @Override
            public void onSuccess(RadioListByCategory listByCategory) {
                mLayoutLoading.setVisibility(View.GONE);
                mRadiosContent.setVisibility(View.VISIBLE);
                mRadios = listByCategory.getRadios();
                mAdapter = new BroadCityListAdapter(mActivity, mRadios);
                if (mRadios.size() == 0) {
                    mNoContent.setVisibility(View.VISIBLE);
                } else {
                    mNoContent.setVisibility(View.GONE);
                }
                mListview.setAdapter(mAdapter);

            }

            @Override
            public void onError(int code, String message) {
                mLayoutLoading.setVisibility(View.GONE);
                mRadiosContent.setVisibility(View.GONE);
                mNetworkAvailable.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mActivity.setContentVisible(false);
        Radio radio = (Radio) mAdapter.getItem(position);
        mActivity.playRadio(radio);
        mActivity.switchState(MainActivity.STATE.PLAYER);
        mActivity.mStack.push(mActivity.mRadiosByCateFragment);
    }
}
