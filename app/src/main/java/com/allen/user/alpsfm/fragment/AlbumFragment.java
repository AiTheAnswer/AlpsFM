package com.allen.user.alpsfm.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.allen.user.alpsfm.MainActivity;
import com.allen.user.alpsfm.R;
import com.allen.user.alpsfm.adapter.AlbumViewPagerAdaper;
import com.allen.user.alpsfm.utils.CategoryId2CategoryName;
import com.allen.user.alpsfm.utils.Utils;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.tag.Tag;
import com.ximalaya.ting.android.opensdk.model.tag.TagList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.allen.user.alpsfm.fragment.RecommendFragment.appSecret;

/**
 * Created by user on 17-3-9.
 */

public class AlbumFragment extends Fragment {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView mTxtTitle;
    @BindView(R.id.album_tablayout)
    TabLayout mTablayout;
    @BindView(R.id.album_viewpager)
    ViewPager mViewPager;
    public long mCurrentClassificationId = 0;
    @BindView(R.id.fragment_isNetworkAvailable)
    RelativeLayout mNetworkAvailable;
    @BindView(R.id.img_loading)
    ImageView mImgLoading;
    @BindView(R.id.relative_loading)
    RelativeLayout mLayoutLoading;
    @BindView(R.id.fragment_album_content)
    LinearLayout mFragmentAlbumContent;
    @BindView(R.id.btn_try_agin)
    Button mBtnTry;

    private List<Tag> mAlbumTags;
    public AlbumViewPagerAdaper mAdapter;
    private MainActivity mActivity;

    public AlbumFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, null);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTablayout.setupWithViewPager(mViewPager);
        mActivity = (MainActivity) getActivity();
        initXiMaLaYa();
        if (!Utils.isNetworkAvailable(mActivity)) {//没有网的时候
            mNetworkAvailable.setVisibility(View.VISIBLE);
            mLayoutLoading.setVisibility(View.GONE);
            mFragmentAlbumContent.setVisibility(View.GONE);
        } else {
            mNetworkAvailable.setVisibility(View.GONE);
            mFragmentAlbumContent.setVisibility(View.GONE);
            getAlbumLabel(); //通过专辑Id来获取该专辑的标签
        }
        mBtnTry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAlbumLabel();
            }
        });
    }

    public void setClassification(long classificationId) {
        mCurrentClassificationId = classificationId;
        if (null != mActivity) {
            if (!Utils.isNetworkAvailable(mActivity)) {//没有网的时候
                mNetworkAvailable.setVisibility(View.VISIBLE);
                mLayoutLoading.setVisibility(View.GONE);
                mFragmentAlbumContent.setVisibility(View.GONE);
            } else {
                mNetworkAvailable.setVisibility(View.GONE);
                getAlbumLabel(); //通过专辑Id来获取该专辑的标签
            }
        }
    }

    /**
     * 通过专辑Id 来获取该专辑的标签集合
     */
    public void getAlbumLabel() {
        mFragmentAlbumContent.setVisibility(View.GONE);
        mLayoutLoading.setVisibility(View.VISIBLE);
        mImgLoading.startAnimation(mActivity.mAnimation);
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.CATEGORY_ID, mCurrentClassificationId + "");
        map.put(DTransferConstants.TYPE, 0 + "");
        CommonRequest.getTags(map, new IDataCallBack<TagList>() {
            @Override
            public void onSuccess(TagList tagList) {
                mLayoutLoading.setVisibility(View.GONE);
                mAlbumTags = tagList.getTagList();
                mFragmentAlbumContent.setVisibility(View.VISIBLE);

                    //加载数据
                    loadDate();


            }

            @Override
            public void onError(int i, String s) {
                mNetworkAvailable.setVisibility(View.VISIBLE);
                mLayoutLoading.setVisibility(View.GONE);
                Log.i("tog", "AlbumFragment::getAlbumLabel --> onError mAlbumTags is size = " + "mCurrentAlbumId = " + AlbumFragment.this.mCurrentClassificationId);
            }
        });

    }

    private void loadDate() {
        List<AlbumListFragment> fragments = new ArrayList<AlbumListFragment>();
        for (Tag tag : mAlbumTags) {
            fragments.add(new AlbumListFragment(tag.getTagName()));
        }
        mAdapter = new AlbumViewPagerAdaper(mActivity.getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(mAdapter);
        initToolbar();
    }

    private void initXiMaLaYa() {
        CommonRequest.getInstanse().init(mActivity, appSecret);
        CommonRequest.getInstanse().setUseHttps(true);
    }

    private void initToolbar() {
        if (null != mToolbar) {
            mToolbar.setTitle("");
            mTxtTitle.setText(CategoryId2CategoryName.getCategoryName(mCurrentClassificationId));
            mActivity.setSupportActionBar(mToolbar);
        }
    }


}
