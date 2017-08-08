package com.allen.user.alpsfm.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.allen.user.alpsfm.MainActivity;
import com.allen.user.alpsfm.R;
import com.allen.user.alpsfm.adapter.AlbumDeatilsAdapter;
import com.allen.user.alpsfm.utils.CategoryId2CategoryName;
import com.allen.user.alpsfm.utils.ImageLoader;
import com.allen.user.alpsfm.utils.Utils;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by user on 17-3-10.
 */

public class AlbumDetailsFragment extends Fragment {
    @BindView(R.id.toolbar_title)
    TextView mTitle;
    @BindView(R.id.album_details_img_icon)
    ImageView mAlbumIcon;
    @BindView(R.id.album_details_txt_title)
    TextView mAlbumTitle;
    @BindView(R.id.album_details_txt_anchor_name)
    TextView mAlbumAnchorName;
    @BindView(R.id.album_details_txt_play_count)
    TextView mAlbumPlayCount;
    @BindView(R.id.album_details_txt_classification)
    TextView mAlbumClassification;
    @BindView(R.id.album_details_tablayout)
    TabLayout mTablayout;
    @BindView(R.id.album_details_viewpager)
    ViewPager mViewPager;
    @BindView(R.id.fragment_isNetworkAvailable)
    RelativeLayout mNetworkAvailable;
    @BindView(R.id.img_loading)
    ImageView mImgLoading;
    @BindView(R.id.relative_loading)
    RelativeLayout mlayoutLoading;
    @BindView(R.id.album_details_content)
    LinearLayout mLayoutContent;
    @BindView(R.id.btn_try_agin)
    Button mBtnTry;
    private MainActivity mActivity;
    private long mAlbumId;
    private AlbumDeatilsAdapter mAdapter;
    private List<Fragment> mFragments;
    private AlbumInfoFragment mAlbumInfoFragment;
    private AlbumTrackFragment mAlbumTrackFragment;

    public void setAlbumId(long albumId) {
        this.mAlbumId = albumId;
        if (null != mActivity) {
            loadDate();
            mViewPager.setCurrentItem(1);
        }
    }

    public void notifyAdapter() {
        if (null != mAlbumTrackFragment) {
            mAlbumTrackFragment.notifyAdapter();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album_details, null);
        ButterKnife.bind(this, view);
        mActivity = (MainActivity) getActivity();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragments = new ArrayList<>();
        loadDate();
        mAlbumInfoFragment = new AlbumInfoFragment();
        mAlbumTrackFragment = new AlbumTrackFragment();
        mFragments.add(mAlbumInfoFragment);
        mFragments.add(mAlbumTrackFragment);
        mAdapter = new AlbumDeatilsAdapter(mActivity.mFragmentManager, mFragments);
        mViewPager.setAdapter(mAdapter);
        mTablayout.setupWithViewPager(mViewPager);
        mTablayout.setTabTextColors(Color.BLACK, Color.RED);
        mTablayout.setSelectedTabIndicatorColor(Color.RED);
        mViewPager.setCurrentItem(1);
        mBtnTry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDate();
            }
        });

    }

    private void loadDate() {
        if (!Utils.isNetworkAvailable(mActivity)) {
            mNetworkAvailable.setVisibility(View.VISIBLE);
            mlayoutLoading.setVisibility(View.GONE);
            mLayoutContent.setVisibility(View.GONE);
        } else {
            mLayoutContent.setVisibility(View.GONE);
            mNetworkAvailable.setVisibility(View.GONE);
            mlayoutLoading.setVisibility(View.VISIBLE);
            mImgLoading.startAnimation(mActivity.mAnimation);
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ALBUM_ID, mAlbumId + "");//具体的某一个专辑Id
        map.put(DTransferConstants.SORT, "asc");
        map.put(DTransferConstants.PAGE, "1");
        map.put(DTransferConstants.PAGE_SIZE, 10 + "");
        CommonRequest.getTracks(map, new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                if (null == trackList) {
                    return;
                }
                mlayoutLoading.setVisibility(View.GONE);
                List<Track> tracks = trackList.getTracks();
                mTitle.setText("专辑详情");
                ImageLoader.loadImage(trackList.getCoverUrlLarge(), mAlbumIcon);
                mAlbumAnchorName.setText(tracks.get(0).getAnnouncer().getNickname());
                mAlbumTitle.setText(trackList.getAlbumTitle());
                mAlbumClassification.setText(CategoryId2CategoryName.getCategoryName(mActivity.mClassificationId));
                mLayoutContent.setVisibility(View.VISIBLE);
                mAlbumInfoFragment.setTrackList(trackList);
                mAlbumTrackFragment.setAlbumId(mAlbumId);

            }

            @Override
            public void onError(int i, String s) {
                mNetworkAvailable.setVisibility(View.VISIBLE);
                mlayoutLoading.setVisibility(View.GONE);
                mLayoutContent.setVisibility(View.GONE);
                Log.i("tog", "AlbumDetailsFragment getTracks code = " + i + "----Message = " + s);
            }
        });
    }
}
