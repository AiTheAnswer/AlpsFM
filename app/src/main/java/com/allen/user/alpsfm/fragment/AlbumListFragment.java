package com.allen.user.alpsfm.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.allen.user.alpsfm.adapter.AlbumListAdapter;
import com.allen.user.alpsfm.utils.Utils;
import com.allen.user.alpsfm.view.SwipyRefreshLayout;
import com.allen.user.alpsfm.view.SwipyRefreshLayoutDirection;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by user on 17-3-8.
 */

public class AlbumListFragment extends Fragment implements AdapterView.OnItemClickListener {

    @BindView(R.id.album_list_lv)
    ListView mListView;
    @BindView(R.id.toolbar_title)
    TextView mTitle;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fragment_isNetworkAvailable)
    RelativeLayout mNetworkAvailable;
    @BindView(R.id.img_loading)
    ImageView mImgLoading;
    @BindView(R.id.relative_loading)
    RelativeLayout mLayoutLoading;
    @BindView(R.id.fragment_album_list_content)
    LinearLayout mAlbumListContent;
    @BindView(R.id.album_list_refresh_layout)
    SwipyRefreshLayout mRefreshLayout;

    private MainActivity mActivity;
    private String mTagName;
    private AlbumListAdapter mAdapter;
    private long mClassificationId;
    private FragmentManager mFragmentManager;
    private AlbumList mAlbumList;
    private boolean isTag = true;
    private AlbumList mDates;
    private int mPage = 1;
    private long mAnnouncerId;

    public AlbumListFragment(String tagName) {
        this.mTagName = tagName;
        this.isTag = true;
    }

    public AlbumListFragment() {
        this.isTag = false;
    }

    public String getTagName() {
        return mTagName;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album_list, null);
        ButterKnife.bind(this, view);
        mActivity = (MainActivity) getActivity();
        mClassificationId = mActivity.mClassificationId;
        mFragmentManager = mActivity.mFragmentManager;
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!Utils.isNetworkAvailable(mActivity)) {
            mNetworkAvailable.setVisibility(View.VISIBLE);
            mLayoutLoading.setVisibility(View.GONE);
            mAlbumListContent.setVisibility(View.GONE);
        } else {
            mNetworkAvailable.setVisibility(View.GONE);
            mLayoutLoading.setVisibility(View.VISIBLE);
            mImgLoading.startAnimation(mActivity.mAnimation);
            if (isTag) {
                mToolbar.setVisibility(View.GONE);
                getAlbumList();

            } else {
                mToolbar.setVisibility(View.VISIBLE);
                mTitle.setText("全部专辑");
                getAnnouncerAlbums();
            }
        }
        mRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTH);
        mRefreshLayout.setColorSchemeColors(Color.BLUE, Color.RED, Color.GREEN);
        initListener();
    }

    private void initListener() {
        mListView.setOnItemClickListener(this);
        mRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if (direction == SwipyRefreshLayoutDirection.TOP) {//刷新
                    if (isTag) {
                        getAlbumList();
                    } else {
                        getAnnouncerAlbums();
                    }
                }
                if (direction == SwipyRefreshLayoutDirection.BOTTOM) {//加载更多
                    if (isTag) {
                        loadMoreAlbumList();
                    } else {
                        loadMoreAnnouncerAlbums();
                    }
                }
            }
        });
    }

    public void setAnnouncerId(long announcerId) {
        this.mAnnouncerId = announcerId;
        if (null != mActivity) {
            getAnnouncerAlbums();
        }
    }

    //根据主播的id来获取专辑列表
    private void getAnnouncerAlbums() {
        if (!Utils.isNetworkAvailable(mActivity)) {
            mNetworkAvailable.setVisibility(View.VISIBLE);
            mLayoutLoading.setVisibility(View.GONE);
            mAlbumListContent.setVisibility(View.GONE);
            return;
        } else {
            mNetworkAvailable.setVisibility(View.GONE);
            mLayoutLoading.setVisibility(View.VISIBLE);
            mAlbumListContent.setVisibility(View.GONE);
            mImgLoading.startAnimation(mActivity.mAnimation);
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.AID, mAnnouncerId + "");
        map.put(DTransferConstants.PAGE, "1");
        map.put(DTransferConstants.PAGE_SIZE, "20");
        CommonRequest.getAlbumsByAnnouncer(map, new IDataCallBack<AlbumList>() {
            @Override
            public void onSuccess(AlbumList albumList) {
                mRefreshLayout.setRefreshing(false);
                mLayoutLoading.setVisibility(View.GONE);
                mAlbumList = albumList;
                mAdapter = new AlbumListAdapter(mAlbumList, mActivity);
                mListView.setAdapter(mAdapter);
                mAlbumListContent.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(int i, String s) {
                mLayoutLoading.setVisibility(View.GONE);
                Toast.makeText(mActivity, "加载失败：code = " + i + "Message = " + s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loadMoreAnnouncerAlbums() {
        if (!Utils.isNetworkAvailable(mActivity)) {
            mNetworkAvailable.setVisibility(View.VISIBLE);
            mAlbumListContent.setVisibility(View.GONE);
            mLayoutLoading.setVisibility(View.GONE);
            mRefreshLayout.setRefreshing(false);
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.AID, mAnnouncerId + "");
        map.put(DTransferConstants.PAGE, ++mPage + "");
        map.put(DTransferConstants.PAGE_SIZE, "20");
        CommonRequest.getAlbumsByAnnouncer(map, new IDataCallBack<AlbumList>() {
            @Override
            public void onSuccess(AlbumList albumList) {
                mRefreshLayout.setRefreshing(false);
                mLayoutLoading.setVisibility(View.GONE);
                if (albumList.getAlbums().size() == 0) {
                    Toast.makeText(mActivity, "没有更多了", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAlbumList.getAlbums().addAll(albumList.getAlbums());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int i, String s) {
                mLayoutLoading.setVisibility(View.GONE);
                mRefreshLayout.setRefreshing(false);
                Toast.makeText(mActivity, "加载失败：code = " + i + "Message = " + s, Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 通过专辑Id和标签来获取专辑列表
     */
    public void getAlbumList() {
        if (!Utils.isNetworkAvailable(mActivity)) {
            mNetworkAvailable.setVisibility(View.VISIBLE);
            mAlbumListContent.setVisibility(View.GONE);
            mLayoutLoading.setVisibility(View.GONE);
            return;
        }
        mAlbumListContent.setVisibility(View.GONE);
        mLayoutLoading.setVisibility(View.VISIBLE);
        mImgLoading.startAnimation(mActivity.mAnimation);
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.CATEGORY_ID, mClassificationId + "");//专辑Id　分类ID，指定分类，为0时表示热门分类
        map.put(DTransferConstants.TAG_NAME, mTagName);
        map.put(DTransferConstants.CALC_DIMENSION, "1");//计算维度，现支持最火（1），最新（2），经典或播放最多（3）
        map.put(DTransferConstants.PAGE, mPage + "");
        map.put(DTransferConstants.PAGE_SIZE, 30 + "");
        CommonRequest.getAlbumList(map, new IDataCallBack<AlbumList>() {
            @Override
            public void onSuccess(AlbumList albumList) {
                mDates = albumList;
                mRefreshLayout.setRefreshing(false);
                mLayoutLoading.setVisibility(View.GONE);
                mAdapter = new AlbumListAdapter(mDates, mActivity);
                mListView.setAdapter(mAdapter);
                mAlbumListContent.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(int i, String s) {
                Log.e("AlbumList", "错误码 = " + i + "---Error--" + s);
            }
        });

    }

    private void loadMoreAlbumList() {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.CATEGORY_ID, mClassificationId + "");//专辑Id　分类ID，指定分类，为0时表示热门分类
        map.put(DTransferConstants.TAG_NAME, mTagName);
        map.put(DTransferConstants.CALC_DIMENSION, "1");//计算维度，现支持最火（1），最新（2），经典或播放最多（3）
        map.put(DTransferConstants.PAGE, ++mPage + "");
        map.put(DTransferConstants.PAGE_SIZE, 20 + "");
        CommonRequest.getAlbumList(map, new IDataCallBack<AlbumList>() {
            @Override
            public void onSuccess(AlbumList albumList) {
                mRefreshLayout.setRefreshing(false);
                if (albumList.getAlbums().size() == 0) {
                    Toast.makeText(mActivity, "没有更多了", Toast.LENGTH_SHORT).show();
                    return;
                }
                mDates.getAlbums().addAll(albumList.getAlbums());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int i, String s) {
                Log.e("AlbumList", "错误码 = " + i + "---Error--" + s);
            }
        });
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Album album = (Album) mAdapter.getItem(position);
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        mActivity.hideContentAllFragment(fragmentTransaction);
        if (!mActivity.mAlbumDetailsFragment.isAdded()) {
            fragmentTransaction.add(R.id.content, mActivity.mAlbumDetailsFragment);
        }
        fragmentTransaction.show(mActivity.mAlbumDetailsFragment).commit();
        if (!isTag) {
            MainActivity.mStack.push(mActivity.mAllAlbumListFragment);
        } else {
            MainActivity.mStack.push(mActivity.mAlbumFragment);
        }
        mActivity.mAlbumDetailsFragment.setAlbumId(album.getId());
    }
}
