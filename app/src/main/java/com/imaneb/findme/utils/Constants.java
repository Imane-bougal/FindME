package com.imaneb.findme.utils;

import java.util.List;

public class Constants {

    public static final String USERS_NODE = "users";
    public static final String POSITIONS_NODE = "users";
    public static final String PROFILE_IMAGE_NODE = "profile_image";
    public static final String FRIEND_REQUEST_NODE = "friend_request";
    public static final String FRIEND_REQUEST_TYPE = "request_type";
    public static final String REQUEST_NODE = "request";
    public static final String IMAGE_TYPE = "image";
    public static final String TEXT_TYPE = "text";
    public static final String MESSAGE_NODE = "messages";
    public static String constraint_gender;
    public static int constraint_age;
    public static List<String> friendsImei;


    public static List<String> getFriendsImei() {
        return friendsImei;
    }
    public static void addFriendImei(String imei){
        Constants.friendsImei.add(imei);
    }
    public static void removeFriendImei(String imei){
        for(int i =0; i<friendsImei.size();i++){
            if (friendsImei.get(i).equals(imei)){
                friendsImei.remove(i);
            }
        }
    }

    public static void setFriendsImei(List<String> friendsImei) {
        Constants.friendsImei = friendsImei;
    }

    public static String getConstraint_gender() {
        if (constraint_gender==null){
            constraint_gender = "All";
        }
        return constraint_gender;
    }

    public static void setConstraint_gender(String constraint_gender) {
        Constants.constraint_gender = constraint_gender;
    }

    public static int getConstraint_age() {
        return constraint_age;
    }

    public static void setConstraint_age(int constraint_age) {
        Constants.constraint_age = constraint_age;
    }







}
