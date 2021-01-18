package com.imaneb.findme.data.repository;

import com.google.firebase.auth.FirebaseUser;
import com.imaneb.findme.dataSource.remote.FirebaseAuthSource;

import io.reactivex.Completable;

public class AuthRepository {

    FirebaseAuthSource firebaseAuthSource;

    public AuthRepository(FirebaseAuthSource firebaseAuthSource) {
        this.firebaseAuthSource = firebaseAuthSource;
    }

    public FirebaseUser getCurrentUser() {
        return firebaseAuthSource.getCurrentUser();
    }

    public String getCurrentUid() {
        return firebaseAuthSource.getCurrentUid();
    }

    public Completable register(String email, String password, String name, String imei, String birthday, String gender) {
        return firebaseAuthSource.register(email, password, name, imei, birthday, gender);
    }

    public Completable login(String email, String password){
        return firebaseAuthSource.login(email,password);
    }

    public void signOut(){
        firebaseAuthSource.logout();
    }
}
