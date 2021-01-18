package com.imaneb.findme.di.main;

import com.imaneb.findme.di.main.chat.ChatModule;
import com.imaneb.findme.di.main.notification.NotificationModule;
import com.imaneb.findme.di.main.users.UserModule;
import com.imaneb.findme.ui.main.connections.chat.ChatFragment;
import com.imaneb.findme.ui.main.connections.notification.NotificationFragment;
import com.imaneb.findme.ui.main.connections.users.UsersFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class MainFragmentBuildersModule {

    @ContributesAndroidInjector(modules = {
            UserModule.class
    })
    abstract UsersFragment contributeUsersFragment();

    @ContributesAndroidInjector(modules = {
            NotificationModule.class
    })
    abstract NotificationFragment contributeNotificationFragment();

    @ContributesAndroidInjector(modules = {
            ChatModule.class
    })
    abstract ChatFragment contributeChatFragment();
}
