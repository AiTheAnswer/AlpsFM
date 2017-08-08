package com.allen.user.alpsfm.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.allen.user.alpsfm.MainActivity;
import com.allen.user.alpsfm.R;
import com.allen.user.alpsfm.adapter.AlbumListAdapter;
import com.allen.user.alpsfm.utils.Utils;
import com.allen.user.alpsfm.view.SwipyRefreshLayout;
import com.allen.user.alpsfm.view.SwipyRefreshLayoutDirection;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by user on 17-3-28.
 */

public class SearchResultAlbumFragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.search_result_null)
    FrameLayout mResultNull;
    @BindView(R.id.search_result_album_lst)
    ListView mListView;
    @BindView(R.id.search_result_album_refreshlayout)
    SwipyRefreshLayout mRefreshlayout;
    @BindView(R.id.fragment_isNetworkAvailable)
    RelativeLayout mNetworkAvailable;
    @BindView(R.id.img_loading)
    ImageView mImgLoading;
    @BindView(R.id.relative_loading)
    RelativeLayout mLayoutLoading;
    @BindView(R.id.txt_relevant)
    TextView mTxtRelevant;
    @BindView(R.id.layout_relevant)
    LinearLayout mLayoutRelevant;
    @BindView(R.id.txt_new)
    TextView mTxtNew;
    @BindView(R.id.layout_new)
    LinearLayout mLayoutNew;
    @BindView(R.id.txt_play)
    TextView mTxtPlay;
    @BindView(R.id.layout_play)
    LinearLayout mLayoutPlay;
    private String mSearchWord = "  ";
    private int mPage = 1;
    private List<Album> mAlbums;
    private AlbumListAdapter mAdapter;
    private int mDimension = 4;

    public void setSearchWord(String searchWord) {
        this.mSearchWord = searchWord;
        mDimension = 4;
        mPage = 1;
        if (null != mActivity) {
            loadDate();
            resetBackgroud();
            mTxtRelevant.setTextColor(Color.WHITE);
            mTxtRelevant.setBackground(getResources().getDrawable(R.drawable.bg_serach_category_txt));
        }

    }

    private MainActivity mActivity;
    private FragmentManager mFragmentManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_result_album, null);
        ButterKnife.bind(this, view);
        mActivity = (MainActivity) getActivity();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        mRefreshlayout.setDirection(SwipyRefreshLayoutDirection.BOTH);
        mRefreshlayout.setColorSchemeColors(Color.BLUE, Color.RED, Color.GREEN);
        mFragmentManager = mActivity.mFragmentManager;
        loadDate();
        resetBackgroud();
        mTxtRelevant.setTextColor(Color.WHITE);
        mTxtRelevant.setBackground(getResources().getDrawable(R.drawable.bg_serach_category_txt));
        initListener();
    }

    private void initListener() {
        mRefreshlayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if (direction == SwipyRefreshLayoutDirection.BOTTOM) {
                    loadMoreAlbums();
                }
                if (direction == SwipyRefreshLayoutDirection.TOP) {
                    mRefreshlayout.setRefreshing(false);
                }
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Album album = mAlbums.get(position);
                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                mActivity.hideContentAllFragment(fragmentTransaction);
                if (!mActivity.mAlbumDetailsFragment.isAdded()) {
                    fragmentTransaction.add(R.id.content, mActivity.mAlbumDetailsFragment);
                }
                fragmentTransaction.show(mActivity.mAlbumDetailsFragment).commit();
                MainActivity.mStack.push(mActivity.mSearchFragment);
                mActivity.mAlbumDetailsFragment.setAlbumId(album.getId());
            }
        });
        mLayoutRelevant.setOnClickListener(this);
        mLayoutNew.setOnClickListener(this);
        mLayoutPlay.setOnClickListener(this);
    }

    private void loadDate() {
        if (!Utils.isNetworkAvailable(mActivity)) {
            mNetworkAvailable.setVisibility(View.VISIBLE);
            mLayoutLoading.setVisibility(View.GONE);
            mRefreshlayout.setVisibility(View.GONE);
            return;
        } else {
            mNetworkAvailable.setVisibility(View.GONE);
        }
        mResultNull.setVisibility(View.GONE);
        mLayoutLoading.setVisibility(View.VISIBLE);
        mImgLoading.startAnimation(mActivity.mAnimation);
        getSearchedAlbums();
    }

    private void getSearchedAlbums() {

        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.SEARCH_KEY, mSearchWord);
        map.put(DTransferConstants.CATEGORY_ID, 0 + "");
        map.put(DTransferConstants.CALC_DIMENSION, mDimension + "");
        map.put(DTransferConstants.PAGE, mPage + "");
        map.put(DTransferConstants.DISPLAY_COUNT, 20 + "");
        CommonRequest.getSearchedAlbums(map, new IDataCallBack<SearchAlbumList>() {
            @Override
            public void onSuccess(SearchAlbumList searchAlbumList) {
                mListView.setAlpha(1.0f);
                if (null == searchAlbumList || null == searchAlbumList.getAlbums() || searchAlbumList.getAlbums().size() == 0) {
                    mResultNull.setVisibility(View.VISIBLE);
                    mRefreshlayout.setVisibility(View.GONE);
                    return;
                } else {
                    mResultNull.setVisibility(View.GONE);
                }
                mLayoutLoading.setVisibility(View.GONE);
                mRefreshlayout.setVisibility(View.VISIBLE);
                mAlbums = searchAlbumList.getAlbums();
                mAdapter = new AlbumListAdapter(mAlbums, mActivity);
                mListView.setAdapter(mAdapter);
            }

            @Override
            public void onError(int i, String s) {
                mListView.setAlpha(1.0f);
                //TODO
                mLayoutLoading.setVisibility(View.GONE);
                Log.i("tog", "code = " + i + "Message = " + s);
            }
        });
    }

    private void loadMoreAlbums() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.SEARCH_KEY, mSearchWord);
        map.put(DTransferConstants.CALC_DIMENSION, mDimension + "");
        map.put(DTransferConstants.CATEGORY_ID, 0 + "");
        map.put(DTransferConstants.PAGE, ++mPage + "");
        map.put(DTransferConstants.DISPLAY_COUNT, 20 + "");
        CommonRequest.getSearchedAlbums(map, new IDataCallBack<SearchAlbumList>() {
            @Override
            public void onSuccess(SearchAlbumList searchAlbumList) {
                mRefreshlayout.setRefreshing(false);
                if (null == searchAlbumList || null == searchAlbumList.getAlbums() || searchAlbumList.getAlbums().size() < 1) {
                    Toast.makeText(mActivity, "没有更多了", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAlbums.addAll(searchAlbumList.getAlbums());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int i, String s) {
                mRefreshlayout.setRefreshing(false);
                //TODO
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_relevant://相关度
                resetBackgroud();
                mDimension = 4;
                mTxtRelevant.setTextColor(Color.WHITE);
                mTxtRelevant.setBackground(getResources().getDrawable(R.drawable.bg_serach_category_txt));
                getSearchedAlbums();
                mListView.setAlpha(0.5f);
                break;
            case R.id.layout_new://最新上传
                resetBackgroud();
                mDimension = 2;
                mTxtNew.setTextColor(Color.WHITE);
                mTxtNew.setBackground(getResources().getDrawable(R.drawable.bg_serach_category_txt));
                getSearchedAlbums();
                mListView.setAlpha(0.5f);
                break;
            case R.id.layout_play://最多播放
                resetBackgroud();
                mDimension = 3;
                mTxtPlay.setTextColor(Color.WHITE);
                mTxtPlay.setBackground(getResources().getDrawable(R.drawable.bg_serach_category_txt));
                getSearchedAlbums();
                mListView.setAlpha(0.5f);
                break;


        }
    }

    private void resetBackgroud() {
        int transparent = getResources().getColor(R.color.transparent);
        int grey = getResources().getColor(R.color.search_gray);
        mTxtRelevant.setBackgroundColor(transparent);
        mTxtRelevant.setTextColor(grey);
        mTxtNew.setBackgroundColor(transparent);
        mTxtNew.setTextColor(grey);
        mTxtPlay.setBackgroundColor(transparent);
        mTxtPlay.setTextColor(grey);

    }
}
