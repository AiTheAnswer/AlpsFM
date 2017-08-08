package com.allen.user.alpsfm.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.allen.user.alpsfm.MainActivity;
import com.allen.user.alpsfm.R;
import com.allen.user.alpsfm.adapter.RecommendListAdapter;
import com.allen.user.alpsfm.utils.CategoryId2CategoryName;
import com.allen.user.alpsfm.utils.Utils;
import com.allen.user.alpsfm.view.SwipyRefreshLayout;
import com.allen.user.alpsfm.view.SwipyRefreshLayoutDirection;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackHotList;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by user on 17-2-27.
 */

public class RecommendFragment extends Fragment {

    @BindView(R.id.recommend_fragment_listView)
    ListView mListView;

    public static final String appSecret = "4d8e605fa7ed546c4bcb33dee1381179";

    @BindView(R.id.fragment_isNetworkAvailable)
    RelativeLayout mNetworkAvailable;
    @BindView(R.id.img_loading)
    ImageView mImgLoading;
    @BindView(R.id.relative_loading)
    RelativeLayout mLayoutLoading;
    @BindView(R.id.txt_loading)
    TextView txtLoading;
    @BindView(R.id.btn_try_agin)
    Button mBtnTryAgin;
    @BindView(R.id.recommend_fragment_swipe_refresh)
    com.allen.user.alpsfm.view.SwipyRefreshLayout mSwipeRefresh;
    private RecommendListAdapter mAdapter;
    private TrackHotList mTrackHotList = new TrackHotList();
    private MainActivity mActivity;
    private int mPage = 1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend, container, false);
        ButterKnife.bind(this, view);
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        initListener();
        mSwipeRefresh.setDirection(SwipyRefreshLayoutDirection.BOTTOM);
        loadTrackHotList();
    }


    private void initListener() {
        mSwipeRefresh.setColorSchemeColors(Color.BLUE, Color.RED, Color.GREEN);
        mSwipeRefresh.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if (direction == SwipyRefreshLayoutDirection.BOTTOM) {
                    loadMoreHotList();
                }
            }
        });
        mBtnTryAgin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadTrackHotList();
            }
        });
    }


    private void loadTrackHotList() {
        if (!Utils.isNetworkAvailable(getActivity())) {
            mNetworkAvailable.setVisibility(View.VISIBLE);
            mLayoutLoading.setVisibility(View.GONE);
            mSwipeRefresh.setVisibility(View.GONE);
            return;
        } else {
            mNetworkAvailable.setVisibility(View.GONE);
            mLayoutLoading.setVisibility(View.VISIBLE);
            mSwipeRefresh.setVisibility(View.GONE);
            mImgLoading.startAnimation(mActivity.mAnimation);
        }

        CommonRequest.getInstanse().init(getContext(), appSecret);
        CommonRequest.getInstanse().setUseHttps(true);
        Map<String, String> param = new HashMap<String, String>();
        param.put(DTransferConstants.CATEGORY_ID, "" + 10);
        param.put(DTransferConstants.PAGE, "" + 1);
        param.put(DTransferConstants.PAGE_SIZE, "" + 40);
        CommonRequest.getHotTracks(param, new IDataCallBack<TrackHotList>() {
                    @Override
                    public void onSuccess(TrackHotList trackHotList) {
                        mSwipeRefresh.setRefreshing(false);
                        mTrackHotList = trackHotList;
                        mLayoutLoading.setVisibility(View.GONE);
                        mSwipeRefresh.setVisibility(View.VISIBLE);
                        mListView.setVisibility(View.VISIBLE);
                        int categoryId = trackHotList.getCategoryId();
                        mActivity.mClassificationId = categoryId;
                        mAdapter = new RecommendListAdapter(mActivity, mTrackHotList);
                        mListView.setAdapter(mAdapter);
                    }

                    @Override
                    public void onError(int i, String s) {
                        Log.i("tog", "RecommendFragment---loadTrackHotList code = " + i + "----Message = " + s);
                        mLayoutLoading.setVisibility(View.GONE);
                        mSwipeRefresh.setVisibility(View.GONE);
                        mNetworkAvailable.setVisibility(View.VISIBLE);
                        Toast.makeText(mActivity, "网络异常", Toast.LENGTH_SHORT).show();
                    }
                }

        );
    }

    public void loadMoreHotList() {
        Map<String, String> param = new HashMap<String, String>();
        param.put(DTransferConstants.CATEGORY_ID, "" + 10);
        param.put(DTransferConstants.PAGE, "" + (++mPage));
        param.put(DTransferConstants.PAGE_SIZE, "" + 20);
        CommonRequest.getHotTracks(param, new IDataCallBack<TrackHotList>() {
            @Override
            public void onSuccess(TrackHotList trackHotList) {
                mSwipeRefresh.setRefreshing(false);
                if (null == trackHotList.getTracks() || trackHotList.getTracks().size() == 0) {
                    Toast.makeText(mActivity, "没有更多了", Toast.LENGTH_SHORT).show();
                    return;
                }
                mTrackHotList.getTracks().addAll(trackHotList.getTracks());
                int categoryId = trackHotList.getCategoryId();
                mActivity.mClassificationId = categoryId;
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int i, String s) {
                Log.i("tog", "RecommendFragment---loadMoreHotList code = " + i + "--Message = " + s);
                mSwipeRefresh.setRefreshing(false);
                Toast.makeText(mActivity, "网络异常", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void notifyRecommendAdapter() {
        if (null != mAdapter) {
            mAdapter.notifyDataSetChanged();
        }
    }


}
