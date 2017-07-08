package com.sample.androidarchitecture.ui.activity.user;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.VisibleForTesting;

import com.sample.androidarchitecture.db.entity.Repo;
import com.sample.androidarchitecture.db.entity.User;
import com.sample.androidarchitecture.service.repository.RepoRepository;
import com.sample.androidarchitecture.service.repository.UserRepository;
import com.sample.androidarchitecture.util.common.AbsentLiveData;
import com.sample.androidarchitecture.util.common.Resource;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

public class UserViewModel extends ViewModel {

    @VisibleForTesting
    private MutableLiveData<String> login = new MutableLiveData<>();

    private LiveData<Resource<List<Repo>>> repositories;
    private LiveData<Resource<User>> user;

    @Inject
    public UserViewModel(UserRepository userRepository, RepoRepository repoRepository) {

        // load users
        user = Transformations.switchMap(login, login -> {
           if (login == null) {
               return AbsentLiveData.create();
           } else {
               return userRepository.loadUsers(login);
           }
        });

        // load repositories
        repositories = Transformations.switchMap(login, login -> {
            if (login == null) {
                return AbsentLiveData.create();
            } else {
                return repoRepository.loadRepos(login);
            }
        });
    }

    /**
     * setLogin
     *
     * @param login The {@link String}
     */
    public void setLogin(String login) {
        if (Objects.equals(this.login.getValue(), login)) {
            return;
        }

        this.login.setValue(login);
    }

    /**
     * getRepositories
     *
     * @return List {@link RepoRepository}
     */
    public LiveData<Resource<List<Repo>>> getRepositories() {
        return repositories;
    }

    /**
     * getUser
     *
     * @return The {@link User}
     */
    public LiveData<Resource<User>> getUser() {
        return user;
    }

    /**
     * retry
     */
    public void retry() {
        if (this.login.getValue() != null) {
            this.login.setValue(this.login.getValue());
        }
    }
}