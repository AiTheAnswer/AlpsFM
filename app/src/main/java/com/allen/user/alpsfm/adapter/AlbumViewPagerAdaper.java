package com.allen.user.alpsfm.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.allen.user.alpsfm.fragment.AlbumListFragment;
import com.ximalaya.ting.android.opensdk.model.tag.Tag;

import java.util.List;


/**
 * Created by user on 17-3-8.
 */

public class AlbumViewPagerAdaper extends FragmentStatePagerAdapter {
    private List<AlbumListFragment> mFragments;

    public AlbumViewPagerAdaper(FragmentManager fm, List<AlbumListFragment> mFragments) {
        super(fm);
        this.mFragments = mFragments;

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
        return (mFragments != null) ? mFragments.get(position).getTagName() : "";
    }


}
