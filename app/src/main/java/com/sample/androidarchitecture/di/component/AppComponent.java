package com.sample.androidarchitecture.di.component;

import android.app.Application;

import com.sample.androidarchitecture.application.GithubApp;
import com.sample.androidarchitecture.di.module.AppModule;
import com.sample.androidarchitecture.di.module.MainActivityModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;

@Singleton
@Component(modules = {
        AndroidInjectionModule.class,
        AppModule.class,
        MainActivityModule.class
})
public interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance Builder application(Application application);

        AppComponent build();
    }

    void inject(GithubApp githubApp);
}
