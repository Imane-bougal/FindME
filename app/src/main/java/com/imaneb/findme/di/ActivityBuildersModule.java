package com.imaneb.findme.di;


import com.imaneb.findme.di.account.AccountModule;
import com.imaneb.findme.di.account.AccountViewModelModule;
import com.imaneb.findme.di.login.LoginViewModelModule;
import com.imaneb.findme.di.main.MainFragmentBuildersModule;
import com.imaneb.findme.di.main.MainModule;
import com.imaneb.findme.di.main.MainViewModelModule;
import com.imaneb.findme.di.message.MessageModule;
import com.imaneb.findme.di.message.MessageViewModelModule;
import com.imaneb.findme.di.profile.ProfileViewModelModule;
import com.imaneb.findme.di.register.RegisterViewModelModule;
import com.imaneb.findme.ui.account.AccountActivity;
import com.imaneb.findme.ui.login.LoginActivity;
import com.imaneb.findme.ui.main.MainActivity;
import com.imaneb.findme.ui.message.MessageActivity;
import com.imaneb.findme.ui.profile.ProfileActivity;
import com.imaneb.findme.ui.register.RegisterActivity;
import com.imaneb.findme.ui.splash.SplashActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuildersModule {

    @ContributesAndroidInjector(modules = {
            RegisterViewModelModule.class
    })
    abstract RegisterActivity contributeAuthActivity();

    @ContributesAndroidInjector(modules = {
            LoginViewModelModule.class
    })
    abstract LoginActivity contributeLoginActivity();

    @ContributesAndroidInjector
    abstract SplashActivity contributeSplashActivity();

    @ContributesAndroidInjector(modules = {
            MainFragmentBuildersModule.class,
            MainViewModelModule.class,
            MainModule.class
    })
    abstract MainActivity contributeMainActivity();

    @ContributesAndroidInjector(modules = {
            AccountViewModelModule.class,
            AccountModule.class
    })
    abstract AccountActivity contributeAccountActivity();

    @ContributesAndroidInjector(modules ={
            ProfileViewModelModule.class
    })
    abstract ProfileActivity contributeProfileActivity();

    @ContributesAndroidInjector(modules = {
            MessageViewModelModule.class,
            MessageModule.class
    })
    abstract MessageActivity contributeMessageActivity();
}
