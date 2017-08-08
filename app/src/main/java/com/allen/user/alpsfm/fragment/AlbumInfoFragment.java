package com.allen.user.alpsfm.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.allen.user.alpsfm.R;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by user on 17-3-10.
 */

public class AlbumInfoFragment extends Fragment {
    @BindView(R.id.album_info_txt_info)
    TextView mTxtInfo;
    private TrackList mTrackList;

    public AlbumInfoFragment() {
    }

    public void setTrackList(TrackList trackList) {
        mTrackList = trackList;
        if (null != getActivity()) {
            mTxtInfo.setText(mTrackList.getAlbumIntro());
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album_info, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTxtInfo.setText(mTrackList.getAlbumIntro());

    }
}
