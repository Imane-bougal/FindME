package com.imaneb.findme.di;

import android.app.Application;


import com.imaneb.findme.BaseActivity;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {
        AndroidSupportInjectionModule.class,
        ActivityBuildersModule.class,
        AppModule.class,
        ViewModelFactoryModule.class
})
public interface AppComponent extends AndroidInjector<BaseActivity> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(Application application);

        AppComponent build();
    }
}
