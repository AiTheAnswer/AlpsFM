package com.allen.user.alpsfm.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.List;

/**
 * Created by user on 17-3-10.
 */

public class AlbumDeatilsAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> mFragments;
    private String[] mTitle = {"详情", "列表"};

    public AlbumDeatilsAdapter(FragmentManager fm, List<Fragment> mfragments) {
        super(fm);
        this.mFragments = mfragments;
    }

    @Override
    public Fragment getItem(int position) {
        return (null != mFragments) ? mFragments.get(position) : null;
    }

    @Override
    public int getCount() {
        return (null != mFragments) ? mFragments.size() : 0;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitle[position];
    }
}
