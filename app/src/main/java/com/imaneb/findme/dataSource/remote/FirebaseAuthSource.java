package com.imaneb.findme.dataSource.remote;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imaneb.findme.utils.Constants;

import java.util.Arrays;
import java.util.HashMap;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;

public class FirebaseAuthSource {

    private static final String TAG = "FirebaseAuthSource";

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    @Inject
    public FirebaseAuthSource(FirebaseAuth firebaseAuth, FirebaseFirestore firebaseFirestore) {
        this.firebaseAuth = firebaseAuth;
        this.firebaseFirestore = firebaseFirestore;
    }

    //get current user uid
    public String getCurrentUid() {
        return firebaseAuth.getCurrentUser().getUid();
    }

    //get current user
    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    //create new account
    public Completable register(final String email, final String password, final String name, final String imei, final String birthday, final String gender) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(final CompletableEmitter emitter) throws Exception {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                emitter.onError(e);
                            }
                        })
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                //create new user in firebase
                                HashMap<String, Object> map = new HashMap<>();

                                map.put("email",email);
                                map.put("displayName",name);
                                map.put("image","default");
                                map.put("status","default");
                                map.put("online",true);
                                map.put("gender", gender);
                                map.put("imei" , imei);
                                map.put("birthday", birthday);
                                map.put("telephone", "default");
                                map.put("friends", Arrays.asList(""));
                                map.put("positions",Arrays.asList(""));

                                HashMap<String, Object> constraintData = new HashMap<>();
                                constraintData.put("constraint_minAge",18);
                                constraintData.put("constraint_gender","All");


                                map.put("constraints",constraintData);


                                firebaseFirestore.collection(Constants.USERS_NODE)
                                        .document(getCurrentUid()).set(map)
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                emitter.onError(e);
                                            }
                                        })
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                emitter.onComplete();
                                            }
                                        });
                            }
                        });
            }
        });
    }

    //login
    public Completable login(final String email, final String password){
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(final CompletableEmitter emitter) throws Exception {
                firebaseAuth.signInWithEmailAndPassword(email,password)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                emitter.onError(e);
                            }
                        })
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                emitter.onComplete();
                            }
                        });
            }
        });
    }
    //logout
    public void logout(){
        firebaseAuth.signOut();
    }
}
