package com.allen.user.alpsfm.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.allen.user.alpsfm.MainActivity;
import com.allen.user.alpsfm.view.TimePopupWindow;

/**
 * Created by user on 17-3-30.
 */

public class CountDownThread extends Thread {
    private boolean mIsStop = false;
    private MainActivity mActivty;

    public void countDownStop() {
        mIsStop = true;
    }

    private Handler mHandler;

    public CountDownThread(MainActivity activity, long startTime, long space, Handler handler) {
        this.mActivty = activity;
        this.startTime = startTime;
        this.space = space;
        this.mHandler = handler;
    }

    private long startTime;
    private long space;

    @Override
    public void run() {
        while (!mIsStop && System.currentTimeMillis() - startTime < space) {
            Message message = Message.obtain();
            message.obj = Utils.secToTime((space - (System.currentTimeMillis() - startTime)) / 1000);
            mHandler.sendMessage(message);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (!mIsStop) {
            mActivty.destory();
        }
    }
}
