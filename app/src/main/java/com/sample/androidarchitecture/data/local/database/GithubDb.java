package com.sample.androidarchitecture.data.local.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.sample.androidarchitecture.data.local.dao.RepoDao;
import com.sample.androidarchitecture.data.local.dao.UserDao;
import com.sample.androidarchitecture.data.local.entity.Contributor;
import com.sample.androidarchitecture.data.local.entity.Repo;
import com.sample.androidarchitecture.data.local.entity.RepoSearchResult;
import com.sample.androidarchitecture.data.local.entity.User;

@Database(entities = {User.class, Repo.class, Contributor.class,
        RepoSearchResult.class}, version = 1)
public abstract class GithubDb extends RoomDatabase {

    abstract public UserDao userDao();

    abstract public RepoDao repoDao();
}
