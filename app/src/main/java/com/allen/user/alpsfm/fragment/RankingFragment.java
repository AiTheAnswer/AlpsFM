package com.allen.user.alpsfm.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.allen.user.alpsfm.MainActivity;
import com.allen.user.alpsfm.R;
import com.allen.user.alpsfm.adapter.RankingAdapter;
import com.allen.user.alpsfm.utils.Utils;
import com.allen.user.alpsfm.view.SwipyRefreshLayout;
import com.allen.user.alpsfm.view.SwipyRefreshLayoutDirection;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.ranks.Rank;
import com.ximalaya.ting.android.opensdk.model.ranks.RankList;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by user on 17-2-27.
 */

public class RankingFragment extends Fragment implements AdapterView.OnItemClickListener {
    @BindView(R.id.ranking_fragment_lv)
    ListView mListView;
    @BindView(R.id.fragment_isNetworkAvailable)
    RelativeLayout mNetworkAvailable;
    @BindView(R.id.img_loading)
    ImageView mImgLoading;
    @BindView(R.id.relative_loading)
    RelativeLayout mLayoutLoading;
    @BindView(R.id.btn_try_agin)
    Button mBtnTry;
    private RankingAdapter mAdapter;
    private MainActivity mActivity;
    private FragmentManager mFragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        loadDate();
        initListener();
    }

    private void initListener() {
        mBtnTry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDate();
            }
        });
        mListView.setOnItemClickListener(this);
    }

    private void loadDate() {
        if (!Utils.isNetworkAvailable(mActivity)) {
            mNetworkAvailable.setVisibility(View.VISIBLE);
            mLayoutLoading.setVisibility(View.GONE);
            mListView.setVisibility(View.GONE);
            return;
        } else {
            mNetworkAvailable.setVisibility(View.GONE);
            mLayoutLoading.setVisibility(View.VISIBLE);
            mImgLoading.startAnimation(mActivity.mAnimation);
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.RANK_TYPE, 1 + "");
        CommonRequest.getRankList(map, new IDataCallBack<RankList>() {
            @Override
            public void onSuccess(RankList rankList) {
                if (null == rankList) {
                    return;
                }
                mLayoutLoading.setVisibility(View.GONE);
                mAdapter = new RankingAdapter(getActivity(), rankList);
                mListView.setAdapter(mAdapter);
                mListView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(int i, String s) {
                mLayoutLoading.setVisibility(View.GONE);
                mNetworkAvailable.setVisibility(View.VISIBLE);
                mListView.setVisibility(View.GONE);
                Toast.makeText(mActivity, "网络异常", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Rank rank = (Rank) mAdapter.getItem(position);
        String rankKey = rank.getRankKey();
        String rankTitle = rank.getRankTitle();
        if (null == rankKey || "".equals(rankKey)) {
            return;
        }
        if (null == mActivity.mRankTrackFragment) {
            mActivity.mRankTrackFragment = new RankTrackFragment();
            mActivity.mRankAlbumFragment = new RankAlbumFragment();
        }
        mFragmentManager = mActivity.mFragmentManager;
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        mActivity.hideContentAllFragment(fragmentTransaction);
        if (rankKey.contains("track")) {
            mActivity.mRankTrackFragment.setParames(rankTitle, rankKey);
            if (!mActivity.mRankTrackFragment.isAdded()) {
                fragmentTransaction.add(R.id.content, mActivity.mRankTrackFragment);
            }
            fragmentTransaction.show(mActivity.mRankTrackFragment).commit();
        }
        if (rankKey.contains("album")) {
            if (!mActivity.mRankAlbumFragment.isAdded()) {
                fragmentTransaction.add(R.id.content, mActivity.mRankAlbumFragment);
            }
            mActivity.mRankAlbumFragment.setParames(rankTitle, rankKey);
            fragmentTransaction.show(mActivity.mRankAlbumFragment).commit();
        }
        mActivity.setContentVisible(true);
    }
}
