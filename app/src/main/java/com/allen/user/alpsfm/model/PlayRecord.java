package com.allen.user.alpsfm.model;

import com.ximalaya.ting.android.opensdk.model.track.Track;

/**
 * Created by user on 17-3-24.
 */

public class PlayRecord {

    private int type;//播放记录的类型 2 track 3 radio
    private String url = "aa";//图片资源的Url
    private String albumName = "aa";//专辑或广播的名字
    private String trackName = "aa";//声音或者Radio节目的名字
    private long albumId;//专辑的id
    private long dataId;//track 或者program 的id

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public long getAblumId() {
        return albumId;
    }

    public void setBlumId(long albumId) {
        this.albumId = albumId;
    }

    public long getDataId() {
        return dataId;
    }

    public void setDataId(long dataId) {
        this.dataId = dataId;
    }
    public PlayRecord(){}
    public PlayRecord(int type, String url, String albumName, String trackName, long albumId, long dataId) {
        this.type = type;
        this.url = url;
        this.albumName = albumName;
        this.trackName = trackName;
        this.albumId = albumId;
        this.dataId = dataId;
    }


    @Override
    public String toString() {
        return "{type = " + this.type + "url = " + this.url + "albumName = "
                + this.albumName + "trackName = " + this.trackName + "blumId = "
                + this.albumId + "dataId = " + this.dataId + "}";
    }
}
