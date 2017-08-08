package com.allen.user.alpsfm.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.allen.user.alpsfm.MainActivity;
import com.allen.user.alpsfm.R;
import com.allen.user.alpsfm.adapter.BroadCityListAdapter;
import com.allen.user.alpsfm.adapter.BrocdcastGridAdapter;
import com.allen.user.alpsfm.utils.Utils;
import com.allen.user.alpsfm.view.MyGridView;
import com.allen.user.alpsfm.view.MyListView;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.live.program.ProgramList;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioCategory;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioCategoryList;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioList;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioListById;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by allen on 2017/3/5.
 */
public class BroadcastFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    @BindView(R.id.toolbar_title)
    TextView mTitle;
    @BindView(R.id.broadcast_layout_local)
    LinearLayout mLayoutLocal;
    @BindView(R.id.broadcast_layout_country)
    LinearLayout mLayoutCountry;
    @BindView(R.id.broadcast_layout_province)
    LinearLayout mLayoutProvince;
    @BindView(R.id.broadcast_layout_internet)
    LinearLayout mLayoutInternet;
    @BindView(R.id.fragment_isNetworkAvailable)
    RelativeLayout mNetworkAvailable;
    @BindView(R.id.img_loading)
    ImageView mImgLoading;
    @BindView(R.id.relative_loading)
    RelativeLayout mLayoutLoading;
    @BindView(R.id.layout_city_more)
    LinearLayout mCityMore;
    @BindView(R.id.broadcast_city_listview)
    MyListView mCityListview;
    @BindView(R.id.broadcast_categorie_gridview)
    MyGridView mCategorieGridView;
    @BindView(R.id.broadcast_layout_content)
    LinearLayout mBroadcastContent;
    @BindView(R.id.fragment_txt_city_name)
    TextView mCityName;
    @BindView(R.id.btn_try_agin)
    Button mBtnTry;
    private MainActivity mActivity;
    private FragmentManager mFragmentManager;
    private String mProvinceCode = "110000";
    private String mLocationProvince = "陕西省";
    private String mLocationCity = "西安";
    private String mCityCode = "233";
    private BrocdcastGridAdapter mGridAdapter;
    private BroadCityListAdapter mCityListAdapter;
    private boolean mCategoryIsLoaded = false;
    private boolean mProvinceRadiosISLoaded = false;
    private List<RadioCategory> mRadioCategories;
    private List<Radio> mRadios;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_broadcast, null);
        ButterKnife.bind(this, view);
        mActivity = (MainActivity) getActivity();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLocationProvince = mActivity.mLocationProvince;
        mLocationCity = mActivity.mLocationCity;
        mCityCode = mActivity.mCityCode;
        mProvinceCode = Utils.getProvinceCode(mLocationProvince);
        mCityName.setText(mLocationCity);
        mTitle.setText("广播");
        initListener();
        loadDate();
    }

    private void loadDate() {
        if (!Utils.isNetworkAvailable(mActivity)) {
            mNetworkAvailable.setVisibility(View.VISIBLE);
            mLayoutLoading.setVisibility(View.GONE);
            mBroadcastContent.setVisibility(View.GONE);
        } else {
            mNetworkAvailable.setVisibility(View.GONE);
            mLayoutLoading.setVisibility(View.VISIBLE);
            mBroadcastContent.setVisibility(View.GONE);
            mImgLoading.startAnimation(mActivity.mAnimation);
            getRadioCategory();
            getRadiosByProvince();
        }
    }

    private void getRadiosByProvince() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.RADIOTYPE, 2 + "");
        map.put(DTransferConstants.PROVINCECODE, mProvinceCode + "");
        map.put(DTransferConstants.PAGE, 1 + "");
        map.put(DTransferConstants.PAGE_SIZE, 5 + "");
        CommonRequest.getRadios(map, new IDataCallBack<RadioList>() {
            @Override
            public void onSuccess(RadioList radioList) {
                if (null == radioList || radioList.getRadios().size() == 0) {
                    return;
                }
                mRadios = radioList.getRadios();
                mCityListAdapter = new BroadCityListAdapter(mActivity, mRadios);
                mCityListview.setAdapter(mCityListAdapter);
                mProvinceRadiosISLoaded = true;
                isLoaded();
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(mActivity, "加载失败", Toast.LENGTH_SHORT).show();
                mProvinceRadiosISLoaded = false;
                Log.i("tog", "BroadcastFragment ----getRadios code = " + i + "----Message = " + s);
                isLoaded();
            }
        });
    }

    private void getRadioCategory() {
        Map<String, String> map = new HashMap<String, String>();
        CommonRequest.getRadioCategory(map, new IDataCallBack<RadioCategoryList>() {
            @Override
            public void onSuccess(RadioCategoryList categoryList) {
                if (null == categoryList || categoryList.getRadioCategories().size() <= 0) {
                    return;
                }
                mRadioCategories = categoryList.getRadioCategories();
                mGridAdapter = new BrocdcastGridAdapter(mActivity, mRadioCategories);
                mCategorieGridView.setAdapter(mGridAdapter);
                mCategoryIsLoaded = true;
                isLoaded();
            }

            @Override
            public void onError(int code, String message) {
                Toast.makeText(mActivity, "加载失败", Toast.LENGTH_SHORT).show();
                Log.i("tog", "BroadcastFragment -- getRadioCategory() code = " + code + "---Message = " + message);
                mCategoryIsLoaded = false;
                isLoaded();
            }
        });
    }

    private void initListener() {
        mLayoutLocal.setOnClickListener(this);
        mLayoutCountry.setOnClickListener(this);
        mLayoutProvince.setOnClickListener(this);
        mLayoutInternet.setOnClickListener(this);
        mCategorieGridView.setOnItemClickListener(this);
        mCityListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mActivity.playRadio(mRadios.get(position));
                mActivity.switchState(MainActivity.STATE.PLAYER);
            }
        });
        mCityMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentManager = mActivity.mFragmentManager;
                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                if (null == mActivity.mRadiosFragment) {
                    mActivity.mRadiosFragment = new RadiosFragment();
                }
                if (!mActivity.mRadiosFragment.isAdded()) {
                    fragmentTransaction.add(R.id.content, mActivity.mRadiosFragment);
                }
                mActivity.mRadiosFragment.setParams(RadiosFragment.RADIOTYPE.LOCAL, mProvinceCode);
                mActivity.hideContentAllFragment(fragmentTransaction);
                mActivity.setContentVisible(true);
                fragmentTransaction.show(mActivity.mRadiosFragment).commit();
            }
        });
        mBtnTry.setOnClickListener(this);
    }

    private void isLoaded() {
        if (mCategoryIsLoaded && mProvinceRadiosISLoaded) {
            mBroadcastContent.setVisibility(View.VISIBLE);
            mLayoutLoading.setVisibility(View.GONE);
            mNetworkAvailable.setVisibility(View.GONE);
        } else if (mCategoryIsLoaded || mProvinceRadiosISLoaded) {
        } else {
            mNetworkAvailable.setVisibility(View.VISIBLE);
            mLayoutLoading.setVisibility(View.GONE);
            mBroadcastContent.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        mFragmentManager = mActivity.mFragmentManager;
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        switch (v.getId()) {
            case R.id.broadcast_layout_local:
                if (null == mActivity.mRadiosFragment) {
                    mActivity.mRadiosFragment = new RadiosFragment();
                }
                if (!mActivity.mRadiosFragment.isAdded()) {
                    fragmentTransaction.add(R.id.content, mActivity.mRadiosFragment);
                }
                mActivity.mRadiosFragment.setParams(RadiosFragment.RADIOTYPE.LOCAL, mProvinceCode);
                mActivity.hideContentAllFragment(fragmentTransaction);
                mActivity.setContentVisible(true);
                fragmentTransaction.show(mActivity.mRadiosFragment).commit();
                break;
            case R.id.broadcast_layout_country:
                if (null == mActivity.mRadiosFragment) {
                    mActivity.mRadiosFragment = new RadiosFragment();
                }
                if (!mActivity.mRadiosFragment.isAdded()) {
                    fragmentTransaction.add(R.id.content, mActivity.mRadiosFragment);
                }
                mActivity.mRadiosFragment.setParams(RadiosFragment.RADIOTYPE.COUNTRY, "1");
                mActivity.hideContentAllFragment(fragmentTransaction);
                mActivity.setContentVisible(true);
                fragmentTransaction.show(mActivity.mRadiosFragment).commit();
                break;
            case R.id.broadcast_layout_province:
                if (null == mActivity.mProvinceRadiosFragment) {
                    mActivity.mProvinceRadiosFragment = new ProvinceRadiosFragment();
                }
                if (!mActivity.mProvinceRadiosFragment.isAdded()) {
                    fragmentTransaction.add(R.id.content, mActivity.mProvinceRadiosFragment);
                }
                mActivity.hideContentAllFragment(fragmentTransaction);
                mActivity.setContentVisible(true);
                fragmentTransaction.show(mActivity.mProvinceRadiosFragment).commit();
                break;
            case R.id.broadcast_layout_internet:
                if (null == mActivity.mRadiosFragment) {
                    mActivity.mRadiosFragment = new RadiosFragment();
                }
                if (!mActivity.mRadiosFragment.isAdded()) {
                    fragmentTransaction.add(R.id.content, mActivity.mRadiosFragment);
                }
                mActivity.mRadiosFragment.setParams(RadiosFragment.RADIOTYPE.INTERNET, "3");
                mActivity.hideContentAllFragment(fragmentTransaction);
                mActivity.setContentVisible(true);
                fragmentTransaction.show(mActivity.mRadiosFragment).commit();
                break;
            case R.id.btn_try_agin://再试一次
                loadDate();
                break;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mFragmentManager = mActivity.mFragmentManager;
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        //分类的子条目点击事件
        if (position < mRadioCategories.size()) {
            if (null == mActivity.mRadiosByCateFragment) {
                mActivity.mRadiosByCateFragment = new RadiosByCateFragment();
            }
            if (!mActivity.mRadiosByCateFragment.isAdded()) {
                fragmentTransaction.add(R.id.content, mActivity.mRadiosByCateFragment);
            }
            mActivity.mRadiosByCateFragment.setParams(mRadioCategories.get(position));
            mActivity.hideContentAllFragment(fragmentTransaction);
            mActivity.setContentVisible(true);
            fragmentTransaction.show(mActivity.mRadiosByCateFragment).commit();
        }

    }
}
