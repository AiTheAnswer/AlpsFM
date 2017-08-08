package com.allen.user.alpsfm.fragment;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.allen.user.alpsfm.MainActivity;
import com.allen.user.alpsfm.R;
import com.allen.user.alpsfm.adapter.HomeFragmentAdapter;
import com.allen.user.alpsfm.adapter.SuggestWordAdapter;
import com.allen.user.alpsfm.model.SearchWradEntity;
import com.allen.user.alpsfm.utils.Utils;
import com.allen.user.alpsfm.view.WarpLinearLayout;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.allen.user.alpsfm.fragment.RecommendFragment.appSecret;

/**
 * Created by user on 17-3-28.
 */

public class SearchFragment extends Fragment implements TextWatcher, View.OnClickListener, AdapterView.OnItemClickListener {
    @BindView(R.id.include_search_history_empty)
    TextView mEmptyHistory;
    @BindView(R.id.search_history_warplinearlayout)
    WarpLinearLayout mHistoryWarpLayout;
    @BindView(R.id.linear_search_history)
    LinearLayout mHistoryLayout;
    @BindView(R.id.search_hot_words_warplinearlayout)
    WarpLinearLayout mHotWardsWarpLayout;
    @BindView(R.id.linear_search_hot_words)
    LinearLayout mHotWardsLayout;
    @BindView(R.id.img_delete_search_text)
    ImageView mImgDelete;
    @BindView(R.id.txt_search)
    TextView mTxtSearch;
    @BindView(R.id.autotext_search)
    EditText mAutoSearch;
    @BindView(R.id.fragment_isNetworkAvailable)
    RelativeLayout mNetworkAvailable;
    @BindView(R.id.search_content)
    LinearLayout mSearchContent;
    @BindView(R.id.search_suggest_word_lst)
    ListView mSuggestListView;
    @BindView(R.id.search_history_hotwards_content)
    ScrollView mHistoryHotwardsContent;
    @BindView(R.id.search_result_tablayout)
    TabLayout mTablayout;
    @BindView(R.id.search_result_viewPager)
    ViewPager mViewPager;
    @BindView(R.id.search_result_content)
    LinearLayout mResultContent;

