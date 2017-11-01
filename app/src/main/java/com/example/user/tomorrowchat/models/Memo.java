package com.example.user.tomorrowchat.models;

/**
 * Created by USER on 2017-09-29.
 */

public class Memo {
    private String txt,title;
    private long createDate;
    private long updateDate;
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public String getTitle() {
        if (txt !=null){
            if (txt.indexOf("\n") >-1){
                return txt.substring(0,txt.indexOf("\n"));
            }else {
                return txt;
            }
        }
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }



}
