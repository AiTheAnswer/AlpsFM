package com.allen.user.alpsfm.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.allen.user.alpsfm.adapter.AlbumTrackAdapter;
import com.allen.user.alpsfm.utils.ImageLoader;
import com.allen.user.alpsfm.utils.Utils;
import com.allen.user.alpsfm.view.SwipyRefreshLayout;
import com.allen.user.alpsfm.view.SwipyRefreshLayoutDirection;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.track.AnnouncerTrackList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by user on 17-3-10.
 */

public class AlbumTrackFragment extends Fragment implements AdapterView.OnItemClickListener {

    @BindView(R.id.album_track_lv)
    ListView mListView;
    @BindView(R.id.toolbar_title)
    TextView mTitle;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.bottom_line)
    View mBottomLine;
    @BindView(R.id.album_track_refresh_layout)
    SwipyRefreshLayout mRefreshLayout;
    @BindView(R.id.fragment_isNetworkAvailable)
    RelativeLayout mNetworkAvailable;
    @BindView(R.id.img_loading)
    ImageView mImgLoading;
    @BindView(R.id.relative_loading)
    RelativeLayout mLayoutLoading;
    @BindView(R.id.album_tracks_content)
    LinearLayout mTracksContent;
    private List<Track> mTracks;
    private AlbumTrackAdapter mAdapter;
    private MainActivity mActivity;
    private long mAlbumId;
    private long mAnnouncerId;
    private boolean isTitle = true;
    private int mPage = 1;

    public AlbumTrackFragment() {
    }

    public void setAlbumId(long albumId) {
        isTitle = false;
        mAlbumId = albumId;
        mPage = 1;
        if (null != mActivity) {
            if (!Utils.isNetworkAvailable(mActivity)) {
                mNetworkAvailable.setVisibility(View.VISIBLE);
                mLayoutLoading.setVisibility(View.GONE);
                mTracksContent.setVisibility(View.GONE);
            } else {
                loadTracks();
            }
        }
    }


    public void setAnnouncerId(long announcerId) {
        isTitle = true;
        this.mAnnouncerId = announcerId;
        this.mPage = 1;
        if (null != mActivity) {
            loadAnnoucerTrack();
            mToolbar.setVisibility(View.VISIBLE);
            mTitle.setText("全部声音");

        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album_track, null);
        ButterKnife.bind(this, view);
        mActivity = (MainActivity) getActivity();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!isTitle) {
            mToolbar.setVisibility(View.GONE);
            mBottomLine.setVisibility(View.GONE);
            loadTracks();
        } else {
            mToolbar.setVisibility(View.VISIBLE);
            mBottomLine.setVisibility(View.VISIBLE);
            mTitle.setText("全部声音");
            loadAnnoucerTrack();
        }
        mRefreshLayout.setColorSchemeColors(Color.BLUE, Color.RED, Color.GREEN);
        mRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTTOM);
        initListener();

    }

    public void notifyAdapter() {
        mAdapter.notifyDataSetChanged();
    }

    private void initListener() {
        mRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                /*if (direction == SwipyRefreshLayoutDirection.TOP) {//下拉刷新
                    if (!isTitle) {
                        loadTracks();
                    } else {
                        loadAnnoucerTrack();
                    }
                }*/
                if (direction == SwipyRefreshLayoutDirection.BOTTOM) {//上啦加载更多
                    if (!isTitle) {
                        loadMore();
                    } else {
                        loadMoreAnnourcerTrack();
                    }
                }
            }
        });

        mListView.setOnItemClickListener(this);
    }

    private void loadAnnoucerTrack() {
        if (!Utils.isNetworkAvailable(mActivity)) {
            mNetworkAvailable.setVisibility(View.VISIBLE);
            mLayoutLoading.setVisibility(View.GONE);
            mTracksContent.setVisibility(View.GONE);
            mRefreshLayout.setRefreshing(false);
            return;
        } else {
            mNetworkAvailable.setVisibility(View.GONE);
            mLayoutLoading.setVisibility(View.VISIBLE);
            mImgLoading.startAnimation(mActivity.mAnimation);
            mTracksContent.setVisibility(View.GONE);
        }
        Map<String, String> maps = new HashMap<String, String>();
        maps.put(DTransferConstants.AID, mAnnouncerId + "");
        CommonRequest.getTracksByAnnouncer(maps, new IDataCallBack<AnnouncerTrackList>() {
            @Override
            public void onSuccess(AnnouncerTrackList announcerTrackList) {
                mRefreshLayout.setRefreshing(false);
                mLayoutLoading.setVisibility(View.GONE);
                List<Track> tracks = announcerTrackList.getTracks();
                mTracks = tracks;
                mAdapter = new AlbumTrackAdapter(mActivity, tracks);
                mListView.setAdapter(mAdapter);
                mTracksContent.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(int i, String s) {
                mRefreshLayout.setRefreshing(false);
            }
        });

    }

    private void loadMoreAnnourcerTrack() {
        Map<String, String> maps = new HashMap<String, String>();
        maps.put(DTransferConstants.AID, mAnnouncerId + "");
        maps.put(DTransferConstants.PAGE, ++mPage + "");
        maps.put(DTransferConstants.PAGE_SIZE, 10 + "");
        CommonRequest.getTracksByAnnouncer(maps, new IDataCallBack<AnnouncerTrackList>() {
            @Override
            public void onSuccess(AnnouncerTrackList announcerTrackList) {
                mRefreshLayout.setRefreshing(false);
                if (announcerTrackList.getTracks().size() == 0) {
                    Toast.makeText(mActivity, "没有更多了", Toast.LENGTH_SHORT).show();
                    return;
                }
                mTracks.addAll(announcerTrackList.getTracks());
                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onError(int i, String s) {
                mRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void loadTracks() {
        if (!Utils.isNetworkAvailable(mActivity)) {
            mRefreshLayout.setRefreshing(false);
            mNetworkAvailable.setVisibility(View.VISIBLE);
            mLayoutLoading.setVisibility(View.GONE);
            mTracksContent.setVisibility(View.GONE);
            return;
        } else {
            mNetworkAvailable.setVisibility(View.GONE);
            mLayoutLoading.setVisibility(View.VISIBLE);
            mTracksContent.setVisibility(View.GONE);
            mImgLoading.startAnimation(mActivity.mAnimation);
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ALBUM_ID, mAlbumId + "");//具体的某一个专辑Id
        map.put(DTransferConstants.SORT, "asc");
        map.put(DTransferConstants.PAGE, 1 + "");
        map.put(DTransferConstants.PAGE_SIZE, 20 + "");
        CommonRequest.getTracks(map, new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                mRefreshLayout.setRefreshing(false);
                if (null == trackList) {
                    return;
                }
                List<Track> tracks = trackList.getTracks();
                mTracks = tracks;
                mLayoutLoading.setVisibility(View.GONE);
                mAdapter = new AlbumTrackAdapter(mActivity, mTracks);
                mListView.setAdapter(mAdapter);
                mTracksContent.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(int i, String s) {
                Log.e("getTracks", i + "----" + s);
            }
        });
    }

    private void loadMore() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ALBUM_ID, mAlbumId + "");//具体的某一个专辑Id
        map.put(DTransferConstants.SORT, "asc");
        map.put(DTransferConstants.PAGE, ++mPage + "");
        map.put(DTransferConstants.PAGE_SIZE, 20 + "");
        CommonRequest.getTracks(map, new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                mRefreshLayout.setRefreshing(false);
                if (null == trackList) {
                    return;
                }
                if (trackList.getTracks().size() == 0) {
                    Toast.makeText(mActivity, "没有更多了", Toast.LENGTH_SHORT).show();
                    return;
                }
                List<Track> tracks = trackList.getTracks();
                mTracks.addAll(tracks);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int i, String s) {
                Log.e("getTracks", i + "----" + s);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mActivity.playList(mTracks, position);
        mActivity.setContentVisible(false);
        if (isTitle) {
            MainActivity.mStack.push(mActivity.mAllTrackFragment);
        } else {
            MainActivity.mStack.push(mActivity.mAlbumDetailsFragment);//加入栈
        }
        mActivity.switchState(MainActivity.STATE.PLAYER);
    }
}
