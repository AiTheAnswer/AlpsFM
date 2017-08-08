package com.allen.user.alpsfm.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.allen.user.alpsfm.MainActivity;
import com.allen.user.alpsfm.R;
import com.allen.user.alpsfm.adapter.AnchorListViewAdapter;
import com.allen.user.alpsfm.utils.Utils;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.announcer.AnnouncerCategoryList;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by user on 17-2-27.
 */

public class AnchorFragment extends Fragment {
    @BindView(R.id.fragment_anchor_lv)
    ListView mListView;
    @BindView(R.id.btn_try_agin)
    Button mBtnTry;
    @BindView(R.id.fragment_isNetworkAvailable)
    RelativeLayout mNetworkAvailable;
    @BindView(R.id.img_loading)
    ImageView mImgLoading;
    @BindView(R.id.relative_loading)
    RelativeLayout mLayoutLoading;
    private AnchorListViewAdapter mAdapter;
    private MainActivity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_anchor, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        getAnchorCategoryList();
        mBtnTry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAnchorCategoryList();
            }
        });
    }

    public void getAnchorCategoryList() {
        if (!Utils.isNetworkAvailable(mActivity)) {
            mNetworkAvailable.setVisibility(View.VISIBLE);
            mLayoutLoading.setVisibility(View.GONE);
            mListView.setVisibility(View.GONE);
            return;
        } else {
            mNetworkAvailable.setVisibility(View.GONE);
            mLayoutLoading.setVisibility(View.VISIBLE);
            mImgLoading.startAnimation(mActivity.mAnimation);
            mListView.setVisibility(View.GONE);
        }
        Map<String, String> map = new HashMap<String, String>();
        CommonRequest.getAnnouncerCategoryList(map, new IDataCallBack<AnnouncerCategoryList>() {
            @Override
            public void onSuccess(AnnouncerCategoryList announcerCategoryList) {
                mLayoutLoading.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
                mAdapter = new AnchorListViewAdapter(mActivity, announcerCategoryList);
                mListView.setAdapter(mAdapter);
            }

            @Override
            public void onError(int i, String s) {
                Log.i("tog", "getAnnouncerCategoryList -- > onError -- code = " + i + "Message = " + s);
                Toast.makeText(mActivity, "网络异常", Toast.LENGTH_SHORT).show();
                mNetworkAvailable.setVisibility(View.VISIBLE);
                mLayoutLoading.setVisibility(View.GONE);
                mListView.setVisibility(View.GONE);
            }
        });
    }
}
