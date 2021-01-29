package com.imaneb.findme.dataSource.remote;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MediatorLiveData;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.imaneb.findme.data.model.Message;
import com.imaneb.findme.data.model.Request;
import com.imaneb.findme.data.model.User;
import com.imaneb.findme.utils.Constants;
import com.imaneb.findme.utils.DataConverter;

import org.w3c.dom.Document;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Cancellable;
import io.reactivex.schedulers.Schedulers;

import static androidx.core.content.ContextCompat.getSystemService;

public class FirebaseDataSource {
    private static final String TAG = "FirebaseDataSource";

    private FirebaseAuthSource firebaseAuthSource;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private String currentUid;

    @Inject
    public FirebaseDataSource(FirebaseAuthSource firebaseAuthSource, FirebaseFirestore firebaseFirestore, StorageReference storageReference) {
        this.firebaseAuthSource = firebaseAuthSource;
        this.firebaseFirestore = firebaseFirestore;
        this.storageReference = storageReference;
        currentUid = firebaseAuthSource.getCurrentUid();


    }

    //fireStore users list
    private Query getUsersQuery() {
        String s = Constants.getConstraint_gender();

        if ( s =="All"){
            return firebaseFirestore.collection(Constants.USERS_NODE);
        }
        if( s == null){
            return firebaseFirestore.collection(Constants.USERS_NODE);
        }else {
            return firebaseFirestore.collection(Constants.USERS_NODE).whereEqualTo("gender", s);
        }

    }

    public FirestoreRecyclerOptions<User> getUserList() {

        return new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(getUsersQuery(), User.class)
                .build();
    }



    //fireStore request list
    private Query getRequestQuery(){
        return firebaseFirestore.collection(Constants.FRIEND_REQUEST_NODE)
                .document(currentUid)
                .collection(Constants.REQUEST_NODE)
                .whereEqualTo("requestType","received");
    }
    public FirestoreRecyclerOptions<Request> getRequestList(){
        return new FirestoreRecyclerOptions.Builder<Request>()
                .setQuery(getRequestQuery(),Request.class)
                .build();
    }


    //fireStore friend list
    private Query getFriendQuery(){
        return firebaseFirestore.collection(Constants.FRIEND_REQUEST_NODE)
                .document(currentUid)
                .collection(Constants.REQUEST_NODE)
                .whereEqualTo("requestType","friend");
    }
    public FirestoreRecyclerOptions<Request> getFriendList(){
        return new FirestoreRecyclerOptions.Builder<Request>()
                .setQuery(getFriendQuery(),Request.class)
                .build();
    }
    //get chat list
    private Query getChatListQuery(String uid){
        return firebaseFirestore.collection(Constants.MESSAGE_NODE).document(currentUid).collection(uid);
    }
    public FirestoreRecyclerOptions<Message> getChatList(String uid){
        return new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(getChatListQuery(uid),Message.class)
                .build();
    }
    //get user information
    public Flowable<DocumentSnapshot> getUserInfo(final String uid) {
        return Flowable.create(new FlowableOnSubscribe<DocumentSnapshot>() {
            @Override
            public void subscribe(final FlowableEmitter<DocumentSnapshot> emitter) throws Exception {
                DocumentReference reference = firebaseFirestore.collection(Constants.USERS_NODE).document(uid);
                final ListenerRegistration registration = reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            emitter.onError(e);
                        }
                        if (documentSnapshot != null) {
                            emitter.onNext(documentSnapshot);
                        }
                    }
                });

                emitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        registration.remove();
                    }
                });
            }
        }, BackpressureStrategy.BUFFER);
    }

    //update status
    public Completable updateStatus(final String status) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(final CompletableEmitter emitter) throws Exception {

                DocumentReference reference = firebaseFirestore.collection(Constants.USERS_NODE).document(currentUid);
                reference.update("status", status)
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

    public Completable updateConstraints(final int minAge,final  String gender) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(final CompletableEmitter emitter) throws Exception {
                HashMap<String, Object> constraintData = new HashMap<>();
                constraintData.put("constraint_minAge",minAge);
                constraintData.put("constraint_gender",gender);
                DocumentReference reference = firebaseFirestore.collection(Constants.USERS_NODE).document(currentUid);
                reference.update("constraints", constraintData).addOnFailureListener(new OnFailureListener() {
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
    // add friend to friend list
    public Completable updateFriendList(final String req_imei,final String req_uid,final String current_imei){

        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(final CompletableEmitter emitter) throws Exception {
                HashMap<String, Object> constraintData = new HashMap<>();

                DocumentReference reference = firebaseFirestore.collection(Constants.USERS_NODE).document(currentUid);
                reference.update("friends", FieldValue.arrayUnion(req_imei)).addOnFailureListener(new OnFailureListener() {
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
                DocumentReference referenceReq = firebaseFirestore.collection(Constants.USERS_NODE).document(req_uid);
                referenceReq.update("friends", FieldValue.arrayUnion(current_imei)).addOnFailureListener(new OnFailureListener() {
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

    // remove friend from friend list

    //update displayImage
    public Completable updateDisplayImage(final Bitmap bitmap) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(final CompletableEmitter emitter) throws Exception {
                final StorageReference reference = storageReference.child(Constants.PROFILE_IMAGE_NODE).child(currentUid + ".jpg");
                final DocumentReference db_reference = firebaseFirestore.collection(Constants.USERS_NODE).document(currentUid);
                reference.putBytes(DataConverter.convertImage2ByteArray(bitmap))
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                emitter.onError(e);
                            }
                        })
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                //get image download url;
                                reference.getDownloadUrl().addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        emitter.onError(e);
                                    }
                                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (!task.isSuccessful()) {
                                            emitter.onError(task.getException());
                                        } else {
                                            db_reference.update("image", task.getResult().toString())
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

                                    }
                                });
                            }
                        });
            }
        });
    }

    //Send friend request
    public Completable sendFriendRequest(final String req_uid){
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(final CompletableEmitter emitter) throws Exception {

                //get time stamp
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                WriteBatch requestBatch = firebaseFirestore.batch();
                DocumentReference receiver_reference = firebaseFirestore.collection(Constants.FRIEND_REQUEST_NODE).document(currentUid).collection(Constants.REQUEST_NODE).document(req_uid);
                DocumentReference sender_reference = firebaseFirestore.collection(Constants.FRIEND_REQUEST_NODE).document(req_uid).collection(Constants.REQUEST_NODE).document(currentUid);

                Request receiver = new Request("received", String.valueOf(timestamp.getTime()));
                Request sender = new Request("sender", String.valueOf(timestamp.getTime()));

                requestBatch.set(receiver_reference,sender);
                requestBatch.set(sender_reference,receiver);

                requestBatch.commit()
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

    //Cancel friend request
    public Completable cancelFriendRequest(final String req_uid){
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(final CompletableEmitter emitter) throws Exception {

                WriteBatch requestBatch = firebaseFirestore.batch();
                DocumentReference receiver_reference = firebaseFirestore.collection(Constants.FRIEND_REQUEST_NODE).document(currentUid).collection(Constants.REQUEST_NODE).document(req_uid);
                DocumentReference sender_reference = firebaseFirestore.collection(Constants.FRIEND_REQUEST_NODE).document(req_uid).collection(Constants.REQUEST_NODE).document(currentUid);


                requestBatch.delete(receiver_reference);
                requestBatch.delete(sender_reference);

                requestBatch.commit()
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

    //Accept friend request
    public Completable acceptFriendRequest(final String req_uid){
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(final CompletableEmitter emitter) throws Exception {
                //get time stamp
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                WriteBatch requestBatch = firebaseFirestore.batch();
                DocumentReference receiver_reference = firebaseFirestore.collection(Constants.FRIEND_REQUEST_NODE).document(currentUid).collection(Constants.REQUEST_NODE).document(req_uid);
                DocumentReference sender_reference = firebaseFirestore.collection(Constants.FRIEND_REQUEST_NODE).document(req_uid).collection(Constants.REQUEST_NODE).document(currentUid);

                HashMap<String, Object> friendMap = new HashMap<>();
                friendMap.put("requestType","friend");

                requestBatch.set(receiver_reference,friendMap);
                requestBatch.set(sender_reference,friendMap);

                requestBatch.commit()
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

    //observe request state
    public Flowable<DocumentSnapshot> requestState(final String uid){
        return Flowable.create(new FlowableOnSubscribe<DocumentSnapshot>() {
            @Override
            public void subscribe(final FlowableEmitter<DocumentSnapshot> emitter) throws Exception {
                DocumentReference receiver_reference = firebaseFirestore.collection(Constants.FRIEND_REQUEST_NODE).document(currentUid).collection(Constants.REQUEST_NODE).document(uid);
                final ListenerRegistration registration = receiver_reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if(e!=null){
                            emitter.onError(e);
                        }

                        emitter.onNext(documentSnapshot);
                    }
                });

                emitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        registration.remove();
                    }
                });
            }
        },BackpressureStrategy.BUFFER);
    }

    //Send message
    public Completable sendMessage(final String friendUid , final Message message){
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(final CompletableEmitter emitter) throws Exception {
                WriteBatch requestBatch = firebaseFirestore.batch();
                message.setSenderUid(firebaseAuthSource.getCurrentUid());
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                DocumentReference sender_reference = firebaseFirestore.collection(Constants.MESSAGE_NODE).document(currentUid).collection(friendUid).document(timestamp.toString());
                DocumentReference receiver_reference = firebaseFirestore.collection(Constants.MESSAGE_NODE).document(friendUid).collection(currentUid).document(timestamp.toString());
                requestBatch.set(receiver_reference,message);
                requestBatch.set(sender_reference,message);

                requestBatch.commit()
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

    //get user information
    public Single<User> getFriendInfo(final String uid) {

        return Single.create(new SingleOnSubscribe<User>() {
            @Override
            public void subscribe(final SingleEmitter<User> emitter) throws Exception {
                DocumentReference reference = firebaseFirestore.collection(Constants.USERS_NODE).document(uid);
                final ListenerRegistration registration = reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            emitter.onError(e);
                        }
                        if (documentSnapshot != null) {
                            User user = documentSnapshot.toObject(User.class);
                            emitter.onSuccess(user);
                        }
                    }
                });
            }
        });
    }

    //get message
    public Flowable<QuerySnapshot> getMessageList(final String uid){
        return Flowable.create(new FlowableOnSubscribe<QuerySnapshot>() {
            @Override
            public void subscribe(final FlowableEmitter<QuerySnapshot> emitter) throws Exception {
                CollectionReference reference = firebaseFirestore.collection(Constants.MESSAGE_NODE).document(currentUid).collection(uid);
                final ListenerRegistration registration = reference.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(e!=null){
                            emitter.onError(e);
                        }

                        if (queryDocumentSnapshots!=null){
                            emitter.onNext(queryDocumentSnapshots);
                        }
                    }
                });

                emitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        registration.remove();
                    }
                });
            }
        },BackpressureStrategy.BUFFER);
    }

}
