package com.imaneb.findme.data.model;

public class User {

    private String email;
    private String displayName;
    private String image;
    private String status;
    private String telephone;
    private String imei;
    private String birthday;
    private String gender;
    private boolean online;



    public User(String email, String displayName, String image, String status, String telephone, String imei, String birthday, String gender, boolean online) {
        this.email = email;
        this.displayName = displayName;
        this.image = image;
        this.status = status;
        this.telephone = telephone;
        this.imei = imei;
        this.birthday = birthday;
        this.gender = gender;
        this.online = online;
    }
    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public User() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
