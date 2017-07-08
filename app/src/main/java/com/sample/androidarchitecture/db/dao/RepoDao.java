package com.sample.androidarchitecture.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.util.SparseIntArray;

import com.sample.androidarchitecture.db.entity.Contributor;
import com.sample.androidarchitecture.db.entity.Repo;
import com.sample.androidarchitecture.db.entity.RepoSearchResult;

import java.util.Collections;
import java.util.List;

@Dao
public abstract class RepoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(Repo... repos);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertContributor(List<Contributor> contributors);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertRepos(List<Repo> repositories);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long createRepoIfNotExists(Repo repo);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(RepoSearchResult searchResult);

    @Query("SELECT * FROM repo WHERE owner_login = :login AND name = :name")
    public abstract LiveData<Repo> loadRepo(String login, String name);

    @Query("SELECT login, avatarUrl, contributions FROM contributor "
            + "WHERE repoName = :name AND repoOwner = :owner "
            + "ORDER BY contributions DESC")
    public abstract LiveData<List<Contributor>> loadContributors(String owner, String name);

    @Query("SELECT * FROM Repo "
            + "WHERE owner_login = :owner "
            + "ORDER BY stars DESC")
    public abstract LiveData<List<Repo>> loadRepos(String owner);

    @Query("SELECT * FROM RepoSearchResult WHERE query = :query")
    public abstract LiveData<RepoSearchResult> search(String query);

    @Query("SELECT * FROM Repo WHERE id in (:repoIds)")
    protected abstract LiveData<List<Repo>> loadById(List<Integer> repoIds);

    @Query("SELECT * FROM RepoSearchResult WHERE query = :query")
    public abstract RepoSearchResult findSearchResult(String query);

    /**
     * Load list repositories with sort.
     *
     * @param repoIds
     * @return
     */
    public LiveData<List<Repo>> loadOrdered(List<Integer> repoIds) {

        SparseIntArray order = new SparseIntArray();
        int index = 0;

        for (Integer repoId : repoIds) {
            order.put(repoId, index++);
        }

        return Transformations.map(loadById(repoIds), repositories -> {
            Collections.sort(repositories, (r1, r2) -> {
                int pos1 = order.get(r1.id);
                int pos2 = order.get(r2.id);
                return pos1 - pos2;
            });

            return repositories;
        });
    }
}