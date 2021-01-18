package com.imaneb.findme.di.register;

import androidx.lifecycle.ViewModel;


import com.imaneb.findme.di.ViewModelKey;
import com.imaneb.findme.ui.register.RegisterViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class RegisterViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(RegisterViewModel.class)
    public abstract ViewModel binViewModel(RegisterViewModel viewModel);
}
