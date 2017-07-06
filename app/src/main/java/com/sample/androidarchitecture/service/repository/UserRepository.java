package com.sample.androidarchitecture.service.repository;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.sample.androidarchitecture.data.local.dao.UserDao;
import com.sample.androidarchitecture.data.local.entity.User;
import com.sample.androidarchitecture.networking.api.IGithubApi;
import com.sample.androidarchitecture.networking.base.ResponseApi;
import com.sample.androidarchitecture.util.common.AppExecutors;
import com.sample.androidarchitecture.util.common.Resource;

import javax.inject.Inject;

public class UserRepository {

    private UserDao userDao;
    private IGithubApi githubApi;
    private AppExecutors appExecutors;

    @Inject
    public UserRepository(UserDao userDao, IGithubApi githubApi, AppExecutors appExecutors) {
        this.userDao = userDao;
        this.githubApi = githubApi;
        this.appExecutors = appExecutors;
    }

    /**
     * Load Users data.
     *
     * @param login
     * @return
     */
    public LiveData<Resource<User>> loadUsers(String login) {

        return new NetworkBoundResource<User, User>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull User user) {
                userDao.insert(user);
            }

            @Override
            protected boolean shouldFetchData(@Nullable User data) {
                return data == null;
            }

            @NonNull
            @Override
            protected LiveData<ResponseApi<User>> createCall() {
                return githubApi.getUser(login);
            }

            @NonNull
            @Override
            protected LiveData<User> loadFromDb() {
                return userDao.getUserByLogin(login);
            }
        }.asLiveData();
    }
}
