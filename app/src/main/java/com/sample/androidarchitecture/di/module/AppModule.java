package com.sample.androidarchitecture.di.module;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.sample.androidarchitecture.db.dao.RepoDao;
import com.sample.androidarchitecture.db.dao.UserDao;
import com.sample.androidarchitecture.db.database.GithubDb;
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

    @Singleton
    @Provides
    GithubDb provideGithubDb(Application application) {
        return Room.databaseBuilder(application, GithubDb.class, "github.db").build();
    }

    @Singleton
    @Provides
    UserDao provideUserDao(GithubDb db) {
        return db.userDao();
    }

    @Singleton
    @Provides
    RepoDao providerRepoDao(GithubDb db) {
        return db.repoDao();
    }
}
