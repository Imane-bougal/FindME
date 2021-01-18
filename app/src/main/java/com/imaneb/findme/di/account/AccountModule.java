package com.imaneb.findme.di.account;


import com.imaneb.findme.utils.InputDialog;

import dagger.Module;
import dagger.Provides;

@Module
public class AccountModule {

    @Provides
    static InputDialog provideDialog(){
        return new InputDialog();
    }
}
