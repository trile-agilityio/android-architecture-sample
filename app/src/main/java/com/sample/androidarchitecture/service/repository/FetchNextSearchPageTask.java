package com.sample.androidarchitecture.service.repository;

import android.arch.lifecycle.MutableLiveData;

import com.sample.androidarchitecture.data.local.database.GithubDb;
import com.sample.androidarchitecture.data.local.entity.RepoSearchResult;
import com.sample.androidarchitecture.networking.api.IGithubApi;
import com.sample.androidarchitecture.networking.base.ResponseApi;
import com.sample.androidarchitecture.networking.response.RepoSearchResponse;
import com.sample.androidarchitecture.util.common.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

/**
 * A task that reads the search result in the database and fetches the next page, if it has one.
 */
public class FetchNextSearchPageTask implements Runnable {

    private MutableLiveData<Resource<Boolean>> liveData = new MutableLiveData<>();
    private String query;
    private IGithubApi githubApi;
    private GithubDb githubDb;

    public MutableLiveData<Resource<Boolean>> getLiveData() {
        return liveData;
    }

    public FetchNextSearchPageTask(String query, IGithubApi githubApi, GithubDb githubDb) {
        this.query = query;
        this.githubApi = githubApi;
        this.githubDb = githubDb;
    }

    @Override
    public void run() {

        RepoSearchResult current = githubDb.repoDao().findSearchResult(query);

        if (current == null) {
            liveData.postValue(null);
            return;
        }

        final Integer nextPage = current.next;

        if (nextPage == null) {
            liveData.postValue(Resource.success(false));
            return;
        }

        try {
            // Search Repositories from server
            Response<RepoSearchResponse> response =
                    githubApi.searchRepositories(query, nextPage).execute();

            ResponseApi<RepoSearchResponse> apiResponse = new ResponseApi<>(response);

            if (apiResponse.isSuccessful()) {

                // we merge all repo ids to 1 list so that it is easier to fetch the result list
                List<Integer> ids = new ArrayList<>();
                ids.addAll(current.repoIds);

                RepoSearchResult merged = new RepoSearchResult(query, ids,
                        apiResponse.body != null ? apiResponse.body.getTotal() : 0,
                        apiResponse.getNextPage());

                try {
                    githubDb.beginTransaction();
                    githubDb.repoDao().insert(merged);
                    if (apiResponse.body != null) {
                        githubDb.repoDao().insertRepos(apiResponse.body.getItems());
                    }
                    githubDb.setTransactionSuccessful();

                } finally {
                    githubDb.endTransaction();
                }

                liveData.postValue(Resource.success(apiResponse.getNextPage() != null));

            } else {
                liveData.postValue(Resource.error(apiResponse.message, true));
            }

        } catch (IOException e) {
            liveData.postValue(Resource.error(e.getMessage(), true));
        }
    }
}