package com.example.orange_app.notifications;

public class Sender {

    private Data data;
    private String to;

    public Sender() {
    }//Sender()

    public Sender(Data data, String to) {
        this.data = data;
        this.to = to;
    }//Sender(..)

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

}//Sender
