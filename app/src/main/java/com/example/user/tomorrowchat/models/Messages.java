package com.example.user.tomorrowchat.models;

/**
 * Created by USER on 2017-10-23.
 */

public class Messages {
    public String message;

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public boolean seen;
    public String time;
    public String type;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String from;

    public Messages(){};
    public Messages(String from){this.from=from;}
    public Messages(String message, String type , String time , boolean seen){

        this.message =message;
        this.seen = seen;
        this.time = time;
        this.type = type;
    }

    public String getMessage() {return message;}

    public void setMessage(String message) {
        this.message = message;
    }




    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }



}
