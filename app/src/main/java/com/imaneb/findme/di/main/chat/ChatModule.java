package com.imaneb.findme.di.main.chat;

import com.bumptech.glide.RequestManager;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.imaneb.findme.adapter.ChatRecyclerAdapter;
import com.imaneb.findme.data.model.Request;
import com.imaneb.findme.data.repository.DatabaseRepository;

import dagger.Module;
import dagger.Provides;

@Module
public class ChatModule {

    @Provides
    static FirestoreRecyclerOptions<Request> provideRequestOptions(DatabaseRepository databaseRepository){
        return databaseRepository.getFriendList();
    }

    @Provides
    static ChatRecyclerAdapter provideRecyclerAdapter(FirestoreRecyclerOptions<Request> options, DatabaseRepository databaseRepository, RequestManager requestManager){
        return new ChatRecyclerAdapter(options,databaseRepository,requestManager);
    }
}
