package com.imaneb.findme.di.main.notification;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.imaneb.findme.adapter.RequestRecyclerAdapter;
import com.imaneb.findme.data.model.Request;
import com.imaneb.findme.data.repository.DatabaseRepository;
import com.imaneb.findme.utils.GetTimeAgo;

import dagger.Module;
import dagger.Provides;

@Module
public class NotificationModule {

    @Provides
    static FirestoreRecyclerOptions<Request> provideRequestOptions(DatabaseRepository databaseRepository){
        return databaseRepository.getRequestList();
    }

    @Provides
    static RequestRecyclerAdapter provideRequestAdapter(FirestoreRecyclerOptions<Request> options, DatabaseRepository databaseRepository, GetTimeAgo getTimeAgo){
        return new RequestRecyclerAdapter(options,databaseRepository,getTimeAgo);
    }
}
