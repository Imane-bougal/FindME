package com.imaneb.findme.ui.splash;

import android.content.Intent;
import android.os.Bundle;

import com.imaneb.findme.HomeActivity;
import com.imaneb.findme.data.repository.AuthRepository;
import com.imaneb.findme.ui.main.MainActivity;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

public class SplashActivity extends DaggerAppCompatActivity {

    @Inject
    AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Change Activity
        if(authRepository.getCurrentUser() != null){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }else {
            startActivity(new Intent(this, com.imaneb.findme.HomeActivity.class));
            finish();
        }

    }
}
