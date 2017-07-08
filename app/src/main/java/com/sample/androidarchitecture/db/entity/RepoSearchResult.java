package com.sample.androidarchitecture.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.Nullable;

import com.sample.androidarchitecture.util.common.GithubTypeConverters;

import java.util.List;

@Entity(primaryKeys = {"query"})
@TypeConverters(GithubTypeConverters.class)
public class RepoSearchResult {

    public String query;
    public List<Integer> repoIds;
    public int totalCount;

    @Nullable
    public Integer next;

    public RepoSearchResult(String query, List<Integer> repoIds, int totalCount, Integer next) {
        this.query = query;
        this.repoIds = repoIds;
        this.totalCount = totalCount;
        this.next = next;
    }

}