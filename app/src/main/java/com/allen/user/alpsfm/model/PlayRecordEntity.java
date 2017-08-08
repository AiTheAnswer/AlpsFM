package com.allen.user.alpsfm.model;

/**
 * Created by user on 17-3-26.
 */

public class PlayRecordEntity {
    private int type;//播放记录的类型 2 track 3 radio
    private long albumId;//专辑的id
    private long dataId;//track 或者program 的id

    public PlayRecordEntity(int type, long albumId, long dataId) {
        this.type = type;
        this.albumId = albumId;
        this.dataId = dataId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public long getDataId() {
        return dataId;
    }

    public void setDataId(long dataId) {
        this.dataId = dataId;
    }

    public String toString() {
        return "{tye = " + type + "albumId = " + albumId + "dataId = " + dataId + "}";
    }
}
