package com.imaneb.findme.ui.register;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.imaneb.findme.data.repository.AuthRepository;
import com.imaneb.findme.utils.StateResource;

import javax.inject.Inject;

import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RegisterViewModel extends ViewModel {

    private static final String TAG = "RegisterViewModel";

    AuthRepository authRepository;

    private CompositeDisposable disposable = new CompositeDisposable();
    private MediatorLiveData<StateResource> onRegister = new MediatorLiveData<>();

    @Inject
    public RegisterViewModel(AuthRepository authRepository) {
        Log.d(TAG, "RegisterViewModel: working...");
        this.authRepository = authRepository;

        if(authRepository.getCurrentUser() == null){
            Log.d(TAG, "RegisterViewModel: No user loged in");
        }
    }

    public void register(String email, String password, String name, String imei, String birthday, String gender){
        authRepository.register(email, password, name , imei, birthday, gender)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                        onRegister.setValue(StateResource.loading());
                    }

                    @Override
                    public void onComplete() {
                        onRegister.setValue(StateResource.success());
                    }

                    @Override
                    public void onError(Throwable e) {
                        onRegister.setValue(StateResource.error(e.getMessage()));
                    }
                });
    }

    public LiveData<StateResource> observeRegister(){
        return onRegister;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
