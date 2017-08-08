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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.allen.user.alpsfm.MainActivity;
import com.allen.user.alpsfm.R;
import com.allen.user.alpsfm.adapter.RankAlbumAdapter;
import com.allen.user.alpsfm.utils.Utils;
import com.allen.user.alpsfm.view.SwipyRefreshLayout;
import com.allen.user.alpsfm.view.SwipyRefreshLayoutDirection;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.ranks.RankAlbumList;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by user on 17-3-15.
 */

public class RankAlbumFragment extends Fragment implements AdapterView.OnItemClickListener {
    @BindView(R.id.toolbar_title)
    TextView mTitle;
    @BindView(R.id.fragment_rank_album_lv)
    ListView mListView;
    @BindView(R.id.fragment_isNetworkAvailable)
    RelativeLayout mNetworkAvailable;
    @BindView(R.id.img_loading)
    ImageView mImgLoading;
    @BindView(R.id.relative_loading)
    RelativeLayout mLayoutLoading;
    @BindView(R.id.rank_album_content)
    LinearLayout mRankAlbumContent;
    @BindView(R.id.refresh_layout)
    SwipyRefreshLayout mRefreshLayout;
    private RankAlbumAdapter mAdapter;
    private MainActivity mActivity;
    private FragmentManager mFragmentManager;
    private String mRankTitle;
    private String mAlbumkey;
    private RankAlbumList mRankAlbumList;
    private int mPage = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rank_album, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        loadRankAlbumList();
        mFragmentManager = mActivity.mFragmentManager;
        mRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTH);
        mRefreshLayout.setColorSchemeColors(Color.BLUE, Color.RED, Color.GREEN);
        initListener();
    }

    private void initListener() {
        mRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if (direction == SwipyRefreshLayoutDirection.TOP) {
                    loadRankAlbumList();
                }
                if (direction == SwipyRefreshLayoutDirection.BOTTOM) {
                    loadMoreRankAlbumList();
                }
            }
        });
        mListView.setOnItemClickListener(this);
    }

    private void loadMoreRankAlbumList() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.PAGE, ++mPage + "");
        map.put(DTransferConstants.PAGE_SIZE, 10 + "");
        map.put(DTransferConstants.RANK_KEY, mAlbumkey);
        CommonRequest.getRankAlbumList(map, new IDataCallBack<RankAlbumList>() {
            @Override
            public void onSuccess(RankAlbumList rankAlbumList) {
                mRefreshLayout.setRefreshing(false);
                if (rankAlbumList.getRankAlbumList().size() == 0) {
                    Toast.makeText(mActivity, "没有更多了", Toast.LENGTH_SHORT).show();
                    return;
                }
                mRankAlbumList.getRankAlbumList().addAll(rankAlbumList.getRankAlbumList());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int i, String s) {
                mRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void setParames(final String rankTitle, String rankAlbumKey) {
        mPage = 1;
        mRankTitle = rankTitle;
        mAlbumkey = rankAlbumKey;
        if (null != mActivity) {
            loadRankAlbumList();
        }

    }

    private void loadRankAlbumList() {
        if (!Utils.isNetworkAvailable(mActivity)) {
            mNetworkAvailable.setVisibility(View.VISIBLE);
            mLayoutLoading.setVisibility(View.GONE);
            mRankAlbumContent.setVisibility(View.GONE);
            mRefreshLayout.setRefreshing(false);
            return;
        } else {
            mNetworkAvailable.setVisibility(View.GONE);
            mLayoutLoading.setVisibility(View.VISIBLE);
            mImgLoading.startAnimation(mActivity.mAnimation);
        }
        mRankAlbumContent.setVisibility(View.GONE);
        if (mAlbumkey.contains("album")) {//某榜单下r专辑列表
            Map<String, String> map = new HashMap<String, String>();
            map.put(DTransferConstants.PAGE, 1 + "");
            map.put(DTransferConstants.PAGE_SIZE, 20 + "");
            map.put(DTransferConstants.RANK_KEY, mAlbumkey);
            CommonRequest.getRankAlbumList(map,
                    new IDataCallBack<RankAlbumList>() {
                        @Override
                        public void onSuccess(RankAlbumList rankAlbumList) {
                            mLayoutLoading.setVisibility(View.GONE);
                            mRefreshLayout.setRefreshing(false);
                            mRankAlbumList = rankAlbumList;
                            mAdapter = new RankAlbumAdapter(getActivity(), mRankAlbumList);
                            mListView.setAdapter(mAdapter);
                            mRankAlbumContent.setVisibility(View.VISIBLE);
                            mTitle.setText(mRankTitle);
                        }

                        @Override
                        public void onError(int i, String s) {
                            mRefreshLayout.setRefreshing(false);
                            //Toast.makeText(getActivity(), "加载失败：　code = " + i + "Message　=" + s, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Album album = (Album) mAdapter.getItem(position);
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        if (!mActivity.mAlbumDetailsFragment.isAdded()) {
            fragmentTransaction.add(R.id.content, mActivity.mAlbumDetailsFragment);
        }
        mActivity.mAlbumDetailsFragment.setAlbumId(album.getId());
        mActivity.hideContentAllFragment(fragmentTransaction);
        fragmentTransaction.show(mActivity.mAlbumDetailsFragment).commit();
        MainActivity.mStack.clear();//清空栈
        MainActivity.mStack.push(mActivity.mRankAlbumFragment);

    }
}
