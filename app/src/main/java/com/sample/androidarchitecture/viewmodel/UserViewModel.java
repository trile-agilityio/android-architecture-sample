package com.sample.androidarchitecture.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.VisibleForTesting;

import com.sample.androidarchitecture.data.local.entity.Repo;
import com.sample.androidarchitecture.data.local.entity.User;
import com.sample.androidarchitecture.service.repository.RepoRepository;
import com.sample.androidarchitecture.service.repository.UserRepository;
import com.sample.androidarchitecture.util.common.AbsentLiveData;
import com.sample.androidarchitecture.util.common.Resource;

import java.util.List;
import java.util.Objects;

public class UserViewModel extends ViewModel {

    @VisibleForTesting
    private MutableLiveData<String> login = new MutableLiveData<>();
    private LiveData<Resource<List<Repo>>> repositories;
    private LiveData<Resource<User>> user;

    public UserViewModel(UserRepository userRepository, RepoRepository responsitory) {

        user = Transformations.switchMap(login, login -> {

            if (login == null) {
                return AbsentLiveData.create();
            } else {
                return userRepository.loadUsers(login);
            }
        });

        // Repositories

    }

    public void setLogin(String login) {
        if (Objects.equals(this.login, login)) {
            return;
        }

        this.login.setValue(login);
    }

    public LiveData<Resource<List<Repo>>> getRepositories() {
        return repositories;
    }

    public LiveData<Resource<User>> getUser() {
        return user;
    }
}
