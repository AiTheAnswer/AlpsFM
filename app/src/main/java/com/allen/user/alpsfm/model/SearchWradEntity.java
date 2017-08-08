package com.allen.user.alpsfm.model;

/**
 * Created by user on 17-3-29.
 */

public class SearchWradEntity {
    private String ward;
    private long time;

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public SearchWradEntity(String ward, long time) {
        this.ward = ward;
        this.time = time;
    }
}
