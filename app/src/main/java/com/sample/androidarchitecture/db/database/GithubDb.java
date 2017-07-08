package com.sample.androidarchitecture.db.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.sample.androidarchitecture.db.dao.RepoDao;
import com.sample.androidarchitecture.db.dao.UserDao;
import com.sample.androidarchitecture.db.entity.Contributor;
import com.sample.androidarchitecture.db.entity.Repo;
import com.sample.androidarchitecture.db.entity.RepoSearchResult;
import com.sample.androidarchitecture.db.entity.User;

@Database(entities = {User.class, Repo.class, Contributor.class,
        RepoSearchResult.class}, version = 1)
public abstract class GithubDb extends RoomDatabase {

    abstract public UserDao userDao();

    abstract public RepoDao repoDao();
}
