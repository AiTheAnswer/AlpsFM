package com.allen.user.alpsfm.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by user on 17-3-1.
 */

public class WindowSize {
    public static String [] getWindowSize(Context mContext){
        WindowManager windowManager  = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = windowManager.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(outMetrics);
        return new String[]{outMetrics.widthPixels+"",outMetrics.heightPixels+""};
    }
}
