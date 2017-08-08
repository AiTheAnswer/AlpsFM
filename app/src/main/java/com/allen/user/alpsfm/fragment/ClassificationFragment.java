package com.allen.user.alpsfm.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.allen.user.alpsfm.MainActivity;
import com.allen.user.alpsfm.R;
import com.allen.user.alpsfm.adapter.ClassificationGridAdapter;
import com.allen.user.alpsfm.utils.Utils;
import com.allen.user.alpsfm.view.SwipyRefreshLayout;
import com.allen.user.alpsfm.view.SwipyRefreshLayoutDirection;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.category.Category;
import com.ximalaya.ting.android.opensdk.model.category.CategoryList;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by user on 17-2-27.
 */

public class ClassificationFragment extends Fragment implements AdapterView.OnItemClickListener {
    @BindView(R.id.gridview_classification)
    GridView mGridView;
    @BindView(R.id.fragment_isNetworkAvailable)
    RelativeLayout mNetworkAvailable;
    @BindView(R.id.img_loading)
    ImageView mImgLoading;
    @BindView(R.id.relative_loading)
    RelativeLayout mLayoutLoading;
    @BindView(R.id.btn_try_agin)
    Button mBtnTry;
    //数据源
    private List<Category> mCategories;
    //适配器
    private ClassificationGridAdapter mAdapter;
    private MainActivity mActivity;
    private FragmentManager mFragmentManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classification, container, false);
        ButterKnife.bind(this, view);
        mActivity = (MainActivity) getActivity();
        mFragmentManager = mActivity.getSupportFragmentManager();
        initXimaLaYa();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getContentCategory();
        initListener();
    }

    private void initListener() {
        mGridView.setOnItemClickListener(this);
        mBtnTry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContentCategory();
            }
        });
    }

    private void initXimaLaYa() {
        CommonRequest.getInstanse().init(mActivity, "4d8e605fa7ed546c4bcb33dee1381179");
        CommonRequest.getInstanse().setUseHttps(true);
    }

    /**
     * 获取所有的类别
     */
    public void getContentCategory() {
        if (!Utils.isNetworkAvailable(mActivity)) {
            mNetworkAvailable.setVisibility(View.VISIBLE);
            mLayoutLoading.setVisibility(View.GONE);
            mGridView.setVisibility(View.GONE);
            return;
        } else {
            mNetworkAvailable.setVisibility(View.GONE);
            mLayoutLoading.setVisibility(View.VISIBLE);
            mGridView.setVisibility(View.GONE);
            mImgLoading.startAnimation(mActivity.mAnimation);
        }
        CommonRequest.getCategories(null, new IDataCallBack<CategoryList>() {
            @Override
            public void onSuccess(CategoryList categoryList) {
                mLayoutLoading.setVisibility(View.GONE);
                mCategories = categoryList.getCategories();
                mAdapter = new ClassificationGridAdapter(mCategories, mActivity);
                mGridView.setAdapter(mAdapter);
                mGridView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(int code, String message) {
                mCategories = new ArrayList<Category>();
                mLayoutLoading.setVisibility(View.GONE);
                mNetworkAvailable.setVisibility(View.VISIBLE);
                mGridView.setVisibility(View.GONE);
                Toast.makeText(mActivity, "网络异常", Toast.LENGTH_SHORT).show();
                Log.i("category", "获取喜马拉雅内容分类－－－－" + code + "   " + message);
            }
        });
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        long classificationId = mCategories.get(position).getId();
        mActivity.mClassificationId = classificationId;
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        mActivity.hideContentAllFragment(fragmentTransaction);
        mActivity.setContentVisible(true);
        if (!mActivity.mAlbumFragment.isAdded()) {
            fragmentTransaction.add(R.id.content, mActivity.mAlbumFragment);
        }
        fragmentTransaction.show(mActivity.mAlbumFragment).commit();
        mActivity.mAlbumFragment.setClassification(classificationId);

    }

}
