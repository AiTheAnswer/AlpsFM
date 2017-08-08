package com.allen.user.alpsfm.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by user on 17-3-18.
 */

public class
Utils {
    public static final HashMap<String, String> map = new HashMap<>();
    public static final List<String> keyList = new ArrayList<String>();

    static {
        keyList.add("北京省");
        keyList.add("天津省");
        keyList.add("河北省");
        keyList.add("山西省");
        keyList.add("内蒙古省");
        keyList.add("辽宁省");
        keyList.add("吉林省");
        keyList.add("黑龙江省");
        keyList.add("上海省");
        keyList.add("江苏省");
        keyList.add("浙江省");
        keyList.add("安徽省");
        keyList.add("福建省");
        keyList.add("江西省");
        keyList.add("山东省");
        keyList.add("河南省");
        keyList.add("湖北省");
        keyList.add("湖南省");
        keyList.add("广东省");
        keyList.add("广西省");
        keyList.add("海南省");
        keyList.add("重庆省");
        keyList.add("四川省");
        keyList.add("贵州省");
        keyList.add("云南省");
        keyList.add("西藏省");
        keyList.add("陕西省");
        keyList.add("甘肃省");
        keyList.add("青海省");
        keyList.add("宁夏省");
        keyList.add("新疆省");
        map.put("北京省", "110000");
        map.put("天津省", "120000");
        map.put("河北省", "130000");
        map.put("山西省", "140000");
        map.put("内蒙古省", "150000");
        map.put("辽宁省", "210000");
        map.put("吉林省", "220000");
        map.put("黑龙江省", "230000");
        map.put("上海省", "310000");
        map.put("江苏省", "320000");
        map.put("浙江省", "330000");
        map.put("安徽省", "340000");
        map.put("福建省", "350000");
        map.put("江西省", "360000");
        map.put("山东省", "370000");
        map.put("河南省", "410000");
        map.put("湖北省", "420000");
        map.put("湖南省", "430000");
        map.put("广东省", "440000");
        map.put("广西省", "450000");
        map.put("海南省", "460000");
        map.put("重庆省", "500000");
        map.put("四川省", "510000");
        map.put("贵州省", "520000");
        map.put("云南省", "530000");
        map.put("西藏省", "540000");
        map.put("陕西省", "610000");
        map.put("甘肃省", "620000");
        map.put("青海省", "630000");
        map.put("宁夏省", "640000");
        map.put("新疆省", "650000");
    }

    /**
     * 判断当前手机是否有网络连接
     *
     * @param activity
     * @return
     */
    public static boolean isNetworkAvailable(Activity activity) {
        Context context = activity.getApplicationContext();
        //通过Context来获取ConnectivityManager
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        } else {
            //通过ConnectivityManager的对象来获取当前所有来连接网络的类型　
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    //判断当前网络是否连接
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static String getProvinceCode(String provinceName) {
        if (!keyList.contains(provinceName)) {
            return "110000";
        } else {
            return map.get(provinceName);
        }
    }

    /**
     * 将秒数格式化
     *
     * @param time
     * @return
     */
    public static String secToTime(long time) {
        String timeStr = null;
        long hour = 0;
        long minute = 0;
        long second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String unitFormat(long i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Long.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    public static String secToDate(long date) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        return formatter.format(calendar.getTime());
    }

}
