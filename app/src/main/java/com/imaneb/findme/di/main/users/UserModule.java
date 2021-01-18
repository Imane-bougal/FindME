package com.imaneb.findme.di.main.users;

import android.app.Application;

import com.bumptech.glide.RequestManager;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.imaneb.findme.adapter.UsersRecyclerAdapter;
import com.imaneb.findme.data.model.User;
import com.imaneb.findme.data.repository.DatabaseRepository;

import dagger.Module;
import dagger.Provides;

@Module
public class UserModule {


    @Provides
    static FirestoreRecyclerOptions<User> provideOption(DatabaseRepository databaseRepository){
        return databaseRepository.getUserList();
    }

    @Provides
    static UsersRecyclerAdapter provideAdapter(FirestoreRecyclerOptions<User> options, RequestManager requestManager, Application application){
        return new UsersRecyclerAdapter(options,requestManager);
    }
}
