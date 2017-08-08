package com.allen.user.alpsfm.application;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.ximalaya.ting.android.opensdk.model.track.TrackHotList;
import com.ximalaya.ting.android.sdkdownloader.XmDownloadManager;

import org.xutils.x;

import java.io.File;

/**
 * Created by user on 17-3-1.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //创建默认的ImageLoader配置参数
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024) // 50 MiB
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .diskCache(new UnlimitedDiskCache(new File(Environment.getExternalStorageDirectory() + "/target")))
                .build();

        ImageLoader.getInstance().init(configuration);
        x.Ext.init(this);
    }


}
