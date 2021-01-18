package com.imaneb.findme.ui.login;

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

public class LoginViewModel extends ViewModel {

    private static final String TAG = "LoginViewModel";
    private AuthRepository authRepository;
    private MediatorLiveData<StateResource> onLogin = new MediatorLiveData<>();
    private CompositeDisposable disposable = new CompositeDisposable();

    @Inject
    public LoginViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void login(String email, String password){
        authRepository.login(email,password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                        onLogin.setValue(StateResource.loading());
                    }

                    @Override
                    public void onComplete() {
                        onLogin.setValue(StateResource.success());
                    }

                    @Override
                    public void onError(Throwable e) {
                        onLogin.setValue(StateResource.error(e.getMessage()));
                    }
                });
    }

    public LiveData<StateResource> observeLogin(){
        return onLogin;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
