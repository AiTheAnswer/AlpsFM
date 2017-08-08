package com.allen.user.alpsfm.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.allen.user.alpsfm.MainActivity;
import com.allen.user.alpsfm.R;
import com.allen.user.alpsfm.utils.ImageLoader;
import com.allen.user.alpsfm.utils.Utils;
import com.pkmmte.view.CircularImageView;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;
import com.ximalaya.ting.android.opensdk.model.track.AnnouncerTrackList;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by user on 17-3-16.
 */

public class AnchorDetailsFragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.fragment_anchor_details_icon)
    ImageView mAnchoIcon;
    @BindView(R.id.fragment_anchor_details_name)
    TextView mAnchorName;
    @BindView(R.id.fragment_anchor_details_anchor_position)
    TextView mAnchorPosition;
    @BindView(R.id.fragment_anchor_details_follow_num)
    TextView mFollowNum;
    @BindView(R.id.fragment_anchor_details_fans_num)
    TextView mFansNum;
    @BindView(R.id.fragment_anchor_details_info)
    TextView mAnchorInfo;
    @BindView(R.id.fragment_anchor_details_album_num)
    TextView mAlbumNum;
    @BindView(R.id.fragment_anchor_details_album_icon)
    ImageView mAlbumIcon;
    @BindView(R.id.fragment_anchor_details_album_title)
    TextView mAlbumTitle;
    @BindView(R.id.fragment_anchor_details_album_info)
    TextView mAlbumInfo;
    @BindView(R.id.fragment_anchor_details_album_size)
    TextView mAlbumSize;
    @BindView(R.id.fragment_anchor_details_track_count)
    TextView mTrackCount;
    @BindView(R.id.fragment_anchor_details_track_icon1)
    CircularImageView mTrackIcon1;
    @BindView(R.id.fragment_anchor_details_track_name1)
    TextView mTrackName1;
    @BindView(R.id.item_img_album_track_play_icon1)
    ImageView mTrackPlayIcon1;
    @BindView(R.id.fragment_anchor_details_track_play_count1)
    TextView mTrackPlayCount1;
    @BindView(R.id.fragment_anchor_details_track_duration1)
    TextView mTrackDuration1;
    @BindView(R.id.fragment_anchor_details_track_icon2)
    CircularImageView mTrackIcon2;
    @BindView(R.id.fragment_anchor_details_track_name2)
    TextView mTrackName2;
    @BindView(R.id.item_img_album_track_play_icon2)
    ImageView mTrackPlayIcon2;
    @BindView(R.id.fragment_anchor_details_track_play_count2)
    TextView mTrackPlayCount2;
    @BindView(R.id.fragment_anchor_details_track_duration2)
    TextView mTrackDuration2;
    @BindView(R.id.fragment_anchor_details_track_relative2)
    RelativeLayout mTrackRelative2;
    @BindView(R.id.fragment_anchor_details_track_relative1)
    RelativeLayout mTrackRelative1;
    @BindView(R.id.fragment_anchor_details_album_layout)
    LinearLayout mAlbumLayout;
    @BindView(R.id.fragment_anchor_details_track_layout)
    LinearLayout mTrackLayout;
    @BindView(R.id.fragment_anchor_details_all_track)
    TextView mTxtAllTrack;
    @BindView(R.id.fragment_anchor_details_all_album)
    TextView mTxtAllAlbum;
    @BindView(R.id.fragment_isNetworkAvailable)
    RelativeLayout mNetworkAvailable;
    @BindView(R.id.img_loading)
    ImageView mImgLoading;
    @BindView(R.id.relative_loading)
    RelativeLayout mLayoutLoading;
    @BindView(R.id.anchor_details_content)
    ScrollView mAnchorDetailsContent;
    @BindView(R.id.anchor_album_details)
    RelativeLayout mAlbumDetails;
    @BindView(R.id.btn_try_agin)
    Button mBtnTry;
    private MainActivity mActivity;
    private FragmentManager mFragmentManager;
    private Announcer mAnnouncer;
    private List<Track> mTracks = new ArrayList<>();
    private boolean mAlbumLoaded = false;
    private boolean mTracksLoaded = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_anchor_details, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        mFragmentManager = mActivity.mFragmentManager;
        mAnchorDetailsContent.smoothScrollTo(0, 0);
        loadDate();
        initListener();
    }

    private void initListener() {
        mTxtAllTrack.setOnClickListener(this);
        mTxtAllAlbum.setOnClickListener(this);
        mAlbumDetails.setOnClickListener(this);
        mTrackRelative1.setOnClickListener(this);
        mTrackRelative2.setOnClickListener(this);
        mBtnTry.setOnClickListener(this);
    }

    public void setAnnouncer(Announcer announcer) {
        this.mAnnouncer = announcer;
        if (null != mActivity) {
            mAnchorDetailsContent.smoothScrollTo(0, 0);
            loadDate();
        }
    }

    private Album mAlbum;

    private void loadDate() {
        if (!Utils.isNetworkAvailable(mActivity)) {
            mNetworkAvailable.setVisibility(View.VISIBLE);
            mLayoutLoading.setVisibility(View.GONE);
            mAnchorDetailsContent.setVisibility(View.GONE);
            return;
        } else {
            mNetworkAvailable.setVisibility(View.GONE);
            mLayoutLoading.setVisibility(View.VISIBLE);
            mImgLoading.startAnimation(mActivity.mAnimation);
            mAnchorDetailsContent.setVisibility(View.GONE);
        }
        getAlbums();
        getTracks();
    }

    private void getAlbums() {
        //获取主播专辑列表
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.AID, mAnnouncer.getAnnouncerId() + "");
        map.put(DTransferConstants.PAGE, "1");
        map.put(DTransferConstants.PAGE_SIZE, "20");
        CommonRequest.getAlbumsByAnnouncer(map, new IDataCallBack<AlbumList>() {
            @Override
            public void onSuccess(AlbumList albumList) {
                mAlbumLoaded = true;
                isLoading();
                mAnchorDetailsContent.setVisibility(View.VISIBLE);
                ImageLoader.loadImage(mAnnouncer.getAvatarUrl(), mAnchoIcon);
                mAnchorName.setText(mAnnouncer.getNickname() + "");
                if ("".equals(mAnnouncer.getAnnouncerPosition())) {
                    mAnchorPosition.setText(mAnnouncer.getNickname() + "");
                } else {
                    mAnchorPosition.setText(mAnnouncer.getAnnouncerPosition() + "");
                }
                mFollowNum.setText(mAnnouncer.getFollowingCount() + "");
                mFansNum.setText(mAnnouncer.getFollowerCount() + "");
                mAnchorInfo.setText(mAnnouncer.getVdesc());
                if (null == albumList) {
                    mAlbumLayout.setVisibility(View.GONE);
                    return;
                }
                List<Album> albums = albumList.getAlbums();
                int totalCount = albums.size();
                if (totalCount == 0) {
                    mAlbumLayout.setVisibility(View.GONE);
                    return;
                }
                mAlbumLayout.setVisibility(View.VISIBLE);
                mAlbum = albums.get(0);
                mAlbumNum.setText("(" + totalCount + ")");
                ImageLoader.loadImage(mAlbum.getCoverUrlMiddle(), mAlbumIcon);
                mAlbumTitle.setText(mAlbum.getAlbumTitle());
                mAlbumInfo.setText(mAlbum.getAlbumIntro());
                mAlbumSize.setText(totalCount + "集");
            }

            @Override
            public void onError(int i, String s) {
                mAlbumLoaded = true;
                isLoading();
                Log.i("tog", "getAlbumsByAnnouncer -- > onError -- code = " + i + "Message = " + s);
            }
        });
    }

    private void getTracks() {
        //获取主播的声音列表
        Map<String, String> maps = new HashMap<String, String>();
        maps.put(DTransferConstants.AID, mAnnouncer.getAnnouncerId() + "");
        CommonRequest.getTracksByAnnouncer(maps, new IDataCallBack<AnnouncerTrackList>() {

            @Override
            public void onSuccess(AnnouncerTrackList announcerTrackList) {
                mTracksLoaded = true;
                isLoading();
                if (null == announcerTrackList) {
                    return;
                }
                mTracks = announcerTrackList.getTracks();
                if (mTracks.size() == 0) {
                    mTrackLayout.setVisibility(View.GONE);
                    return;
                }
                mTrackLayout.setVisibility(View.VISIBLE);
                mTrackCount.setText("(" + mTracks.size() + ")");
                if (mTracks.size() == 0) {
                    mTrackLayout.setVisibility(View.GONE);
                    return;
                }
                Track track = mTracks.get(0);
                ImageLoader.loadImage(track.getCoverUrlMiddle(), mTrackIcon1);
                mTrackName1.setText(track.getTrackTitle());
                mTrackPlayCount1.setText(track.getPlayCount() + "");
                mTrackDuration1.setText(track.getDuration() + "");
                if (mTracks.size() >= 2) {
                    mTrackRelative2.setVisibility(View.VISIBLE);
                    Track track2 = mTracks.get(1);
                    ImageLoader.loadImage(track2.getCoverUrlMiddle(), mTrackIcon2);
                    mTrackName2.setText(track2.getTrackTitle());
                    mTrackPlayCount2.setText(track2.getPlayCount() + "");
                    mTrackDuration2.setText(track2.getDuration() + "");
                } else {
                    mTrackRelative2.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(int i, String s) {
                mTracksLoaded = true;
                isLoading();
                Log.i("tog", "getTracksByAnnouncer -- onError -- code = " + i + "Message = " + s);
            }
        });
    }

    private void isLoading() {
        if (mAlbumLoaded && mTracksLoaded) {
            mLayoutLoading.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        switch (v.getId()) {
            case R.id.fragment_anchor_details_all_track://查看全部声音
                mActivity.hideContentAllFragment(fragmentTransaction);
                mActivity.setContentVisible(true);
                if (null == mActivity.mAllTrackFragment) {
                    mActivity.mAllTrackFragment = new AlbumTrackFragment();
                }
                if (!mActivity.mAllTrackFragment.isAdded()) {
                    fragmentTransaction.add(R.id.content, mActivity.mAllTrackFragment);
                }
                fragmentTransaction.show(mActivity.mAllTrackFragment).commit();
                MainActivity.mStack.push(mActivity.mAnchorDetailsFragment);
                mActivity.mAllTrackFragment.setAnnouncerId(mAnnouncer.getAnnouncerId());
                break;
            case R.id.fragment_anchor_details_all_album://查看全部专辑
                mActivity.hideContentAllFragment(fragmentTransaction);
                mActivity.setContentVisible(true);
                if (null == mActivity.mAllAlbumListFragment) {
                    mActivity.mAllAlbumListFragment = new AlbumListFragment();
                }
                if (!mActivity.mAllAlbumListFragment.isAdded()) {
                    fragmentTransaction.add(R.id.content, mActivity.mAllAlbumListFragment);
                }
                fragmentTransaction.show(mActivity.mAllAlbumListFragment).commit();
                MainActivity.mStack.push(mActivity.mAnchorDetailsFragment);
                mActivity.mAllAlbumListFragment.setAnnouncerId(mAnnouncer.getAnnouncerId());
                break;
            case R.id.anchor_album_details://专辑详情
                mActivity.hideContentAllFragment(fragmentTransaction);
                if (!mActivity.mAlbumDetailsFragment.isAdded()) {
                    fragmentTransaction.add(R.id.content, mActivity.mAlbumDetailsFragment);
                }
                fragmentTransaction.show(mActivity.mAlbumDetailsFragment).commit();
                mActivity.mStack.push(mActivity.mAnchorDetailsFragment);
                mActivity.mAlbumDetailsFragment.setAlbumId(mAlbum.getId());
                break;
            case R.id.fragment_anchor_details_track_relative1://声音1
                mActivity.playList(mTracks, 0);
                mActivity.switchState(MainActivity.STATE.PLAYER);
                mActivity.mStack.push(mActivity.mAnchorDetailsFragment);
                mActivity.setContentVisible(false);
                break;
            case R.id.fragment_anchor_details_track_relative2://声音2
                mActivity.playList(mTracks, 1);
                mActivity.switchState(MainActivity.STATE.PLAYER);
                mActivity.mStack.push(mActivity.mAnchorDetailsFragment);
                mActivity.setContentVisible(false);
                break;
            case R.id.btn_try_agin://再试一次
                loadDate();
                break;

        }
    }
}
