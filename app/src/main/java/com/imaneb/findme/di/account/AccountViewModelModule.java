package com.imaneb.findme.di.account;

import androidx.lifecycle.ViewModel;


import com.imaneb.findme.di.ViewModelKey;
import com.imaneb.findme.ui.account.AccountViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class AccountViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AccountViewModel.class)
    public abstract ViewModel binViewModel(AccountViewModel viewModel);
}
