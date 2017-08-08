package com.allen.user.alpsfm.view;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.allen.user.alpsfm.MainActivity;
import com.allen.user.alpsfm.R;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.allen.user.alpsfm.MainActivity.mPlayerManager;

/**
 * Created by user on 17-3-6.
 */

public class ListPopupWindow extends PopupWindow implements View.OnClickListener {
    private final MainActivity mActivity;
    private final Window mWindow;
    @BindView(R.id.img_pop_play_mode)
    ImageView mImgPlayMode;
    @BindView(R.id.layout_pop_play_mode)
    LinearLayout mLayoutMode;
    private WindowManager.LayoutParams mParams;
    @BindView(R.id.txt_pop_play_mode)
    TextView mPopPlayMode;//播放模式
    @BindView(R.id.listview_pop)
    ListView mListview;//内容列表
    @BindView(R.id.txt_pop_stop)
    TextView mPopStop;//关闭PopupWindow

    //播放列表集合
    private List<Track> mTracks;
    //当前的声音Position
    private int mPosition;
    private PopupListAdapter mAdapter;
    private LayoutInflater mInflater;
    private XmPlayListControl.PlayMode mPlayMode;
    private Drawable mDrawableModeList;
    private Drawable mDrawableModeListLoop;
    private Drawable mDrawableModeSingle;
    private Drawable mDrawableModeRandom;
    //播放模式的集合
    private HashMap<XmPlayListControl.PlayMode, String> mPlayModeMap;

    public ListPopupWindow(Activity activity, List<Track> list, int position, int height) {
        super(activity.getLayoutInflater().inflate(R.layout.popupwindow_play_list, null),
                ViewGroup.LayoutParams.MATCH_PARENT,
                height);
        mInflater = activity.getLayoutInflater();
        ButterKnife.bind(this, getContentView());
        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable());
        this.mActivity = (MainActivity) activity;
        mPlayMode = mPlayerManager.getPlayMode();
        mWindow = activity.getWindow();
        this.mPosition = position;
        mParams = mWindow.getAttributes();
        this.mTracks = list;
        initDate();
        initListener();
    }


    private void initListener() {
        mLayoutMode.setOnClickListener(this);//单曲 顺序
        mPopStop.setOnClickListener(this);//关闭PopupWindow
        setOnDismissListener(new OnDismissListener() {//popupWindow的销毁的监听事件
            @Override
            public void onDismiss() {
                mParams.alpha = 1.0f;
                mWindow.setAttributes(mParams);
            }
        });
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPlayerManager.play(position);
                mListview.setSelection(position);
            }
        });
    }

    //刷新
    public void notifyListPopAdapte() {
        mAdapter.notifyDataSetChanged();

    }

    /**
     * 显示PopupWindow
     */
    public void show() {
        showAtLocation(mWindow.getDecorView(), Gravity.BOTTOM, 0, 0);
        mParams.alpha = 0.5f;
        mWindow.setAttributes(mParams);
    }

    private void initDate() {
        mPlayModeMap = new HashMap<>();
        mPlayModeMap.put(XmPlayListControl.PlayMode.PLAY_MODEL_LIST, "顺序");
        mPlayModeMap.put(XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP, "循环");
        mPlayModeMap.put(XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP, "单曲");
        mPlayModeMap.put(XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM, "随机");
        mPopPlayMode.setText(mPlayModeMap.get(mPlayMode));
        Resources resources = mActivity.getResources();
        mDrawableModeList = resources.getDrawable(R.mipmap.playmode_loop);
        mDrawableModeListLoop = resources.getDrawable(R.mipmap.playmode_order);
        mDrawableModeSingle = resources.getDrawable(R.mipmap.playmode_single);
        mDrawableModeRandom = resources.getDrawable(R.mipmap.playmode_random);
        switch (mPlayMode) {
            case PLAY_MODEL_LIST:
                mImgPlayMode.setImageDrawable(mDrawableModeList);
                break;
            case PLAY_MODEL_LIST_LOOP:
                mImgPlayMode.setImageDrawable(mDrawableModeListLoop);
                break;
            case PLAY_MODEL_SINGLE_LOOP:
                mImgPlayMode.setImageDrawable(mDrawableModeSingle);
                break;
            case PLAY_MODEL_RANDOM:
                mImgPlayMode.setImageDrawable(mDrawableModeRandom);
                break;

        }
        mAdapter = new PopupListAdapter();
        mListview.setAdapter(mAdapter);
        mListview.setSelection(mPosition);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_pop_stop://关闭
                dismiss();
                break;
            case R.id.layout_pop_play_mode://播放模式
                mPlayMode = mPlayerManager.getPlayMode();
                XmPlayListControl.PlayMode newPlayMode = XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
                switch (mPlayMode) {
                    case PLAY_MODEL_LIST://顺序
                        newPlayMode = XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE;
                        mPopPlayMode.setText("单曲");
                        mImgPlayMode.setImageDrawable(mDrawableModeSingle);
                        Toast.makeText(mActivity, "播放模式已切换为单曲播放", Toast.LENGTH_SHORT).show();
                        break;
                    case PLAY_MODEL_SINGLE://单曲播放
                        newPlayMode = XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
                        Toast.makeText(mActivity, "播放模式已切换为随机播放", Toast.LENGTH_SHORT).show();
                        mImgPlayMode.setImageDrawable(mDrawableModeRandom);
                        mPopPlayMode.setText("随机");
                        break;
                    case PLAY_MODEL_RANDOM://随机播放
                        newPlayMode = XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
                        Toast.makeText(mActivity, "播放模式已切换为循环播放", Toast.LENGTH_SHORT).show();
                        mImgPlayMode.setImageDrawable(mDrawableModeListLoop);
                        mPopPlayMode.setText("循环");
                        break;
                    case PLAY_MODEL_LIST_LOOP://循环播放
                        newPlayMode = XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
                        Toast.makeText(mActivity, "播放模式已切换为顺序播放", Toast.LENGTH_SHORT).show();
                        mImgPlayMode.setImageDrawable(mDrawableModeList);
                        mPopPlayMode.setText("顺序");
                        break;
                }
                mPlayerManager.setPlayMode(newPlayMode);

                break;

        }

    }

    class PopupListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return (null != mTracks) ? mTracks.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return mTracks.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder mViewHolder;
            if (null == convertView) {
                mViewHolder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.item_popup_list, null);
                mViewHolder.mTxtTitle = (TextView) convertView.findViewById(R.id.item_txt_popup_title);

                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (ViewHolder) convertView.getTag();
            }
            final Track track = mTracks.get(position);
            mViewHolder.mTxtTitle.setText(track.getTrackTitle());
            if (track.getDataId() == mActivity.mDateId) {
                mViewHolder.mTxtTitle.setTextColor(Color.RED);
                mListview.setSelection(position);
            } else {
                mViewHolder.mTxtTitle.setTextColor(Color.BLACK);
            }
            return convertView;
        }

        private class ViewHolder {
            TextView mTxtTitle;
        }


    }
}
