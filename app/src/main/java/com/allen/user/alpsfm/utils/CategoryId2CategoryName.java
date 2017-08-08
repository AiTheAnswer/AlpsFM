package com.allen.user.alpsfm.utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by user on 17-3-1.
 */

public class CategoryId2CategoryName {
    public static final HashMap<Long, String> map = new HashMap<>();
    public static final List<Long> keyList = new ArrayList<Long>();

    static {
        keyList.add(1L);
        keyList.add(2L);
        keyList.add(3L);
        keyList.add(4L);
        keyList.add(6L);
        keyList.add(7L);
        keyList.add(8L);
        keyList.add(9L);
        keyList.add(10L);
        keyList.add(11L);
        keyList.add(12L);
        keyList.add(13L);
        keyList.add(15L);
        keyList.add(16L);
        keyList.add(17L);
        keyList.add(18L);
        keyList.add(21L);
        keyList.add(22L);
        keyList.add(23L);
        keyList.add(24L);
        keyList.add(28L);
        keyList.add(29L);
        keyList.add(30L);
        keyList.add(31L);
        keyList.add(32L);
        keyList.add(34L);
        keyList.add(38L);
        keyList.add(39L);
        keyList.add(40L);
        map.put(1L, "咨询");
        map.put(2L, "音乐");
        map.put(3L, "有声书");
        map.put(4L, "娱乐");
        map.put(6L, "儿童");
        map.put(7L, "健康养生");
        map.put(8L, "商业财经");
        map.put(9L, "历史");
        map.put(10L, "情感生活");
        map.put(11L, "其他");
        map.put(12L, "相声评书");
        map.put(13L, "教育培训");
        map.put(15L, "广播剧");
        map.put(16L, "戏曲");
        map.put(17L, "电台");
        map.put(18L, "IT科技");
        map.put(21L, "汽车");
        map.put(22L, "旅游");
        map.put(23L, "电影");
        map.put(24L, "动漫游戏");
        map.put(28L, "脱口秀");
        map.put(29L, "3D体验馆");
        map.put(30L, "名校公开课");
        map.put(31L, "时尚生活");
        map.put(32L, "小语种");
        map.put(34L, "诗歌");
        map.put(38L, "英语");
        map.put(39L, "人文");
        map.put(40L, "百家讲坛");
    }

    public static String getCategoryName(long categoryId) {
        if (keyList.contains(categoryId)) {
            return map.get(categoryId);
        }else{
            return "";
        }

    }
}
