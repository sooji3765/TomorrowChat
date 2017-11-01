package com.example.user.tomorrowchat.models;

/**
 * Created by USER on 2017-10-10.
 */

public class Blog {
    private String title;
    private String desc;
    private String image;

    public Blog(){

    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
