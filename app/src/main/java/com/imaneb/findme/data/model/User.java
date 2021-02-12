package com.imaneb.findme.data.model;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class User {

    private String email;
    private String displayName;
    private String image;
    private String status;
    private String telephone;
    private String imei;
    private String birthday;
    private String gender;
    private String g;
    private GeoPoint l;
    private boolean online;
    private List<String> friends;

    private HashMap<String, Object> constraints;
    private HashMap<String, Object> positions;

    public User(String email, String displayName, String image, String status, String telephone, String imei, String birthday, String gender, String g, GeoPoint l, boolean online, List<String> friends, HashMap<String, Object> constraints, HashMap<String, Object> positions) {
        this.email = email;
        this.displayName = displayName;
        this.image = image;
        this.status = status;
        this.telephone = telephone;
        this.imei = imei;
        this.birthday = birthday;
        this.gender = gender;
        this.g = g;
        this.l = l;
        this.online = online;
        this.friends = friends;
        this.constraints = constraints;
        this.positions = positions;
    }

    public String getG() {
        return g;
    }

    public void setG(String g) {
        this.g = g;
    }

    public GeoPoint getL() {
        return l;
    }

    public void setL(GeoPoint l) {
        this.l = l;
    }

    public HashMap<String, Object> getPositions() {
        return positions;
    }

    public void setPositions(HashMap<String, Object> positions) {
        this.positions = positions;
    }

    public HashMap<String, Object> getPosotion() {
        return positions;
    }

    public void setPosotion(HashMap<String, Object> position) {
        this.positions = position;
    }


    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }



    public HashMap<String, Object> getConstraints() {
        return constraints;
    }

    public void setConstraints(HashMap<String, Object> constraints) {
        this.constraints = constraints;
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
