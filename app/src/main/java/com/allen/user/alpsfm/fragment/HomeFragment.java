package com.allen.user.alpsfm.fragment;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allen.user.alpsfm.MainActivity;
import com.allen.user.alpsfm.R;
import com.allen.user.alpsfm.adapter.HomeFragmentAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by allen on 2017/3/4.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.home_tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.home_viewPager)
    ViewPager mViewPager;
    @BindView(R.id.home_txt_find_album)
    TextView mFind;
    @BindView(R.id.home_playrecord)
    LinearLayout mPlayRecord;
    private String[] mTitle;
    private HomeFragmentAdapter mAdapter;
    private List<Fragment> mFragments;
    private RecommendFragment mRecommendFragment;
    private HotFrament mHotFrament;
    private ClassificationFragment mClassificationFragment;
    private RankingFragment mRankingFragment;
    private AnchorFragment mAnchorFragment;
    private FragmentManager mFragmentManager;
    private MainActivity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null);
        ButterKnife.bind(this, view);
        mViewPager.setOffscreenPageLimit(5);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initTabLayout();
        initFragment();
        initDate();
        initListener();
    }

    private void initTabLayout() {
        mTabLayout.setTabTextColors(Color.BLACK, Color.RED);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void initDate() {
        mActivity = (MainActivity) getActivity();
        mFragmentManager = mActivity.mFragmentManager;
        mTitle = new String[]{"热门", "分类", "榜单", "主播"};
        mAdapter = new HomeFragmentAdapter(getChildFragmentManager(), mFragments, mTitle);
        mViewPager.setAdapter(mAdapter);
    }

    private void initFragment() {
        mFragments = new ArrayList<>();
        mRecommendFragment = new RecommendFragment();
        mHotFrament = new HotFrament();
        mClassificationFragment = new ClassificationFragment();
        mRankingFragment = new RankingFragment();
        mAnchorFragment = new AnchorFragment();
        mFragments.add(mRecommendFragment);
//        mFragments.add(mHotFrament);
        mFragments.add(mClassificationFragment);
        mFragments.add(mRankingFragment);
        mFragments.add(mAnchorFragment);
    }

    public Fragment getFragment(int index) {
        if (index < mFragments.size()) {
            return mFragments.get(index);
        } else {
            return mFragments.get(0);
        }
    }

    public void initListener() {
        mPlayRecord.setOnClickListener(this);
        mFind.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        switch (v.getId()) {
            case R.id.home_playrecord:
                mActivity.hideContentAllFragment(fragmentTransaction);
                mActivity.setContentVisible(true);
                mActivity.mPlayRecordFragment.notifyDate();
                if (!mActivity.mPlayRecordFragment.isAdded()) {
                    fragmentTransaction.add(R.id.content, mActivity.mPlayRecordFragment);
                }
                fragmentTransaction.show(mActivity.mPlayRecordFragment).commit();
                break;
            case R.id.home_txt_find_album:
                mActivity.hideContentAllFragment(fragmentTransaction);
                mActivity.mSearchFragment = new SearchFragment();
                if (!mActivity.mSearchFragment.isAdded()) {
                    fragmentTransaction.add(R.id.content, mActivity.mSearchFragment);
                }
                fragmentTransaction.show(mActivity.mSearchFragment).commit();
                mActivity.setContentVisible(true);
                break;
        }
    }
}
