package com.example.orange_app.models;

import java.util.ArrayList;

public class Users {

    public String name, status, image, uid, device_token;
    public UsersState userState;

    public Users() {

    }//Users

    public Users(String name, String status, String image, String uid, String device_token, UsersState userState) {
        this.name = name;
        this.status = status;
        this.image = image;
        this.uid = uid;
        this.device_token = device_token;
        this.userState = userState;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public UsersState getUserState() {
        return userState;
    }

    public void setUserState(UsersState userState) {
        this.userState = userState;
    }
}//Users
