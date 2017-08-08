package com.allen.user.alpsfm.view;

import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.allen.user.alpsfm.MainActivity;
import com.allen.user.alpsfm.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by user on 17-3-30.
 */

public class TimePopupWindow extends PopupWindow implements View.OnClickListener {
    @BindView(R.id.pop_img_no_open)
    ImageView mImgNoOpen;
    @BindView(R.id.pop_layout_no_open)
    LinearLayout mLayoutNoOpen;
    @BindView(R.id.pop_time_off_countdown_current_track)
    TextView mTxtCurTrack;
    @BindView(R.id.pop_img_current_track)
    ImageView mImgCurTrack;
    @BindView(R.id.pop_layout_current_track)
    LinearLayout mLayoutCurTrack;
    @BindView(R.id.pop_time_off_countdown_ten)
    TextView mTxtTen;
    @BindView(R.id.pop_img_ten)
    ImageView mImgTen;
    @BindView(R.id.pop_layout_ten)
    LinearLayout mLayoutTen;
    @BindView(R.id.pop_time_off_countdown_twenty)
    TextView mTxtTwenty;
    @BindView(R.id.pop_img_twenty)
    ImageView mImgTwenty;
    @BindView(R.id.pop_layout_twenty)
    LinearLayout mLayoutTwenty;
    @BindView(R.id.pop_time_off_countdown_thirty)
    TextView mTxtThirty;
    @BindView(R.id.pop_img_thirty)
    ImageView mImgThirty;
    @BindView(R.id.pop_layout_thirty)
    LinearLayout mLayoutThirty;
    @BindView(R.id.pop_time_off_countdown_sixty)
    TextView mTxtSixty;
    @BindView(R.id.pop_img_sixty)
    ImageView mImgSixty;
    @BindView(R.id.pop_layout_sixty)
    LinearLayout mLayoutSixty;
    @BindView(R.id.pop_time_off_countdown_ninety)
    TextView mTxtNinety;
    @BindView(R.id.pop_img_ninety)
    ImageView mImgNinety;
    @BindView(R.id.pop_layout_ninety)
    LinearLayout mLayoutNinety;
    @BindView(R.id.pop_time_off_close)
    TextView mTxtClose;

    private Drawable mDrawSelect;
    private Drawable mDrawNormal;
    private MainActivity mActivity;
    private Resources mResources;
    private Window mWindow;
    private WindowManager.LayoutParams mParams;


    public TimePopupWindow(MainActivity activity) {
        super(activity.getLayoutInflater().inflate(R.layout.popupwindon_time_off, null),
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ButterKnife.bind(this, getContentView());
        this.mActivity = activity;
        this.mWindow = activity.getWindow();
        this.mParams = mWindow.getAttributes();
        this.mResources = activity.getResources();
        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable());
        initDate();
        initListener();
    }

    public void show() {
        showAtLocation(mWindow.getDecorView(), Gravity.BOTTOM, 0, 0);
        mParams.alpha = 0.5f;
        mWindow.setAttributes(mParams);
    }

    private void initDate() {
        mDrawSelect = mResources.getDrawable(R.mipmap.check_selected);
        mDrawNormal = mResources.getDrawable(R.mipmap.check_normal);
    }

    private void initListener() {
        mLayoutNoOpen.setOnClickListener(this);
        mLayoutCurTrack.setOnClickListener(this);
        mLayoutTen.setOnClickListener(this);
        mLayoutTwenty.setOnClickListener(this);
        mLayoutThirty.setOnClickListener(this);

        mLayoutSixty.setOnClickListener(this);
        mLayoutNinety.setOnClickListener(this);
        mTxtClose.setOnClickListener(this);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                mParams.alpha = 1.0f;
                mWindow.setAttributes(mParams);
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.pop_layout_no_open://不开启
                resetView();
                mImgNoOpen.setImageDrawable(mDrawSelect);
                mActivity.setCountDownPosition(MainActivity.COUNTDOWNPOS.NOOPEN);
                break;
            case R.id.pop_layout_current_track://播放完当前声音
                resetView();
                mImgCurTrack.setImageDrawable(mDrawSelect);
                mTxtCurTrack.setVisibility(View.VISIBLE);
                mActivity.setCountDownPosition(MainActivity.COUNTDOWNPOS.CURRENTTRACK);
                break;
            case R.id.pop_layout_ten://十分钟
                resetView();
                mImgTen.setImageDrawable(mDrawSelect);
                mTxtTen.setVisibility(View.VISIBLE);
                mActivity.setCountDownPosition(MainActivity.COUNTDOWNPOS.TEN);
                break;
            case R.id.pop_layout_twenty://二十分钟
                resetView();
                mImgTwenty.setImageDrawable(mDrawSelect);
                mTxtTwenty.setVisibility(View.VISIBLE);
                mActivity.setCountDownPosition(MainActivity.COUNTDOWNPOS.TWENTY);
                break;
            case R.id.pop_layout_thirty://三十分钟
                resetView();
                mImgThirty.setImageDrawable(mDrawSelect);
                mTxtThirty.setVisibility(View.VISIBLE);
                mActivity.setCountDownPosition(MainActivity.COUNTDOWNPOS.THIRTY);
                break;
            case R.id.pop_layout_sixty://六十分钟
                resetView();
                mImgSixty.setImageDrawable(mDrawSelect);
                mTxtSixty.setVisibility(View.VISIBLE);
                mActivity.setCountDownPosition(MainActivity.COUNTDOWNPOS.SIXTY);
                break;
            case R.id.pop_layout_ninety://九十分钟
                resetView();
                mActivity.setCountDownPosition(MainActivity.COUNTDOWNPOS.NIETY);
                mImgNinety.setImageDrawable(mDrawSelect);
                mTxtNinety.setVisibility(View.VISIBLE);
                break;
            case R.id.pop_time_off_close:
                dismiss();
                break;
        }

    }

    private void resetView() {
        mTxtCurTrack.setVisibility(View.GONE);
        mTxtTen.setVisibility(View.GONE);
        mTxtTwenty.setVisibility(View.GONE);
        mTxtThirty.setVisibility(View.GONE);
        mTxtSixty.setVisibility(View.GONE);
        mTxtNinety.setVisibility(View.GONE);
        mImgNoOpen.setImageDrawable(mDrawNormal);
        mImgCurTrack.setImageDrawable(mDrawNormal);
        mImgTen.setImageDrawable(mDrawNormal);
        mImgTwenty.setImageDrawable(mDrawNormal);
        mImgThirty.setImageDrawable(mDrawNormal);
        mImgSixty.setImageDrawable(mDrawNormal);
        mImgNinety.setImageDrawable(mDrawNormal);
    }

    public void setCountDownTime(MainActivity.COUNTDOWNPOS downPosition, String time) {
        switch (downPosition) {
            case CURRENTTRACK://播放当前音乐
                mTxtCurTrack.setText("倒计时 " + time);
                break;
            case TEN://十分钟
                mTxtTen.setText("倒计时 " + time);
                break;
            case TWENTY://二十分钟
                mTxtTwenty.setText("倒计时 " + time);
                break;
            case THIRTY://三十分钟
                mTxtThirty.setText("倒计时 " + time);
                break;
            case SIXTY://六十分钟
                mTxtSixty.setText("倒计时 " + time);
                break;
            case NIETY: // 九十分钟
                mTxtNinety.setText("倒计时 " + time);
                break;
        }

    }
}
