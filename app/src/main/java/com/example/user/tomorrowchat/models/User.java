package com.example.user.tomorrowchat.models;

// [START blog_user_class]

public class User {

    public String name;
    public String email;
    public String image;

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public String thumb_image;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String token;

    public String getImage() {
        return image;
    }


    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String email, String image, String thumb_image,String token) {
        this.name = name;
        this.email = email;
        this.image = image;
        this.thumb_image = thumb_image;
        this.token = token;

    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }




}