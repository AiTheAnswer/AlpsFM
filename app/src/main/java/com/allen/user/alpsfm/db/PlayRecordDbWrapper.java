package com.allen.user.alpsfm.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.allen.user.alpsfm.model.PlayRecord;
import com.allen.user.alpsfm.model.PlayRecordEntity;
import com.allen.user.alpsfm.model.SearchWradEntity;

import java.util.ArrayList;
import java.util.List;

import static com.allen.user.alpsfm.db.PlayRecordDbHelp.COLUMN_ALBUMID;
import static com.allen.user.alpsfm.db.PlayRecordDbHelp.COLUMN_ALbumTitle;
import static com.allen.user.alpsfm.db.PlayRecordDbHelp.COLUMN_IMGURL;
import static com.allen.user.alpsfm.db.PlayRecordDbHelp.COLUMN_TIME;
import static com.allen.user.alpsfm.db.PlayRecordDbHelp.COLUMN_TRACKID;
import static com.allen.user.alpsfm.db.PlayRecordDbHelp.COLUMN_TRACKNAME;
import static com.allen.user.alpsfm.db.PlayRecordDbHelp.COLUMN_TYPE;
import static com.allen.user.alpsfm.db.PlayRecordDbHelp.COLUMN_WORD;
import static com.allen.user.alpsfm.db.PlayRecordDbHelp.TABLE_NAME;
import static com.allen.user.alpsfm.db.PlayRecordDbHelp.TABLE_SEARCH;

/**
 * Created by user on 17-3-26.
 */

public class PlayRecordDbWrapper {
    private PlayRecordDbHelp mPlayRecordDbHelp;
    private static PlayRecordDbWrapper mPlayRecordDbWrapper;

    private PlayRecordDbWrapper(Context context) {
        mPlayRecordDbHelp = PlayRecordDbHelp.getInstance(context);
    }

    public static PlayRecordDbWrapper getInatance(Context context) {
        if (null == mPlayRecordDbWrapper) {
            mPlayRecordDbWrapper = new PlayRecordDbWrapper(context);
        }
        return mPlayRecordDbWrapper;
    }

