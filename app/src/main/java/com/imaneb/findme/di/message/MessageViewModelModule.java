package com.imaneb.findme.di.message;

import androidx.lifecycle.ViewModel;


import com.imaneb.findme.di.ViewModelKey;
import com.imaneb.findme.ui.message.MessageViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class MessageViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MessageViewModel.class)
    public abstract ViewModel bindMessageViewModel(MessageViewModel viewModel);
}
