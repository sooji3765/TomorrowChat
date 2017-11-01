package com.example.user.tomorrowchat.models;

/**
 * Created by USER on 2017-10-24.
 */

public class Chat {
    public String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Chat(){};

    public Chat(String date){
        this.date = date;
    }
}