    public long insert(PlayRecord playRecord) {
        albumIsExist(playRecord.getAblumId());//广播或者是Track且track的专辑是不存在的
        SQLiteDatabase mWritableDb = mPlayRecordDbHelp.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TYPE, playRecord.getType());
        contentValues.put(COLUMN_ALBUMID, playRecord.getAblumId());
        contentValues.put(COLUMN_TRACKID, playRecord.getDataId());
        contentValues.put(COLUMN_IMGURL, playRecord.getUrl());
        contentValues.put(COLUMN_ALbumTitle, playRecord.getAlbumName());
        contentValues.put(COLUMN_TRACKNAME, playRecord.getTrackName());
        long playrecord = mWritableDb.insert(TABLE_NAME, playRecord.getDataId() + "", contentValues);
        mWritableDb.close();
        return playrecord;
    }

    /**
     * 判断Track 的专辑是否存在
     *
     * @param albumId
     * @return true 存在 false  不存在
     */
    public void albumIsExist(long albumId) {
        SQLiteDatabase mReadableDb = mPlayRecordDbHelp.getReadableDatabase();
        Cursor cursor = mReadableDb.query(TABLE_NAME, null, null, null, null, null, null);
        if (null != cursor) {
            cursor.moveToFirst();
            if (cursor.getCount() == 0) {
                return;
            }
            int blumIdIndex0 = cursor.getColumnIndex(COLUMN_ALBUMID);
            long mAblumId0 = cursor.getLong(blumIdIndex0);
            if (mAblumId0 == albumId) {//该专辑已存在
                deletePlayRecord(albumId);
                cursor.close();
                mReadableDb.close();
                return;
            }
            while (cursor.moveToNext()) {
                int blumIdIndex = cursor.getColumnIndex(COLUMN_ALBUMID);
                long mAblumId = cursor.getLong(blumIdIndex);
                if (mAblumId == albumId) {//该专辑已存在
                    deletePlayRecord(albumId);
                    cursor.close();
                    mReadableDb.close();
                    return;
                }
            }
            cursor.close();
            mReadableDb.close();
        }
    }

    private void deletePlayRecord(long albumId) {
        SQLiteDatabase writableDatabase = mPlayRecordDbHelp.getWritableDatabase();
        writableDatabase.delete(TABLE_NAME, COLUMN_ALBUMID + "=?", new String[]{albumId + ""});
        writableDatabase.close();
    }

    public List<PlayRecord> getAllRecord() {
        SQLiteDatabase mReadableDb = mPlayRecordDbHelp.getReadableDatabase();
        List<PlayRecord> records = new ArrayList<>();
        Cursor cursor = mReadableDb.query(TABLE_NAME, null, null, null, null, null, "id desc", null);
        if (null != cursor) {
            cursor.moveToFirst();
            if (cursor.getCount() == 0) {
                return null;
            }
            int typeIndex1 = cursor.getColumnIndex(COLUMN_TYPE);
            int albumIdIndex1 = cursor.getColumnIndex(COLUMN_ALBUMID);
            int dataIdIndex1 = cursor.getColumnIndex(COLUMN_TRACKID);
            int imgUrlIndex1 = cursor.getColumnIndex(COLUMN_IMGURL);
            int albumTitleIndex1 = cursor.getColumnIndex(COLUMN_ALbumTitle);
            int trackNameIndex1 = cursor.getColumnIndex(COLUMN_TRACKNAME);
            int type1 = cursor.getInt(typeIndex1);
            long albumId1 = cursor.getLong(albumIdIndex1);
            long dataId1 = cursor.getLong(dataIdIndex1);
            String imgUrl1 = cursor.getString(imgUrlIndex1);
            String albumTitle1 = cursor.getString(albumTitleIndex1);
            String trackName1 = cursor.getString(trackNameIndex1);
            PlayRecord playRecord1 = new PlayRecord(type1, imgUrl1, albumTitle1, trackName1, albumId1, dataId1);
            records.add(playRecord1);
            while (cursor.moveToNext()) {
                int typeIndex = cursor.getColumnIndex(COLUMN_TYPE);
                int albumIdIndex = cursor.getColumnIndex(COLUMN_ALBUMID);
                int dataIdIndex = cursor.getColumnIndex(COLUMN_TRACKID);
                int imgUrlIndex = cursor.getColumnIndex(COLUMN_IMGURL);
                int albumTitleIndex = cursor.getColumnIndex(COLUMN_ALbumTitle);
                int trackNameIndex = cursor.getColumnIndex(COLUMN_TRACKNAME);
                int type = cursor.getInt(typeIndex);
                long albumId = cursor.getLong(albumIdIndex);
                long dataId = cursor.getLong(dataIdIndex);
                String imgUrl = cursor.getString(imgUrlIndex);
                String albumTitle = cursor.getString(albumTitleIndex);
                String trackName = cursor.getString(trackNameIndex);
                PlayRecord playRecord = new PlayRecord(type, imgUrl, albumTitle, trackName, albumId, dataId);
                records.add(playRecord);
            }
            cursor.close();
        }
        mReadableDb.close();
        return records;
    }

    public PlayRecord getLastPlayRecord() {
        SQLiteDatabase mReadableDb = mPlayRecordDbHelp.getReadableDatabase();
        Cursor cursor = mReadableDb.query(TABLE_NAME, null, null, null, null, null, "id desc", "1");
        if (null != cursor) {
            cursor.moveToFirst();
            int count = cursor.getCount();
            if (count == 0) {
                return null;
            }
            int typeIndex = cursor.getColumnIndex(COLUMN_TYPE);
            int albumIdIndex = cursor.getColumnIndex(COLUMN_ALBUMID);
            int dataIdIndex = cursor.getColumnIndex(COLUMN_TRACKID);
            int imgUrlIndex = cursor.getColumnIndex(COLUMN_IMGURL);
            int albumTitleIndex = cursor.getColumnIndex(COLUMN_ALbumTitle);
            int trackNameIndex = cursor.getColumnIndex(COLUMN_TRACKNAME);
            int type = cursor.getInt(typeIndex);
            long albumId = cursor.getLong(albumIdIndex);
            long dataId = cursor.getLong(dataIdIndex);
            String imgUrl = cursor.getString(imgUrlIndex);
            String albumTitle = cursor.getString(albumTitleIndex);
            String trackName = cursor.getString(trackNameIndex);
            PlayRecord playRecord = new PlayRecord(type, imgUrl, albumTitle, trackName, albumId, dataId);
            cursor.close();
            return playRecord;
        }
        mReadableDb.close();
        return null;
    }

    public void clearAll() {
        SQLiteDatabase mWritableDb = mPlayRecordDbHelp.getWritableDatabase();
        mWritableDb.delete(TABLE_NAME, null, null);
        mWritableDb.close();
    }

    // 搜索数据表的插入
    public void insertWard(SearchWradEntity entity) {
        if (!wardIsExit(entity)) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_WORD, entity.getWard());
            values.put(COLUMN_TIME, entity.getTime());
            SQLiteDatabase writableDatabase = mPlayRecordDbHelp.getWritableDatabase();
            writableDatabase.insert(TABLE_SEARCH, null, values);
            writableDatabase.close();
        }
    }

    public boolean wardIsExit(SearchWradEntity entity) {
        SQLiteDatabase mReadableDb = mPlayRecordDbHelp.getReadableDatabase();
        Cursor cursor = mReadableDb.query(TABLE_SEARCH, null, null, null, null, null, null);
        if (null != cursor) {
            cursor.moveToFirst();
            if (cursor.getCount() == 0) {
                return false;
            }
            int wardIndex0 = cursor.getColumnIndex(COLUMN_WORD);
            String resultWard0 = cursor.getString(wardIndex0);
            if (entity.getWard().equals(resultWard0)) {
                cursor.close();
                updateTime(entity);
                return true;
            }
            while (cursor.moveToNext()) {
                int wardIndex = cursor.getColumnIndex(COLUMN_WORD);
                String resultWard = cursor.getString(wardIndex);
                if (entity.getWard().equals(resultWard)) {
                    cursor.close();
                    updateTime(entity);
                    return true;
                }
            }

        }
        mReadableDb.close();
        return false;
    }

    public void updateTime(SearchWradEntity entity) {
        SQLiteDatabase writableDatabase = mPlayRecordDbHelp.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TIME, entity.getTime());
        writableDatabase.update(TABLE_SEARCH, values, COLUMN_WORD + "=?", new String[]{entity.getWard()});
        writableDatabase.close();
    }

    public void clearAllWard() {
        SQLiteDatabase mWritableDb = mPlayRecordDbHelp.getWritableDatabase();
        mWritableDb.delete(TABLE_SEARCH, null, null);
        mWritableDb.close();
    }

    public List<SearchWradEntity> getAllWard() {
        SQLiteDatabase readableDatabase = mPlayRecordDbHelp.getReadableDatabase();
        List<SearchWradEntity> entities = new ArrayList<>();
        Cursor cursor = readableDatabase.query(TABLE_SEARCH, null, null, null, null, null, "time desc", null);
        if (null != cursor) {
            while (cursor.moveToNext()) {
                int wardIndex = cursor.getColumnIndex(COLUMN_WORD);
                int timeIndex = cursor.getColumnIndex(COLUMN_TIME);
                String ward = cursor.getString(wardIndex);
                long time = cursor.getLong(timeIndex);
                SearchWradEntity entity = new SearchWradEntity(ward, time);
                entities.add(entity);
            }
        }
        return entities;
    }

}
