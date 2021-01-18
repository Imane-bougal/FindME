package com.imaneb.findme.di.login;

import androidx.lifecycle.ViewModel;


import com.imaneb.findme.di.ViewModelKey;
import com.imaneb.findme.ui.login.LoginViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class LoginViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel.class)
    public abstract ViewModel bindViewModel(LoginViewModel viewModel);

}