    private SearchResultAlbumFragment mSearchResultAlbumFragment;
    private SearchResultTrackFragment mSearchResultTrackFragment;
    private MainActivity mActivity;
    private String mSerachWard = "";
    private LayoutInflater mInflater;
    private SuggestWordAdapter mAdapter;
    private List<String> mDates = new ArrayList<>();
    private HomeFragmentAdapter mViewPagerAdapter;
    private List<Fragment> mFragments;
    private String[] mTitle = {"专辑", "声音"};
    private boolean isLoadHotWard = true;
    private AlertDialog mDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, null);
        ButterKnife.bind(this, view);
        mActivity = (MainActivity) getActivity();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initListener();
        initXiMaLaya();
        initFragments();
        loadDate();
        mTablayout.setupWithViewPager(mViewPager);
        mAdapter = new SuggestWordAdapter(mDates, mActivity);
        mSuggestListView.setAdapter(mAdapter);

    }

    private void initFragments() {
        mFragments = new ArrayList<>();
        mSearchResultAlbumFragment = new SearchResultAlbumFragment();
        mSearchResultTrackFragment = new SearchResultTrackFragment();
        mFragments.add(mSearchResultAlbumFragment);
        mFragments.add(mSearchResultTrackFragment);
        mViewPagerAdapter = new HomeFragmentAdapter(getChildFragmentManager(), mFragments, mTitle);
        mViewPager.setAdapter(mViewPagerAdapter);

    }

    private void updateSearchWord() {
        isLoadHotWard = false;
        mAutoSearch.setText(mSerachWard);
        mAutoSearch.setSelection(mSerachWard.length());
        mSearchResultAlbumFragment.setSearchWord(mSerachWard);
        mSearchResultTrackFragment.setSearchWord(mSerachWard);
        mViewPager.setCurrentItem(0);
        mActivity.mPlayRecordDbWrapper.insertWard(new SearchWradEntity(mSerachWard, System.currentTimeMillis()));
        showSearchResult();
    }


    private void loadDate() {
        getSearchHistory();
        if (!Utils.isNetworkAvailable(mActivity)) {
            mNetworkAvailable.setVisibility(View.VISIBLE);
            mSearchContent.setVisibility(View.GONE);
            return;
        } else {
            mNetworkAvailable.setVisibility(View.GONE);
            mSearchContent.setVisibility(View.VISIBLE);
        }

        getHotWards();//获取当前搜索热词
    }

    private void getSearchHistory() {
        List<SearchWradEntity> allWard = mActivity.mPlayRecordDbWrapper.getAllWard();
        if (null == allWard || allWard.size() < 1) {
            mHistoryWarpLayout.setVisibility(View.GONE);
            return;
        }
        mHistoryWarpLayout.setVisibility(View.VISIBLE);
        mHistoryWarpLayout.removeAllViews();
        for (SearchWradEntity entity : allWard) {
            TextView textView = (TextView) mInflater.inflate(R.layout.item_search_history_text, null);
            textView.setText(entity.getWard());
            mHistoryWarpLayout.addView(textView);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView textView = (TextView) v;
                    mSerachWard = textView.getText().toString();
                    updateSearchWord();
                }
            });
        }
    }

    private void getSuggestWord(String searchKey) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.SEARCH_KEY, searchKey);
        CommonRequest.getSuggestWord(map, new IDataCallBack<SuggestWords>() {
            @Override
            public void onSuccess(SuggestWords suggestWords) {
                if (null == suggestWords || null == suggestWords.getKeyWordList()) {
                    return;
                }
                showSuggestList();
                mDates.clear();
                List<QueryResult> keyWordList = suggestWords.getKeyWordList();
                for (QueryResult result : keyWordList) {
                    mDates.add(result.getKeyword());
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    private void getHotWards() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.TOP, "10");
        CommonRequest.getHotWords(map, new IDataCallBack<HotWordList>() {
            @Override
            public void onSuccess(HotWordList hotWordList) {
                List<HotWord> hotWords = hotWordList.getHotWordList();
                if (null == hotWordList || null == hotWords || hotWords.size() < 1) {
                    mHotWardsLayout.setVisibility(View.GONE);
                    return;
                }
                mHotWardsLayout.setVisibility(View.VISIBLE);
                for (HotWord hotWord : hotWords) {
                    TextView view = (TextView) mInflater.inflate(R.layout.item_search_text, null);
                    view.setText(hotWord.getSearchword());
                    mHotWardsWarpLayout.addView(view);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TextView textView = (TextView) v;
                            mSerachWard = textView.getText().toString();
                            updateSearchWord();
                        }
                    });
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    private void initListener() {
        mAutoSearch.addTextChangedListener(this);
        mImgDelete.setOnClickListener(this);
        mNetworkAvailable.setOnClickListener(this);
        mSuggestListView.setOnItemClickListener(this);
        mTxtSearch.setOnClickListener(this);
        mEmptyHistory.setOnClickListener(this);
    }


    private void initView() {
        mImgDelete.setVisibility(View.GONE);
        mInflater = LayoutInflater.from(mActivity);
        mTablayout.setSelectedTabIndicatorColor(Color.RED);
        mTablayout.setTabTextColors(Color.BLACK, Color.RED);
        showHistoryHotContent();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        mSerachWard = s.toString();
        if (mSerachWard.length() > 0) {
            showSuggestList();
            mImgDelete.setVisibility(View.VISIBLE);
        } else {
            mImgDelete.setVisibility(View.GONE);
            showHistoryHotContent();
        }
        if (isLoadHotWard) {
            getSuggestWord(mSerachWard);//获取某个关键词的联想词
        }
        isLoadHotWard = true;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_delete_search_text://清除已输入的文本
                mAutoSearch.setText("");
                isLoadHotWard = true;
                break;
            case R.id.fragment_isNetworkAvailable://点击重新加载
                notifyDate();
                break;
            case R.id.txt_search://搜索
                if (mSerachWard.equals("")) {
                    Toast.makeText(mActivity, "请输入内容在搜索", Toast.LENGTH_SHORT).show();
                    return;
                }
                updateSearchWord();
                break;
            case R.id.include_search_history_empty://清空播放历史
                showClearDialog();
                break;
        }
    }

    private void showClearDialog() {
        if (null == mDialog) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            LayoutInflater inflater = LayoutInflater.from(mActivity);
            View view = inflater.inflate(R.layout.dialog_playrecord_delte, null);
            TextView dialogMessage = (TextView) view.findViewById(R.id.dialog_txt_delete_message);
            TextView cancel = (TextView) view.findViewById(R.id.dialog_txt_delete_cancel);
            TextView confirm = (TextView) view.findViewById(R.id.dialog_txt_delete_confirm);
            dialogMessage.setText("确认清空搜索历史?");
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                }
            });
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActivity.mPlayRecordDbWrapper.clearAllWard();
                    getSearchHistory();
                    mDialog.dismiss();
                }
            });
            builder.setView(view);
            mDialog = builder.create();
        }
        mDialog.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mSerachWard = mDates.get(position);
        updateSearchWord();
    }

    private void showSearchResult() {
        mSuggestListView.setVisibility(View.GONE);
        mHistoryHotwardsContent.setVisibility(View.GONE);
        mResultContent.setVisibility(View.VISIBLE);
    }

    private void showSuggestList() {
        mSuggestListView.setVisibility(View.VISIBLE);
        mHistoryHotwardsContent.setVisibility(View.GONE);
        mResultContent.setVisibility(View.GONE);
    }

    private void showHistoryHotContent() {
        mSuggestListView.setVisibility(View.GONE);
        mHistoryHotwardsContent.setVisibility(View.VISIBLE);
        mResultContent.setVisibility(View.GONE);
        getSearchHistory();
    }

    private void initXiMaLaya() {
        CommonRequest.getInstanse().init(mActivity, appSecret);
        CommonRequest.getInstanse().setUseHttps(true);
    }

    public void notifyDate() {
        if (null != mActivity) {
            loadDate();
        }
    }
}