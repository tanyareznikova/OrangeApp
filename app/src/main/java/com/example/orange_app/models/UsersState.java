package com.example.orange_app.models;

public class UsersState {

    public String date, state, time;

    public UsersState() {
    }

    public UsersState(String date, String state, String time) {
        this.date = date;
        this.state = state;
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}//UsersState
