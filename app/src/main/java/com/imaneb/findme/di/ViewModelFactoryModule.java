package com.imaneb.findme.di;

import androidx.lifecycle.ViewModelProvider;


import com.imaneb.findme.viewModels.ViewModelProviderFactory;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class ViewModelFactoryModule {

    @Binds
    public abstract ViewModelProvider.Factory binViewModelFactory(ViewModelProviderFactory factory);
}
