package com.imaneb.findme.data.repository;

import android.graphics.Bitmap;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.imaneb.findme.data.model.Message;
import com.imaneb.findme.data.model.Request;
import com.imaneb.findme.data.model.User;
import com.imaneb.findme.dataSource.remote.FirebaseDataSource;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class DatabaseRepository {

    FirebaseDataSource firebaseDataSource;

    @Inject
    public DatabaseRepository(FirebaseDataSource firebaseDataSource) {
        this.firebaseDataSource = firebaseDataSource;
    }

    public FirestoreRecyclerOptions<User> getUserList() {
        return firebaseDataSource.getUserList();
    }

    public FirestoreRecyclerOptions<Request> getRequestList(){
        return firebaseDataSource.getRequestList();
    }

    public FirestoreRecyclerOptions<Request> getFriendList(){
        return firebaseDataSource.getFriendList();
    }

    public Flowable<DocumentSnapshot> getUserinfo(String uid) {
        return firebaseDataSource.getUserInfo(uid);
    }

    public Completable updateStatus(String status) {
        return firebaseDataSource.updateStatus(status);
    }

    public Completable updateConstraints(int min_age,String gender) {
        return firebaseDataSource.updateConstraints(min_age,gender);
    }

    public Completable updateFriendList(String imei,String req_uid,String current_imei) {
        return firebaseDataSource.updateFriendList(imei,req_uid,current_imei);
    }

    public Completable updateDisplayImage(Bitmap bitmap) {
        return firebaseDataSource.updateDisplayImage(bitmap);
    }

    public Completable sendFriendRequest(String req_uid) {
        return firebaseDataSource.sendFriendRequest(req_uid);
    }

    public Flowable<DocumentSnapshot> getRequestState(String uid) {
        return firebaseDataSource.requestState(uid);
    }

    public Completable cancelFriendRequest(String req_uid) {
        return firebaseDataSource.cancelFriendRequest(req_uid);
    }

    public Completable acceptFriendRequest(String req_uid) {
        return firebaseDataSource.acceptFriendRequest(req_uid);
    }

    public Completable sendMessage(String friendUid, Message message){
        return firebaseDataSource.sendMessage(friendUid,message);
    }

    public FirestoreRecyclerOptions<Message> getChatList(String uid){
        return firebaseDataSource.getChatList(uid);
    }

    public Single<User> getFriendInfo(String uid){
        return firebaseDataSource.getFriendInfo(uid);
    }

    public Flowable<QuerySnapshot> getMessageList(String uid){
        return firebaseDataSource.getMessageList(uid);
    }
}
