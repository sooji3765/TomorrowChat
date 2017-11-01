package com.example.user.tomorrowchat.models;

/**
 * Created by USER on 2017-10-24.
 */

public class Request {
    public String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Request(){};

    public Request(String date){
        this.date = date;
    }
}
