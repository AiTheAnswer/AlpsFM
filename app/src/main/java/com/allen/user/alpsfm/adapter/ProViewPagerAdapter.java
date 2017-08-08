package com.allen.user.alpsfm.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.allen.user.alpsfm.fragment.ProRadiosListFragment;
import com.allen.user.alpsfm.fragment.ProvinceRadiosFragment;

import java.util.List;

/**
 * Created by user on 17-3-22.
 */

public class ProViewPagerAdapter extends FragmentStatePagerAdapter {
    private List<ProRadiosListFragment> mFragments;

    public ProViewPagerAdapter(FragmentManager fm, List<ProRadiosListFragment> fragments) {
        super(fm);
        this.mFragments = fragments;
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
        return mFragments.get(position).getProvinceName();
    }
}
