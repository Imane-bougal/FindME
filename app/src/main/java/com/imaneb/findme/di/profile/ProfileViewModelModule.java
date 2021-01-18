package com.imaneb.findme.di.profile;

import androidx.lifecycle.ViewModel;

import com.imaneb.findme.di.ViewModelKey;
import com.imaneb.findme.ui.profile.ProfileViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ProfileViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel.class)
    public abstract ViewModel bindViewModel(ProfileViewModel profileViewModel);
}
