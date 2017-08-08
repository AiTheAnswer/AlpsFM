package com.allen.user.alpsfm.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by user on 17-3-7.
 */

public class MySharedPreferences {
    private static SharedPreferences preferences;

    public static SharedPreferences getPreferences(Context activity) {

        preferences = activity
                .getSharedPreferences("com.allen.alpsFm_datas", Context.MODE_PRIVATE);
        return preferences;
    }
}
