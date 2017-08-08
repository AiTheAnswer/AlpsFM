package com.allen.user.alpsfm.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.allen.user.alpsfm.MainActivity;
import com.allen.user.alpsfm.R;
import com.allen.user.alpsfm.adapter.RankTrackAdapter;
import com.allen.user.alpsfm.utils.Utils;
import com.allen.user.alpsfm.view.SwipyRefreshLayout;
import com.allen.user.alpsfm.view.SwipyRefreshLayoutDirection;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.ranks.RankTrackList;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by user on 17-3-15.
 */

public class RankTrackFragment extends Fragment implements AdapterView.OnItemClickListener {
    @BindView(R.id.toolbar_title)
    TextView mTitle;
    @BindView(R.id.fragment_rank_track_lv)
    ListView mListView;
    @BindView(R.id.fragment_isNetworkAvailable)
    RelativeLayout mNetworkAvailable;
    @BindView(R.id.img_loading)
    ImageView mImgLoading;
    @BindView(R.id.relative_loading)
    RelativeLayout mLayoutLoading;
    @BindView(R.id.refresh_layout)
    SwipyRefreshLayout mRefreshLayout;
    @BindView(R.id.rank_track_content)
    LinearLayout mRankTrackContent;
    private RankTrackAdapter mAdapter;
    private MainActivity mAtivity;
    private List<Track> mTrackList;
    private int mPage = 1;
    private String mRankTrackKey;
    private RankTrackList mRankTrackList;
    private String mTitleText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rank_track, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAtivity = (MainActivity) getActivity();
        getRankTrackList();
        mRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTH);
        mRefreshLayout.setColorSchemeColors(Color.BLUE, Color.RED, Color.GREEN);
        initListener();
    }

    private void initListener() {
        mRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if (direction == SwipyRefreshLayoutDirection.TOP) {
                    getRankTrackList();
                }
                if (direction == SwipyRefreshLayoutDirection.BOTTOM) {
                    loadMoreTrackList();
                }

            }
        });
        mListView.setOnItemClickListener(this);
    }

    public void notifyAdapter() {
        if (null != mAdapter) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void loadMoreTrackList() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.RANK_KEY, mRankTrackKey);
        map.put(DTransferConstants.PAGE, ++mPage + "");
        map.put(DTransferConstants.PAGE_SIZE, 20 + "");
        CommonRequest.getRankTrackList(map,
                new IDataCallBack<RankTrackList>() {
                    @Override
                    public void onSuccess(RankTrackList rankTrackList) {
                        mRefreshLayout.setRefreshing(false);
                        if (null != rankTrackList || rankTrackList.getTrackList().size() == 0) {
                            Toast.makeText(mAtivity, "没有更多了", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mRankTrackList.getTrackList().addAll(rankTrackList.getTrackList());
                        mTrackList = mRankTrackList.getTrackList();
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(int i, String s) {
                        mRefreshLayout.setRefreshing(false);
                        //Toast.makeText(getActivity(), "加载失败: code = " + i + "Message=" + s, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void setParames(final String rankTitle, String rankTrackKey) {
        this.mRankTrackKey = rankTrackKey;
        this.mPage = 1;
        this.mTitleText = rankTitle;
        if (null != mAtivity) {
            getRankTrackList();
        }

    }

    private void getRankTrackList() {
        if (!Utils.isNetworkAvailable(mAtivity)) {
            mNetworkAvailable.setVisibility(View.VISIBLE);
            mLayoutLoading.setVisibility(View.GONE);
            mRefreshLayout.setRefreshing(false);
            mRankTrackContent.setVisibility(View.GONE);
            return;
        } else {
            mNetworkAvailable.setVisibility(View.GONE);
            mLayoutLoading.setVisibility(View.VISIBLE);
            mRankTrackContent.setVisibility(View.GONE);
            mImgLoading.startAnimation(mAtivity.mAnimation);
        }
        if (mRankTrackKey.contains("track")) {//某榜单下声音列表
            Map<String, String> map = new HashMap<String, String>();
            map.put(DTransferConstants.RANK_KEY, mRankTrackKey);
            map.put(DTransferConstants.PAGE, mPage + "");
            map.put(DTransferConstants.PAGE_SIZE, 20 + "");
            CommonRequest.getRankTrackList(map,
                    new IDataCallBack<RankTrackList>() {
                        @Override
                        public void onSuccess(RankTrackList rankTrackList) {
                            mRefreshLayout.setRefreshing(false);
                            mLayoutLoading.setVisibility(View.GONE);
                            mRankTrackList = rankTrackList;
                            mTrackList = mRankTrackList.getTrackList();
                            mAdapter = new RankTrackAdapter(mAtivity, mRankTrackList);
                            mListView.setAdapter(mAdapter);
                            mTitle.setText(mTitleText);
                            mRankTrackContent.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(int i, String s) {
                            mRefreshLayout.setRefreshing(false);
                            //Toast.makeText(getActivity(), "加载失败: code = " + i + "Message=" + s, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mAtivity.playList(mTrackList, position);
        mAtivity.setContentVisible(false);
        MainActivity.mStack.clear();//清空栈
        MainActivity.mStack.push(mAtivity.mRankTrackFragment);
        mAtivity.switchState(MainActivity.STATE.PLAYER);
    }
}
