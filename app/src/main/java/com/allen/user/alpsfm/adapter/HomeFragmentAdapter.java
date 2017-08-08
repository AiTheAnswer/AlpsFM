package com.allen.user.alpsfm.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.List;

/**
 * Created by user on 17-2-27.
 */

public class HomeFragmentAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> mFragments;
    private String[] mTitle;

    public HomeFragmentAdapter(FragmentManager fm, List<Fragment> mFragments, String[] mTitle) {
        super(fm);
        this.mFragments = mFragments;
        this.mTitle = mTitle;
    }

    @Override
    public Fragment getItem(int position) {

        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return (null != mFragments) ? mFragments.size() : 0;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return (mTitle != null && position < mTitle.length) ? mTitle[position] : "";
    }
}
