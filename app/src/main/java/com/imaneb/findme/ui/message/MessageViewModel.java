package com.imaneb.findme.ui.message;

import android.content.Intent;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.imaneb.findme.MapsActivity;
import com.imaneb.findme.data.model.Message;
import com.imaneb.findme.data.model.User;
import com.imaneb.findme.data.repository.DatabaseRepository;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.imaneb.findme.ui.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.CompletableObserver;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MessageViewModel extends ViewModel {

    private static final String TAG = "MessageViewModel";
    private String PROFILE_UID;
    private DatabaseRepository databaseRepository;
    private CompositeDisposable disposable = new CompositeDisposable();
    private MediatorLiveData<List<Message>> getAllMessage = new MediatorLiveData<>();
    private MediatorLiveData<Message> getNewMessage = new MediatorLiveData<>();
    private MediatorLiveData<User> friendInfo = new MediatorLiveData<>();
    private int counter = 0;
    private List<Message> messageList = new ArrayList<>();

    @Inject
    public MessageViewModel(DatabaseRepository databaseRepository) {
        Log.d(TAG, "MessageViewModel: working...");
        this.databaseRepository = databaseRepository;
    }

    public void setProfileUid(String profileUid) {
        PROFILE_UID = profileUid;
        loadMessage();
        loadFriendInfo();
    }

    private void loadFriendInfo() {
        databaseRepository.getUserinfo(PROFILE_UID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toObservable()
                .subscribe(new Observer<DocumentSnapshot>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onNext(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        friendInfo.setValue(user);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void loadMessage() {
        databaseRepository.getMessageList(PROFILE_UID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toObservable()
                .subscribe(new Observer<QuerySnapshot>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onNext(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots!=null){
                            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                                if (dc.getType() == DocumentChange.Type.ADDED) {
                                    if(counter ==0){
                                        Message message = dc.getDocument().toObject(Message.class);
                                        messageList.add(message);
                                    }
                                    else {
                                        Message message = dc.getDocument().toObject(Message.class);
                                        getNewMessage.setValue(message);
                                    }
                                }
                            }
                            if(counter == 0){
                                getAllMessage.setValue(messageList);
                                counter++;
                            }

                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public String LocateFriend(){
       return PROFILE_UID;
    }

    public void sendMessage(Message message){
        databaseRepository.sendMessage(PROFILE_UID,message)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: message sent success");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: "+e);
                    }
                });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if(disposable!=null){
            disposable.dispose();
        }
    }

    public LiveData<List<Message>> getAllMessage(){
        return getAllMessage;
    }
    public LiveData<Message> getNewMessage(){
        return getNewMessage;
    }

    public LiveData<User> getFriendInfo(){
        return friendInfo;
    }
}
