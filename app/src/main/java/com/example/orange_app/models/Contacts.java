package com.example.orange_app.models;

import java.util.ArrayList;

public class Contacts extends ArrayList<Contacts> {

    public String name, status, image;

    public Boolean selected = false;

    public Contacts(){

    }//Contacts()

    public Contacts(String name, String status, String image) {
        this.name = name;
        this.status = status;
        this.image = image;
    }//Contacts(...)

    public String getName() {
        return name;
    }//getName

    public void setName(String name) {
        this.name = name;
    }//setName

    public String getStatus() {
        return status;
    }//getStatus

    public void setStatus(String status) {
        this.status = status;
    }//setStatus

    public String getImage() {
        return image;
    }//getImage

    public void setImage(String image) {
        this.image = image;
    }//setImage

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

}//Contacts
