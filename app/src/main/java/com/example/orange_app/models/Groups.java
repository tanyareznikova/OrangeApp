package com.example.orange_app.models;

public class Groups {

    private String admin, groupName, image;

    private Members members;

    public Groups() {
    }

    public Groups(String admin, String groupName, String image, Members members) {
        this.admin = admin;
        this.groupName = groupName;
        this.image = image;
        this.members = members;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Members getMembers() {
        return members;
    }

    public void setMembers(Members members) {
        this.members = members;
    }

}//Groups
