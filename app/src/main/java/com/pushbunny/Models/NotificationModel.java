package com.pushbunny.Models;

public class NotificationModel {

    private String id, title, message, time, image, otherData;

    public NotificationModel(String id, String title, String message, String time, String image, String otherData) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.time = time;
        this.image = image;
        this.otherData = otherData;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getOtherData() {
        return otherData;
    }

    public void setOtherData(String otherData) {
        this.otherData = otherData;
    }
}
