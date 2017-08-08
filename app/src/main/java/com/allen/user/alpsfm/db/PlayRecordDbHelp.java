package com.allen.user.alpsfm.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by user on 17-3-25.
 */

public class PlayRecordDbHelp extends SQLiteOpenHelper {
    private static PlayRecordDbHelp mDatabaseHelper = null;

    //数据库名称
    public static final String DATABASE_NAME = "playrecord.db";

    //数据库版本号
    private static final int DATABASE_VERSION = 1;
    //数据库的播放记录表名
    public static final String TABLE_NAME = "playrecord";
    //专辑Id列名
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_ALBUMID = "blumid";
    public static final String COLUMN_TRACKID = "dataid";
    public static final String COLUMN_IMGURL = "imgurl";
    public static final String COLUMN_ALbumTitle = "albumtitle";
    public static final String COLUMN_TRACKNAME = "trackname";

    //数据库SQL语句,添加一个表
    private static final String NAME_TABLE_CREATE = "create table " + TABLE_NAME + "("
            + "id integer primary key autoincrement,"//主键
            + "type integer,"//类型 2 track 3 radio
            + "blumid long,"//专辑的Id
            + "dataid long,"//track或radio的id
            + "imgurl text,"
            + "albumtitle text,"
            + "trackname text)";

    //---------搜索记录
    //数据库的搜索记录的表名
    public static final String TABLE_SEARCH = "searchward";
    public static final String COLUMN_WORD = "ward";
    public static final String COLUMN_TIME = "time";
    private static final String NAME_TABLE_TIME_CREATE = "create table " + TABLE_SEARCH + "("
            + "id integer primary key autoincrement,"//主键
            + "ward text,"//搜索的词
            + "time long)";//搜索时间

    public PlayRecordDbHelp(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static PlayRecordDbHelp getInstance(Context context) {
        if (null == mDatabaseHelper) {
            mDatabaseHelper = new PlayRecordDbHelp(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        return mDatabaseHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(NAME_TABLE_CREATE);
        db.execSQL(NAME_TABLE_TIME_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


}
