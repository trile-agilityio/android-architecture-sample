package com.sample.androidarchitecture.di.module;

import com.sample.androidarchitecture.networking.api.IGithubApi;
import com.sample.androidarchitecture.util.common.LiveDataCallAdapterFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = ViewModelModule.class)
public class AppModule {

    @Singleton
    @Provides
    IGithubApi provideIGithubApi() {

        return new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build()
                .create(IGithubApi.class);
    }
}
