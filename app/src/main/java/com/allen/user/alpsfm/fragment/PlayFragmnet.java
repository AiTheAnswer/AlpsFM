package com.allen.user.alpsfm.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.allen.user.alpsfm.MainActivity;
import com.allen.user.alpsfm.R;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.track.TrackHotList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by allen on 2017/3/4.
 */
public class PlayFragmnet extends Fragment {
    @BindView(R.id.play_img_Icon)
    ImageView mImgIcon;
    @BindView(R.id.seekBar)
    SeekBar mSeekBar;
    @BindView(R.id.play_img_pause)
    ImageView mImgPause;
    @BindView(R.id.play_img_previous)
    ImageView mImgPrevious;
    @BindView(R.id.play_img_next)
    ImageView mImgNext;
    private TrackHotList mTrackHotList;
    private MainActivity mActivity;
    private XmPlayerManager mPlayerManager;
    /**
     * 播放器的状态回调监听事件
     */
    private IXmPlayerStatusListener mPlayerStatusListener = new IXmPlayerStatusListener() {
        @Override
        public void onPlayStart() {

        }

        @Override
        public void onPlayPause() {

        }

        @Override
        public void onPlayStop() {

        }

        @Override
        public void onSoundPlayComplete() {

        }

        @Override
        public void onSoundPrepared() {

        }

        @Override
        public void onSoundSwitch(PlayableModel playableModel, PlayableModel playableModel1) {

        }

        @Override
        public void onBufferingStart() {

        }

        @Override
        public void onBufferingStop() {

        }

        @Override
        public void onBufferProgress(int i) {

        }

        @Override
        public void onPlayProgress(int i, int i1) {

        }

        @Override
        public boolean onError(XmPlayerException e) {
            return false;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.player_layout, container);
        ButterKnife.bind(this, view);
        mActivity = (MainActivity) getActivity();
        initPlayer();
        return view;
    }

    /**
     * 初始化播放器
     */
    private void initPlayer() {
        mPlayerManager = XmPlayerManager.getInstance(mActivity);
        mPlayerManager.addPlayerStatusListener(mPlayerStatusListener);
        mPlayerManager.init();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
