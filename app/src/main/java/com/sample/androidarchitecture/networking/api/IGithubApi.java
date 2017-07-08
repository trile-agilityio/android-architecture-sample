package com.sample.androidarchitecture.networking.api;

import android.arch.lifecycle.LiveData;

import com.sample.androidarchitecture.db.entity.Contributor;
import com.sample.androidarchitecture.db.entity.Repo;
import com.sample.androidarchitecture.db.entity.User;
import com.sample.androidarchitecture.networking.base.ResponseApi;
import com.sample.androidarchitecture.networking.response.RepoSearchResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IGithubApi {

    @GET("users/{login}")
    LiveData<ResponseApi<User>> getUser(@Path("login") String login);

    @GET("users/{login}/repos")
    LiveData<ResponseApi<List<Repo>>> getRepositories(@Path("login") String login);

    @GET("repos/{owner}/{name}")
    LiveData<ResponseApi<Repo>> getRepository(@Path("owner") String owner, @Path("name") String name);

    @GET("repos/{owner}/{name}/contributors")
    LiveData<ResponseApi<List<Contributor>>> getContributors(@Path("owner") String owner, @Path("name") String name);

    @GET("search/repositories")
    LiveData<ResponseApi<RepoSearchResponse>> searchRepositories(@Query("q") String query);

    @GET("search/repositories")
    Call<RepoSearchResponse> searchRepositories(@Query("q") String query, @Query("page") int page);

}
