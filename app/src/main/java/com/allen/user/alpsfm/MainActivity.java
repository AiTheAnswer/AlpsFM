package com.allen.user.alpsfm;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.allen.user.alpsfm.db.PlayRecordDbWrapper;
import com.allen.user.alpsfm.fragment.AlbumDetailsFragment;
import com.allen.user.alpsfm.fragment.AlbumFragment;
import com.allen.user.alpsfm.fragment.AlbumListFragment;
import com.allen.user.alpsfm.fragment.AlbumTrackFragment;
import com.allen.user.alpsfm.fragment.AnchorDetailsFragment;
import com.allen.user.alpsfm.fragment.AnchorListFragment;
import com.allen.user.alpsfm.fragment.BroadcastFragment;
import com.allen.user.alpsfm.fragment.HomeFragment;
import com.allen.user.alpsfm.fragment.PlayRecordFragment;
import com.allen.user.alpsfm.fragment.ProvinceRadiosFragment;
import com.allen.user.alpsfm.fragment.RadiosByCateFragment;
import com.allen.user.alpsfm.fragment.RadiosFragment;
import com.allen.user.alpsfm.fragment.RankAlbumFragment;
import com.allen.user.alpsfm.fragment.RankTrackFragment;
import com.allen.user.alpsfm.fragment.RecommendFragment;
import com.allen.user.alpsfm.fragment.SearchFragment;
import com.allen.user.alpsfm.model.PlayRecord;
import com.allen.user.alpsfm.utils.CountDownThread;
import com.allen.user.alpsfm.utils.DisplayUtil;
import com.allen.user.alpsfm.utils.ImageLoader;
import com.allen.user.alpsfm.utils.Utils;
import com.allen.user.alpsfm.view.ListPopupWindow;
import com.allen.user.alpsfm.view.TimePopupWindow;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.album.SubordinatedAlbum;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioListById;
import com.ximalaya.ting.android.opensdk.model.track.CommonTrackList;
import com.ximalaya.ting.android.opensdk.model.track.LastPlayTrackList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.allen.user.alpsfm.MainActivity.STATE.HOME;
import static com.allen.user.alpsfm.fragment.RecommendFragment.appSecret;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.home_drawerLayout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.main_layout_home)
    LinearLayout mLayoutHome;
    @BindView(R.id.main_img_home)
    ImageView mImgHome;
    @BindView(R.id.main_txt_home)
    TextView mTxtHome;
    @BindView(R.id.main_layout_broad)
    LinearLayout mLayoutBroad;
    @BindView(R.id.main_img_broad)
    ImageView mImgBroad;
    @BindView(R.id.main_txt_broad)
    TextView mTxtBroad;
    @BindView(R.id.main_img_player)
    ImageView mImgPlayer;
    @BindView(R.id.main_layout_bottom)
    LinearLayout mLayoutBottom;
    @BindView(R.id.mian_player_icon)
    RelativeLayout mLayoutPlayerIcon;
    //　放首页　Player 广播的容器
    @BindView(R.id.home_content)
    LinearLayout mContentLinearLayout;
    // 放专辑Fragment　的容器
    @BindView(R.id.content)
    FrameLayout mAlbumContent;
    @BindView(R.id.play_img_Icon)
    ImageView mTrackIcon;
    @BindView(R.id.seekBar)
    SeekBar mSeekBar;
    @BindView(R.id.play_img_pause)
    ImageView mPlayerPause;
    @BindView(R.id.play_img_previous)
    ImageView mPlayerPrevious;
    @BindView(R.id.play_img_next)
    ImageView mPlayerNext;
    //  　放Player的父容器
    @BindView(R.id.layout_player)
    FrameLayout mLayoutPlayer;
    //  放首页和广播的父容器
    @BindView(R.id.layout_content)
    FrameLayout mLayoutContent;
    @BindView(R.id.txt_current_time)
    TextView mCurrentTime;
    @BindView(R.id.txt_total_time)
    TextView mTotalTime;
    @BindView(R.id.play_list)
    LinearLayout mPlayList;
    @BindView(R.id.player_img_album_icon)
    ImageView mPlayerImgAlbumIcon;
    @BindView(R.id.player_txt_album_title)
    TextView mPlayerTxtAlbumTitle;
    @BindView(R.id.layout_album)
    LinearLayout mLayoutAlbumInfo;
    @BindView(R.id.player_track_title)
    TextView mPlayerTrackTitle;
    @BindView(R.id.player_txt_play_count)
    TextView mPlayerTxtPlayCount;
    @BindView(R.id.player_track_update_date)
    TextView mPlayerTrackUpdateDate;
    @BindView(R.id.fragment_isNetworkAvailable)
    RelativeLayout mNetworkAvailable;
    @BindView(R.id.img_loading)
    ImageView mImgLoading;
    @BindView(R.id.relative_loading)
    RelativeLayout mLayoutLoading;
    @BindView(R.id.player_content)
    FrameLayout mPlayerContent;
    @BindView(R.id.toolbar_title)
    TextView mPlayerRadioTitle;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.player_radio_name)
    TextView mRadioName;
    @BindView(R.id.player_radio_seekbar)
    SeekBar mRadioSeekbar;
    @BindView(R.id.bottom_line)
    View mBottomView;
    @BindView(R.id.player_radio_play_icon)
    ImageView mRadioPlayIcon;
    @BindView(R.id.player_radio_img_icon)
    ImageView mRadioImgIcon;
    @BindView(R.id.player_radio_subname)
    TextView mRadioSubname;
    @BindView(R.id.player_radio)
    LinearLayout mLayoutPlayerRadio;
    @BindView(R.id.player_txt_subscribe_num)
    TextView playerTxtSubscribeNum;
    @BindView(R.id.player_track)
    ScrollView mLayoutPlayerTrack;
    @BindView(R.id.player_track_layout_icon_seekbar)
    RelativeLayout mLayoutIconSeekBar;
    @BindView(R.id.player_radio_program_name)
    TextView mPlayerRadioProgramName;
    @BindView(R.id.player_radio_start_end)
    TextView mRadioStartEnd;
    @BindView(R.id.player_track_txt_time_off)
    TextView mTxtTimeOff;
    @BindView(R.id.player_track_layout_time)
    LinearLayout mLayoutTime;
    @BindView(R.id.activity_play)
    FrameLayout activityPlay;
    @BindView(R.id.btn_try_agin)
    Button mBtnTry;


    private ActionBarDrawerToggle mDrawerToggle;
    private HomeFragment mHomeFragment;//首页
    private BroadcastFragment mBroadcastFragment;//广播
    public AlbumFragment mAlbumFragment;//专辑Fragment
    public AlbumDetailsFragment mAlbumDetailsFragment;//专辑详情Fragment
    public RankAlbumFragment mRankAlbumFragment;//榜单的专辑Framgent
    public RankTrackFragment mRankTrackFragment;//榜单的Track Fragment
    public AnchorListFragment mAnchorListFragment;//主播列表　Fragment
    public AnchorDetailsFragment mAnchorDetailsFragment;//主播详情 Fragment
    public AlbumListFragment mAlbumListFragment;//专辑列表
    public AlbumListFragment mAllAlbumListFragment;//全部专辑列表
    public AlbumTrackFragment mAllTrackFragment;//全部声音
    //------------广播------------
    public RadiosFragment mRadiosFragment;
    public RadiosByCateFragment mRadiosByCateFragment;
    public ProvinceRadiosFragment mProvinceRadiosFragment;
    public FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    //----------- 播放记录
    public PlayRecordFragment mPlayRecordFragment;
    //----------- 搜索
    public SearchFragment mSearchFragment;
    private Radio mCurrentRadio;
    //正在播放的列表
    private List<Track> mTracks = new ArrayList<>();
    //正在播放声音的Id
    public int mPosition = -1;
    //当前的声音
    private Track mCurrentTrack;
    //播放器管理者
    public static XmPlayerManager mPlayerManager;
    //屏幕的宽高
    private int mWidth;
    private int mHeight;
    private ListPopupWindow mListPopWindow;//显示播放列表的PopupWindow
    private TimePopupWindow mTimePopWindow;//定时的PopupWindow
    private Resources mResources;
    //上一首不可按的图片
    private Drawable mPreviousDrawableDisable;
    //上一首普通的图片
    private Drawable mPreviousDrawableNormal;
    //下一首不可按的图片
    private Drawable mNextDrawableDisable;
    //下一首普通的图片
    private Drawable mNextDrawableNormal;
    //正在播放的图片
    private Drawable mPlayDrawable;
    //暂停的图片
    private Drawable mPauseDrawable;
    //选择的专辑Id
    public long mClassificationId = 0;
    public long mDateId = -1;
    private static final int PLAYINGSOUNT_TYPE_TRACK = 2;//Track
    private static final int PLAYINGSOUNT_TYPE_RADIO = 3;//RADIO
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private AlertDialog mDialog;
    public Animation mAnimation;
    private Animation mAnimPlaying;
    public PlayRecordDbWrapper mPlayRecordDbWrapper;


    public enum STATE {
        HOME, PLAYER, BROADCAST
    }


    public static Stack<Fragment> mStack = new Stack<>();
    private STATE mCurrentState = HOME;
    private Activity mActivity;
    private int mCurrentPlayType;//播放声音的类型 2是Track 3是Radio
    private IXmPlayerStatusListener mPlayerListener = new IXmPlayerStatusListener() {
        @Override
        public void onPlayStart() {
            Log.i("tog", "----------onPlayStart------");
            mLayoutLoading.setVisibility(View.GONE);
            mCurrentPlayType = mPlayerManager.getCurrPlayType();
            if (mCurrentPlayType == PLAYINGSOUNT_TYPE_TRACK) {//track
                mLayoutPlayerRadio.setVisibility(View.GONE);
                mLayoutPlayerTrack.setVisibility(View.VISIBLE);
                mCurrentTrack = (Track) mPlayerManager.getCurrSound();
                mDateId = mCurrentTrack.getDataId();
                mTracks = mPlayerManager.getPlayList();
                mPosition = mPlayerManager.getCurrentIndex();
                mPlayerPause.setImageDrawable(mPauseDrawable);//播放的时候设置暂停图片
                //加载大图
                ImageLoader.loadImage(mCurrentTrack.getCoverUrlLarge(), mTrackIcon);
                ImageLoader.loadImage(mCurrentTrack.getCoverUrlMiddle(), mImgPlayer);
                mImgPlayer.startAnimation(mAnimPlaying);
                //设置当前声音的总时间
                mTotalTime.setText(Utils.secToTime(mCurrentTrack.getDuration()));
                //刷新适配器
                notifyAdapter();
                notifyView();//刷新当前声音的信息

            } else {//Radio

                mRadioSeekbar.setVisibility(View.GONE);
                mLayoutPlayerRadio.setVisibility(View.VISIBLE);
                mLayoutPlayerTrack.setVisibility(View.GONE);
                mCurrentRadio = (Radio) mPlayerManager.getCurrSound();
                mDateId = mCurrentRadio.getDataId();
                ImageLoader.loadImage(mCurrentRadio.getCoverUrlLarge(), mRadioImgIcon);
                ImageLoader.loadImage(mCurrentRadio.getCoverUrlLarge(), mImgPlayer);
                mImgPlayer.startAnimation(mAnimPlaying);
                mRadioPlayIcon.setImageDrawable(mPauseDrawable);
                mPlayerRadioTitle.setText(mCurrentRadio.getRadioName());
                mPlayerRadioTitle.setTextColor(Color.WHITE);
                if (!"".equals(mCurrentRadio.getRadioDesc())) {
                    mRadioSubname.setText(mCurrentRadio.getProgramName());
                    mRadioName.setText(mCurrentRadio.getRadioDesc());
                } else {
                    mRadioSubname.setText(mCurrentRadio.getProgramName());
                    mRadioName.setText(mCurrentRadio.getRadioDesc());
                }
                mPlayerRadioProgramName.setText(mCurrentRadio.getRadioName());
            }
            mPlayerContent.setVisibility(View.VISIBLE);


        }


        @Override
        public void onPlayPause() {
            Log.i("tog", "----------onPlayPause------");
            mImgPlayer.clearAnimation();
            mPlayerPause.setImageDrawable(mPlayDrawable);//暂停的时候设置播放的图片
            mRadioPlayIcon.setImageDrawable(mPlayDrawable);
            mDateId = -1;
            notifyAdapter();
        }

        @Override
        public void onPlayStop() {
            Log.i("tog", "----------onPlayStop------");
            mDateId = -1;
            mImgPlayer.clearAnimation();
        }

        @Override
        public void onSoundPlayComplete() {
            Log.i("tog", "----------onSoundPlayComplete------");
            mDateId = -1;
            notifyAdapter();
            mPlayerPause.setImageDrawable(mPlayDrawable);//播放完成的时候设置播放的图片
        }

        @Override
        public void onSoundPrepared() {
            Log.i("tog", "----------onSoundPrepared------");

        }

        @Override
        public void onSoundSwitch(PlayableModel playableModel, PlayableModel playableModel1) {
            Log.i("tog", "----------onSoundSwitch------");
            savePlayRecord(playableModel1);//保存播放记录
            mDateId = -1;
            mImgPlayer.clearAnimation();
            changePlayerView();//改变播放器的视图
        }

        @Override
        public void onBufferingStart() {
            Log.i("tog", "----------onBufferingStart------");
        }

        @Override
        public void onBufferingStop() {
            Log.i("tog", "----------onBufferingStop------");

        }

        @Override
        public void onBufferProgress(int i) {
            //Log.e("tog","----------onBufferProgress------");

        }

        @Override
        public void onPlayProgress(int i, int i1) {
            //Log.e("tog","----------onPlayProgress------");
            mCurrentTime.setText(Utils.secToTime(i / 1000));
            mSeekBar.setProgress((int) (i * 100.0 / i1));
            if (mCountdownpos == COUNTDOWNPOS.CURRENTTRACK) {
                if (i1 - 800 > i) {
                    mTxtTimeOff.setText(Utils.secToTime((i1 - i) / 1000));
                    mTimePopWindow.setCountDownTime(mCountdownpos, Utils.secToTime((i1 - i) / 1000));
                } else {
                    MainActivity.this.destory();
                }
            }


        }

        @Override
        public boolean onError(XmPlayerException e) {
            mLayoutLoading.setVisibility(View.GONE);
            Log.i("tog", "----------onError------" + e.getMessage());
            Toast.makeText(MainActivity.this, "播放失败，请重试", Toast.LENGTH_SHORT).show();
            return false;
        }
    };

    private void savePlayRecord(PlayableModel playableModel1) {
        if (null == playableModel1) {
            return;
        }
        String kind = playableModel1.getKind();
        if (kind.equals("track")) {
            Track track = (Track) playableModel1;
            SubordinatedAlbum album = track.getAlbum();
            PlayRecord record = new PlayRecord(2, track.getCoverUrlMiddle(), album.getAlbumTitle(),
                    track.getTrackTitle(), album.getAlbumId(), track.getDataId());
            mPlayRecordDbWrapper.insert(record);
        }
        if (kind.equals("radio") && mCurrentRadio != null) {
            PlayRecord record = new PlayRecord(3, mCurrentRadio.getCoverUrlSmall(), mCurrentRadio.getRadioName(), mCurrentRadio.getProgramName(),
                    playableModel1.getDataId(), -1);
            mPlayRecordDbWrapper.insert(record);
        }

    }

    private void notifyAdapter() {
        if (null != ((RecommendFragment) mHomeFragment.getFragment(0))) {//刷新推荐页面的适配器
            ((RecommendFragment) mHomeFragment.getFragment(0)).notifyRecommendAdapter();
        }
        if (null != mListPopWindow) {//刷新播放列表的适配器
            mListPopWindow.notifyListPopAdapte();
        }
        if (null != mAlbumDetailsFragment) {//刷新专辑详细信息里面的声音列表的适配器
            mAlbumDetailsFragment.notifyAdapter();
        }
        if (null != mRankTrackFragment) {//刷新榜单是声音列表的适配器
            mRankTrackFragment.notifyAdapter();
        }
    }

    /**
     * 当播放的声音发生改变的时候刷新声音的信息
     */
    private void notifyView() {
        SubordinatedAlbum album = mCurrentTrack.getAlbum();
        //设置专辑的图片
        ImageLoader.loadImage(album.getCoverUrlSmall(), mPlayerImgAlbumIcon);
        mPlayerTxtAlbumTitle.setText(album.getAlbumTitle());//专辑的标题
        mPlayerTrackTitle.setText(mCurrentTrack.getTrackTitle());
        mPlayerTxtPlayCount.setText(mCurrentTrack.getPlayCount() + "次播放");
        mPlayerTrackUpdateDate.setText(Utils.secToDate(mCurrentTrack.getUpdatedAt()));

    }

    /**
     * 改变播放器的视图
     */
    private void changePlayerView() {
        if (mPlayerManager.hasPreSound()) {//有上一首
            mPlayerPrevious.setEnabled(true);
            mPlayerPrevious.setImageDrawable(mPreviousDrawableNormal);
        } else {
            mPlayerPrevious.setEnabled(false);
            mPlayerPrevious.setImageDrawable(mPreviousDrawableDisable);
        }
        if (mPlayerManager.hasNextSound()) {//有下一首
            mPlayerNext.setEnabled(true);
            mPlayerNext.setImageDrawable(mNextDrawableNormal);
        } else {
            mPlayerNext.setEnabled(false);
            mPlayerNext.setImageDrawable(mNextDrawableDisable);
        }

    }

    public LocationClient mLocationClient = null;
    public String mLocationProvince = "陕西省";
    public String mLocationCity = "西安";
    public String mCityCode = "233";
    public BDLocationListener myListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (167 == bdLocation.getLocType()) {
                Toast.makeText(MainActivity.this, "定位失败,请检测GPS是否打开", Toast.LENGTH_SHORT).show();
            }
            if (null != bdLocation && null != bdLocation.getCityCode() && null != bdLocation.getCity()) {
                mLocationProvince = bdLocation.getProvince();
                mLocationCity = bdLocation.getCity();
                mCityCode = bdLocation.getCityCode();
                mLocationClient.unRegisterLocationListener(myListener);
            }

        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initPlayer();
        getLocation();
        initDate();
        initView();
        initFragment();
        initListener();
    }


    public void getLocation() {
        mLocationClient = new LocationClient(this);
        initLocation();
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        //注册监听函数
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mLocationClient.start();
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != 0 ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != 0
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != 0
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != 0
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != 0) {//检测权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                    , Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else {
            mLocationClient.start();
        }
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");
        //可选，默认gcj02，设置返回的定位结果坐标系
        option.setIsNeedAddress(true);
        //可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);
        //可选，默认false,设置是否使用gps
        option.setIgnoreKillProcess(false);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集
        mLocationClient.setLocOption(option);
    }

    private void initDate() {
        mActivity = this;
        mAnimPlaying = AnimationUtils.loadAnimation(mActivity, R.anim.playing);
        mAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.loading);
        getScreenSize();//获取屏幕的宽高
        mResources = getResources();
        mPlayRecordDbWrapper = PlayRecordDbWrapper.getInatance(this);
        mPlayDrawable = mResources.getDrawable(R.drawable.bg_player_play_selector);
        mPauseDrawable = mResources.getDrawable(R.drawable.bg_player_pause_selector);
        mPreviousDrawableDisable = mResources.getDrawable(R.drawable.player_toolbar_previous_disable);
        mPreviousDrawableNormal = mResources.getDrawable(R.drawable.bg_player_previous_selector);
        mNextDrawableDisable = mResources.getDrawable(R.drawable.player_toolbar_next_disable);
        mNextDrawableNormal = mResources.getDrawable(R.drawable.bg_player_next_selector);
        getLastPlayRecord();//获取播放的历史
    }


    private void getLastPlayRecord() {
        PlayRecord record = mPlayRecordDbWrapper.getLastPlayRecord();
        if (null != record) {
            if (record.getType() == 2) {//track
                getLastPlayTrackList(record.getAblumId(), record.getDataId());
            }
            if (record.getType() == 3) {//radio
                String ids = record.getAblumId() + ",";
                Log.i("tog", "ids = " + ids);
                CommonRequest.getInstanse().init(this, appSecret);
                CommonRequest.getInstanse().setUseHttps(true);
                Map<String, String> map = new HashMap<String, String>();
                map.put(DTransferConstants.RADIO_IDS, ids);
                CommonRequest.getRadiosByIds(map, new IDataCallBack<RadioListById>() {
                    @Override
                    public void onSuccess(RadioListById radioListById) {
                        if (null == radioListById || null == radioListById.getRadios() || radioListById.getRadios().size() == 0) {
                            Toast.makeText(MainActivity.this, "没有播放记录", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        List<Radio> radios = radioListById.getRadios();
                        mCurrentRadio = radios.get(0);

                        mRadioSeekbar.setVisibility(View.GONE);
                        mLayoutPlayerRadio.setVisibility(View.VISIBLE);
                        mLayoutPlayerTrack.setVisibility(View.GONE);
                        ImageLoader.loadImage(mCurrentRadio.getCoverUrlLarge(), mRadioImgIcon);
                        ImageLoader.loadImage(mCurrentRadio.getCoverUrlLarge(), mImgPlayer);
                        mPlayerRadioTitle.setText(mCurrentRadio.getRadioName());
                        mPlayerRadioTitle.setTextColor(Color.WHITE);
                        if (!"".equals(mCurrentRadio.getRadioDesc())) {
                            mRadioSubname.setText(mCurrentRadio.getProgramName());
                            mRadioName.setText(mCurrentRadio.getRadioDesc());
                        } else {
                            mRadioSubname.setText(mCurrentRadio.getProgramName());
                            mRadioName.setText(mCurrentRadio.getRadioDesc());
                        }
                        mPlayerRadioProgramName.setText(mCurrentRadio.getRadioName());
                    }

                    @Override
                    public void onError(int i, String s) {
                        Log.i("tog", "onError  i = " + i + "--Message = " + s);
                    }
                });
            }
        }
    }


    public void getLastPlayTrackList(long albumId, final long dataId) {
        Map<String, String> map = new HashMap<String, String>();
        CommonRequest.getInstanse().init(this, appSecret);
        CommonRequest.getInstanse().setUseHttps(true);
        map.put(DTransferConstants.ALBUM_ID, albumId + "");
        map.put(DTransferConstants.TRACK_ID, dataId + "");
        CommonRequest.getLastPlayTracks(map,
                new IDataCallBack<LastPlayTrackList>() {
                    @Override
                    public void onSuccess(LastPlayTrackList lastPlayTrackList) {
                        if (null != lastPlayTrackList) {
                            mLayoutPlayerRadio.setVisibility(View.GONE);
                            mLayoutPlayerTrack.setVisibility(View.VISIBLE);
                            List<Track> tracks = lastPlayTrackList.getTracks();

                            int position = 0;
                            for (int i = 0; i < tracks.size(); i++) {
                                if (dataId == tracks.get(i).getDataId()) {
                                    position = i;
                                    break;
                                }
                            }
                            Track track = tracks.get(position);

                            mTracks = tracks;
                            mPosition = position;
                            mCurrentTrack = tracks.get(position);
                            //加载大图
                            ImageLoader.loadImage(track.getCoverUrlLarge(), mTrackIcon);
                            ImageLoader.loadImage(track.getCoverUrlMiddle(), mImgPlayer);
                            //设置当前声音的总时间
                            mTotalTime.setText(Utils.secToTime(track.getDuration()));
                        } else {
                            Toast.makeText(MainActivity.this, "没有播放记录", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(int i, String s) {
                        Log.i("tog", i + "---" + s);
                    }
                });
    }

    private void initView() {
        if (!Utils.isNetworkAvailable(this)) {
            mNetworkAvailable.setVisibility(View.VISIBLE);
            mLayoutLoading.setVisibility(View.GONE);
            mPlayerContent.setVisibility(View.GONE);
        } else {
            mNetworkAvailable.setVisibility(View.GONE);
            mLayoutLoading.setVisibility(View.GONE);
            mPlayerContent.setVisibility(View.VISIBLE);
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mWidth, mWidth);
        mTrackIcon.setLayoutParams(params);
        LinearLayout.LayoutParams layoutIconSeekBarParams = new LinearLayout.LayoutParams(mWidth, mWidth + DisplayUtil.dip2px(this, 5));
        mLayoutIconSeekBar.setLayoutParams(layoutIconSeekBarParams);
        mTxtHome.setTextColor(Color.RED);
        mImgHome.setImageDrawable(getResources().getDrawable(R.mipmap.btn_home_down));
        mBottomView.setVisibility(View.GONE);
    }

    /**
     * 初始化播放器
     */
    private void initPlayer() {
        mPlayerManager = XmPlayerManager.getInstance(this);
        mPlayerManager.init();
        mPlayerManager.addPlayerStatusListener(mPlayerListener);
        mPlayerManager.setOnConnectedListerner(new XmPlayerManager.IConnectListener() {
            @Override
            public void onConnected() {
            }
        });
    }

    //---
    public void playList(CommonTrackList<Track> list, int position) {
        mTracks = list.getTracks();
        mPosition = position;
        mLayoutPlayerRadio.setVisibility(View.GONE);
        mLayoutPlayerTrack.setVisibility(View.VISIBLE);
        Track track = list.getTracks().get(position);
        if (mPlayerManager.isPlaying()) {
            if (PLAYINGSOUNT_TYPE_TRACK == mPlayerManager.getCurrPlayType()) {//Track
                Track currentTrack = (Track) mPlayerManager.getCurrSound();
                if (track.equals(currentTrack)) {
                    return;
                }
            }
        }

        if (!Utils.isNetworkAvailable(this)) {
            mNetworkAvailable.setVisibility(View.VISIBLE);
            mLayoutLoading.setVisibility(View.GONE);
            mPlayerContent.setVisibility(View.GONE);
        } else {
            mLayoutLoading.setVisibility(View.VISIBLE);
            mNetworkAvailable.setVisibility(View.GONE);
            mPlayerContent.setVisibility(View.GONE);
            mImgLoading.startAnimation(mAnimation);
        }
        mPlayerManager.playList(list, position);

    }

    public void playList(List<Track> list, int position) {
        mTracks = list;
        mPosition = position;
        mLayoutPlayerRadio.setVisibility(View.GONE);
        mLayoutPlayerTrack.setVisibility(View.VISIBLE);
        Track track = list.get(position);
        if (mPlayerManager.isPlaying()) {
            if (PLAYINGSOUNT_TYPE_TRACK == mPlayerManager.getCurrPlayType()) {//Track
                Track currentTrack = (Track) mPlayerManager.getCurrSound();
                if (track.equals(currentTrack)) {
                    return;
                }
            }
        }
        if (!Utils.isNetworkAvailable(this)) {
            mNetworkAvailable.setVisibility(View.VISIBLE);
            mLayoutLoading.setVisibility(View.GONE);
            mPlayerContent.setVisibility(View.GONE);
        } else {
            mLayoutLoading.setVisibility(View.VISIBLE);
            mNetworkAvailable.setVisibility(View.GONE);
            mPlayerContent.setVisibility(View.GONE);
            mImgLoading.startAnimation(mAnimation);
        }
        mPlayerManager.playList(list, position);
    }

    public void playRadio(Radio radio) {
        mLayoutPlayerRadio.setVisibility(View.VISIBLE);
        mLayoutPlayerTrack.setVisibility(View.GONE);
        int currPlayType = mPlayerManager.getCurrPlayType();
        if (currPlayType == PLAYINGSOUNT_TYPE_RADIO) {
            Radio radio1 = (Radio) mPlayerManager.getCurrSound();
            if (radio.equals(radio1)) {
                return;
            }
        }
        if (!Utils.isNetworkAvailable(this)) {
            mNetworkAvailable.setVisibility(View.VISIBLE);
            mLayoutLoading.setVisibility(View.GONE);
            mPlayerContent.setVisibility(View.GONE);
        } else {
            mLayoutLoading.setVisibility(View.VISIBLE);
            mNetworkAvailable.setVisibility(View.GONE);
            mPlayerContent.setVisibility(View.GONE);
            mImgLoading.startAnimation(mAnimation);
        }
        mCurrentRadio = radio;
        mPlayerManager.playRadio(radio);

    }

    public void playerPause() {
        mPlayerManager.pause();
    }

    public void switchState(STATE state) {
        //reSetView();
        switch (state) {
            case PLAYER://播放器的状态下
                mLayoutContent.setVisibility(View.GONE);
                mLayoutPlayer.setVisibility(View.VISIBLE);
                setBottomIndiVisible(false);
                reSetView();
                break;
            case BROADCAST://广播的状态下
                mCurrentState = state;
                mLayoutContent.setVisibility(View.VISIBLE);
                mLayoutPlayer.setVisibility(View.GONE);
                showFragment(mBroadcastFragment);
                reSetView();
                setBottomIndiVisible(true);
                mImgBroad.setImageDrawable(getResources().getDrawable(R.drawable.tab4_down));
                mTxtBroad.setTextColor(Color.RED);
                break;
            case HOME://首页的状态下
                mCurrentState = state;
                mLayoutContent.setVisibility(View.VISIBLE);
                mLayoutPlayer.setVisibility(View.GONE);
                showFragment(mHomeFragment);
                reSetView();
                setBottomIndiVisible(true);
                mImgHome.setImageDrawable(getResources().getDrawable(R.mipmap.btn_home_down));
                mTxtHome.setTextColor(Color.RED);
                break;
        }
    }

    //设置底部导航的View
    private void reSetView() {
        mImgHome.setImageDrawable(getResources().getDrawable(R.mipmap.btn_home));
        mImgBroad.setImageDrawable(getResources().getDrawable(R.drawable.tab4));
        mTxtHome.setTextColor(Color.BLACK);
        mTxtBroad.setTextColor(Color.BLACK);
    }

    private void initListener() {
        mPlayerPause.setOnClickListener(this);//Track
        mRadioPlayIcon.setOnClickListener(this);//Radio
        mPlayerPrevious.setOnClickListener(this);
        mPlayerNext.setOnClickListener(this);
        mPlayList.setOnClickListener(this);//播放列表
        mLayoutTime.setOnClickListener(this);
        mLayoutAlbumInfo.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mPlayerManager.seekToByPercent(seekBar.getProgress() / (float) seekBar.getMax());
            }
        });
        mLayoutHome.setOnClickListener(this);
        mLayoutBroad.setOnClickListener(this);
        mImgPlayer.setOnClickListener(this);
        mBtnTry.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_img_pause://Track暂停或播放
                if (mPlayerManager.isPlaying()) {
                    mPlayerManager.pause();
                } else if (null != mTracks && mTracks.size() > 1) {
                    mPlayerManager.playList(mTracks, mPosition);
                } else {
                    Toast.makeText(mActivity, "暂时没有可播放的声音", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.player_radio_play_icon://Radio
                if (mPlayerManager.isPlaying()) {
                    mPlayerManager.pause();
                } else if (null != mCurrentRadio) {
                    mPlayerManager.playRadio(mCurrentRadio);
                } else {
                    Toast.makeText(mActivity, "暂时没有可播放的声音", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.play_img_previous://上一首
                mPlayerManager.playPre();
                mPlayerManager.play();
                break;
            case R.id.play_img_next://下一首
                mPlayerManager.playNext();
                mPlayerManager.play();
                break;
            case R.id.play_list://播放列表
                initListPopupWindow();
                break;
            case R.id.player_track_layout_time://定时关闭
                initTimePopupWindow();
                break;
            case R.id.layout_album://专辑声音列表
                break;
            case R.id.main_layout_home://首页
                mFragmentTransaction = mFragmentManager.beginTransaction();
                hideAllFragemnt(mFragmentTransaction);
                switchState(STATE.HOME);
                break;
            case R.id.main_layout_broad://广播
                mFragmentTransaction = mFragmentManager.beginTransaction();
                hideAllFragemnt(mFragmentTransaction);
                switchState(STATE.BROADCAST);
                break;
            case R.id.main_img_player://播放
                mFragmentTransaction = mFragmentManager.beginTransaction();
                hideAllFragemnt(mFragmentTransaction);
                switchState(STATE.PLAYER);
                setContentVisible(false);
                break;
            case R.id.btn_try_agin: {//点击重试
                if (null != mTracks && mTracks.size() > mPosition && mPosition != -1) {
                    playList(mTracks, mPosition);
                    return;
                }
                if (null != mCurrentRadio) {
                    playRadio(mCurrentRadio);
                    return;
                }
                Toast.makeText(mActivity, "暂时没有可以播放的音乐", Toast.LENGTH_SHORT).show();
                break;
            }


        }
    }

    private void initTimePopupWindow() {
        if (null == mTimePopWindow) {
            mTimePopWindow = new TimePopupWindow(this);
        }
        mTimePopWindow.show();
    }

    private COUNTDOWNPOS mCountdownpos = COUNTDOWNPOS.NOOPEN;

    public enum COUNTDOWNPOS {
        NOOPEN, CURRENTTRACK, TEN, TWENTY, THIRTY, SIXTY, NIETY
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mTimePopWindow.isShowing()) {
                mTimePopWindow.setCountDownTime(mCountdownpos, (String) msg.obj);
            }
            mTxtTimeOff.setText((String) msg.obj);
        }
    };
    private int mSpace;
    private long mStartTime;
    private List<CountDownThread> mThreads = new ArrayList<>();

    public void setCountDownPosition(COUNTDOWNPOS downPosition) {
        mCountdownpos = downPosition;
        for (CountDownThread thread : mThreads) {
            thread.countDownStop();
        }
        mThreads.clear();
        if (downPosition == COUNTDOWNPOS.NOOPEN) {
            mTxtTimeOff.setText("定时关闭");
            return;
        }
        if (downPosition == COUNTDOWNPOS.CURRENTTRACK) {
            return;
        }
        switch (downPosition) {
            case TEN:
                mSpace = 600000;
                break;
            case TWENTY:
                mSpace = 1200000;
                break;
            case THIRTY:
                mSpace = 1800000;
                break;
            case SIXTY:
                mSpace = 3600000;
                break;
            case NIETY:
                mSpace = 5400000;
                break;
        }
        mStartTime = System.currentTimeMillis();
        CountDownThread thread = new CountDownThread(this, mStartTime, mSpace, mHandler);
        thread.start();
        mThreads.add(thread);

    }

    /**
     * 初始化PopupWindow
     */
    private void initListPopupWindow() {
        mListPopWindow = new ListPopupWindow(mActivity, mTracks, mPosition, (int) (mHeight * 0.7));
        mListPopWindow.show();
    }

    /**
     * 隐藏所有的Fragment
     */
    private void hideAllFragemnt(FragmentTransaction fragmentTransaction) {
        fragmentTransaction.hide(mHomeFragment);
        fragmentTransaction.hide(mBroadcastFragment);
    }

    public void hideContentAllFragment(FragmentTransaction fragmentTransaction) {
        if (null != mAlbumFragment) {
            fragmentTransaction.hide(mAlbumFragment);//专辑
        }
        if (null != mAlbumDetailsFragment) {
            fragmentTransaction.hide(mAlbumDetailsFragment);//专辑详细
        }
        if (null != mRankAlbumFragment) {
            fragmentTransaction.hide(mRankAlbumFragment);//榜单专辑
        }
        if (null != mRankTrackFragment) {
            fragmentTransaction.hide(mRankTrackFragment);//榜单声音列表
        }
        if (null != mAnchorListFragment) {
            fragmentTransaction.hide(mAnchorListFragment);//主播列表
        }
        if (null != mAnchorDetailsFragment) {
            fragmentTransaction.hide(mAnchorDetailsFragment);//主播详细
        }
        if (null != mAlbumListFragment) {
            fragmentTransaction.hide(mAnchorListFragment);//专辑列表
        }
        if (null != mAllAlbumListFragment) {
            fragmentTransaction.hide(mAllAlbumListFragment);//全部专辑
        }
        if (null != mAllTrackFragment) {
            fragmentTransaction.hide(mAllTrackFragment);//全部声音
        }
        if (null != mRadiosFragment) {
            fragmentTransaction.hide(mRadiosFragment);//广播列表
        }
        if (null != mProvinceRadiosFragment) {
            fragmentTransaction.hide(mProvinceRadiosFragment);//省市直播列表
        }
        if (null != mRadiosByCateFragment) {
            fragmentTransaction.hide(mRadiosByCateFragment);//分类直播列表
        }
        if (null != mPlayRecordFragment) {
            fragmentTransaction.hide(mPlayRecordFragment);//播放历史的Fragment
        }
        if (null != mSearchFragment) {
            fragmentTransaction.hide(mSearchFragment);//搜索的Fragment
        }
    }

    /**
     * 显示某个Fragment
     */
    private void showFragment(Fragment mFragemnt) {
        if (!mFragemnt.isAdded()) {
            mFragmentTransaction.add(R.id.layout_content, mFragemnt);
        }
        mFragmentTransaction.show(mFragemnt).commit();
    }

    private void initFragment() {
        mFragmentManager = getSupportFragmentManager();
        mHomeFragment = new HomeFragment();
        mBroadcastFragment = new BroadcastFragment();
        mAlbumFragment = new AlbumFragment();
        mAlbumDetailsFragment = new AlbumDetailsFragment();
        mPlayRecordFragment = new PlayRecordFragment();
        mFragmentManager.beginTransaction().add(R.id.layout_content, mHomeFragment).commit();
    }


    /**
     * 设置当前要显示那个内容
     *
     * @param visible 　true 显示专辑　false 显示播放
     */
    public void setContentVisible(boolean visible) {
        if (visible) {//显示专辑的内容
            mContentLinearLayout.setVisibility(View.GONE);
            mAlbumContent.setVisibility(View.VISIBLE);
        } else {
            mContentLinearLayout.setVisibility(View.VISIBLE);
            mAlbumContent.setVisibility(View.GONE);
        }
    }

    public void setBottomIndiVisible(boolean visible) {
        if (visible) {
            mLayoutBottom.setVisibility(View.VISIBLE);
            mLayoutPlayerIcon.setVisibility(View.VISIBLE);
        } else {
            mLayoutBottom.setVisibility(View.GONE);
            mLayoutPlayerIcon.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {

        mFragmentTransaction = mFragmentManager.beginTransaction();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        if (mLayoutPlayer.getVisibility() == View.VISIBLE && mStack.isEmpty()) {//首页切换至播放页面
            Log.e("tog", "從播放页面切换至Home");
            mLayoutPlayerIcon.setVisibility(View.VISIBLE);
            switchState(mCurrentState);
            setContentVisible(false);
            return;
        } else if (mLayoutPlayer.getVisibility() == View.VISIBLE && !mStack.isEmpty()) {
            Log.e("tog", "从播放页面切换至上一个Fragment");
            mLayoutPlayerIcon.setVisibility(View.VISIBLE);
            Fragment fragment = mStack.pop();
            hideContentAllFragment(fragmentTransaction);
            setContentVisible(true);
            fragmentTransaction.show(fragment).commit();
            return;
        }
        if (!mStack.isEmpty()) {
            Log.e("tog", "不在播放页面，切换至上一个页面");
            mLayoutPlayerIcon.setVisibility(View.VISIBLE);
            Fragment fragment = mStack.pop();
            hideContentAllFragment(fragmentTransaction);
            setContentVisible(true);
            fragmentTransaction.show(fragment).commit();
            return;
        } else if (mStack.isEmpty() && mAlbumContent.getVisibility() == View.VISIBLE) {
            Log.e("tog", "回退栈中没有Fragment,切换至Home");
            setContentVisible(false);
            return;
        } else {
            if (null == mDialog) {
                initDialog();
            }
            mDialog.show();
        }

    }

    private void initDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_exit, null);
        RadioButton cancel = (RadioButton) view.findViewById(R.id.btn_dialog_cancel);
        RadioButton min = (RadioButton) view.findViewById(R.id.btn_dialog_min);
        RadioButton exit = (RadioButton) view.findViewById(R.id.btn_dialog_exit);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveTaskToBack(true);
                mDialog.dismiss();
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                MainActivity.super.onBackPressed();
            }
        });
        builder.setView(view);
        mDialog = builder.create();
    }

    public void destory() {
        if (null != mPlayerManager) {
            mPlayerManager.stop();
            mPlayerManager.removePlayerStatusListener(mPlayerListener);
            mPlayerManager.release();
        }
        Log.i("tog", "--------------");
        System.exit(0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length == 5) {
                mLocationClient.start();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (null != mPlayerManager) {
            mPlayerManager.stop();
            mPlayerManager.removePlayerStatusListener(mPlayerListener);
            mPlayerManager.release();
        }
        super.onDestroy();
    }

    /**
     * 获取屏幕的宽高
     *
     * @return
     */
    public int[] getScreenSize() {
        WindowManager manager = (WindowManager) this.getSystemService(WINDOW_SERVICE);
        Display defaultDisplay = manager.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(outMetrics);
        mWidth = outMetrics.widthPixels;
        mHeight = outMetrics.heightPixels;
        return new int[]{mWidth, mHeight};
    }
}
