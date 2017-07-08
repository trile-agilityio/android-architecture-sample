package com.sample.androidarchitecture.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.sample.androidarchitecture.db.entity.User;

@Dao
public interface UserDao {

    @Query("SELECT * FROM user WHERE login = :login")
    LiveData<User> getUserByLogin(String login);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);

}
