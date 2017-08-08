package com.allen.user.alpsfm.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.allen.user.alpsfm.MainActivity;
import com.allen.user.alpsfm.R;
import com.allen.user.alpsfm.adapter.PlayRecordAdapter;
import com.allen.user.alpsfm.model.PlayRecord;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioListById;
import com.ximalaya.ting.android.opensdk.model.track.LastPlayTrackList;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.allen.user.alpsfm.fragment.RecommendFragment.appSecret;

/**
 * Created by user on 17-3-27.
 */

public class PlayRecordFragment extends Fragment {
    @BindView(R.id.fragment_play_record_lst)
    ListView mListView;
    @BindView(R.id.toolbar_title)
    TextView mTitle;
    @BindView(R.id.toolbar_delete)
    FrameLayout mDelete;
    private MainActivity mActivity;
    private PlayRecordAdapter mAdapter;
    private List<PlayRecord> mAllRecord;
    private AlertDialog mLodingDialog;
    private AlertDialog mDeleteDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playrecord, null);
        ButterKnife.bind(this, view);
        mActivity = (MainActivity) getActivity();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTitle.setText("播放历史");
        loadDate();
        initListener();
    }

    private void initListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlayRecord playRecord = mAllRecord.get(position);
                showLoadingDialog();//显示加载播放列表的对话框
                if (playRecord.getType() == 2) {//track
                    getLastPlayTrackList(playRecord.getAblumId(), playRecord.getDataId());
                }
                if (playRecord.getType() == 3) {//radio
                    getRadio(playRecord);
                }
            }
        });
        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog();
            }
        });
    }

    private void showLoadingDialog() {
        if (null == mLodingDialog) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            LayoutInflater inflater = LayoutInflater.from(mActivity);
            View view1 = inflater.inflate(R.layout.dialog_playrecord_load_list, null);
            ImageView imageView = (ImageView) view1.findViewById(R.id.dialog_img_playrecord_loading_list);
            imageView.startAnimation(mActivity.mAnimation);
            builder.setView(view1);
            mLodingDialog = builder.create();
            mLodingDialog.setCanceledOnTouchOutside(false);//点击外面不消失
        }
        WindowManager.LayoutParams attributes = mActivity.getWindow().getAttributes();
        attributes.alpha = 0.5f;
        mActivity.getWindow().setAttributes(attributes);
        mLodingDialog.show();
    }

    private void dismissLoadingDialog() {
        WindowManager.LayoutParams attributes = mActivity.getWindow().getAttributes();
        attributes.alpha = 1.0f;
        mActivity.getWindow().setAttributes(attributes);
        mLodingDialog.dismiss();
    }

    private void showDeleteDialog() {
        if (null == mDeleteDialog) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            LayoutInflater inflater = LayoutInflater.from(mActivity);
            View view1 = inflater.inflate(R.layout.dialog_playrecord_delte, null);
            TextView cancel = (TextView) view1.findViewById(R.id.dialog_txt_delete_cancel);
            TextView confirm = (TextView) view1.findViewById(R.id.dialog_txt_delete_confirm);
            builder.setView(view1);

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissDeleteDialog();
                }
            });
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActivity.mPlayRecordDbWrapper.clearAll();
                    if (null != mAllRecord) {
                        mAllRecord.clear();
                    }
                    mAdapter.notifyDataSetChanged();
                    dismissDeleteDialog();
                }
            });
            mDeleteDialog = builder.create();
            mDeleteDialog.setCanceledOnTouchOutside(false);//点击外面不消失
        }

        mDeleteDialog.show();
    }

    private void dismissDeleteDialog() {
        WindowManager.LayoutParams attributes = mActivity.getWindow().getAttributes();
        attributes.alpha = 1.0f;
        mActivity.getWindow().setAttributes(attributes);
        mDeleteDialog.dismiss();
    }

    private void getLastPlayTrackList(long albumId, final long dataId) {
        Map<String, String> map = new HashMap<String, String>();
        CommonRequest.getInstanse().init(mActivity, appSecret);
        CommonRequest.getInstanse().setUseHttps(true);
        map.put(DTransferConstants.ALBUM_ID, albumId + "");
        map.put(DTransferConstants.TRACK_ID, dataId + "");
        CommonRequest.getLastPlayTracks(map, new IDataCallBack<LastPlayTrackList>() {
            @Override
            public void onSuccess(LastPlayTrackList lastPlayTrackList) {
                dismissLoadingDialog();
                if (null == lastPlayTrackList || lastPlayTrackList.getTracks() == null) {
                    return;
                }
                List<Track> tracks = lastPlayTrackList.getTracks();
                int position = 0;
                for (int i = 0; i < tracks.size(); i++) {
                    if (dataId == tracks.get(i).getDataId()) {
                        position = i;
                        break;
                    }
                }
                mActivity.playList(tracks, position);
                mActivity.setContentVisible(false);
                mActivity.mStack.push(mActivity.mPlayRecordFragment);
                mActivity.switchState(MainActivity.STATE.PLAYER);

            }

            @Override
            public void onError(int i, String s) {
                dismissLoadingDialog();
            }
        });
    }

    private void getRadio(PlayRecord playRecord) {
        String ids = playRecord.getAblumId() + ",";
        CommonRequest.getInstanse().init(mActivity, appSecret);
        CommonRequest.getInstanse().setUseHttps(true);
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.RADIO_IDS, ids);
        CommonRequest.getRadiosByIds(map, new IDataCallBack<RadioListById>() {
            @Override
            public void onSuccess(RadioListById radioListById) {
                dismissLoadingDialog();
                if (null == radioListById || null == radioListById.getRadios() || radioListById.getRadios().size() == 0) {
                    return;
                }
                Radio radio = radioListById.getRadios().get(0);
                mActivity.playRadio(radio);
                mActivity.setContentVisible(false);
                mActivity.switchState(MainActivity.STATE.PLAYER);
                mActivity.mStack.push(mActivity.mPlayRecordFragment);
            }

            @Override
            public void onError(int i, String s) {
                dismissLoadingDialog();
            }
        });

    }

    private void loadDate() {
        if (mActivity != null) {
            mAllRecord = mActivity.mPlayRecordDbWrapper.getAllRecord();
            mAdapter = new PlayRecordAdapter(mActivity, mAllRecord);
            mListView.setAdapter(mAdapter);
        }
    }

    public void notifyDate() {
        loadDate();
    }
}
