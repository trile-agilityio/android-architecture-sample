package com.sample.androidarchitecture.service.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.sample.androidarchitecture.db.dao.RepoDao;
import com.sample.androidarchitecture.db.database.GithubDb;
import com.sample.androidarchitecture.db.entity.Contributor;
import com.sample.androidarchitecture.db.entity.Repo;
import com.sample.androidarchitecture.db.entity.RepoSearchResult;
import com.sample.androidarchitecture.networking.api.IGithubApi;
import com.sample.androidarchitecture.networking.base.ResponseApi;
import com.sample.androidarchitecture.networking.response.RepoSearchResponse;
import com.sample.androidarchitecture.util.common.AbsentLiveData;
import com.sample.androidarchitecture.util.common.AppExecutors;
import com.sample.androidarchitecture.util.common.RateLimiter;
import com.sample.androidarchitecture.util.common.Resource;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import timber.log.Timber;

public class RepoRepository {

    private GithubDb githubDb;
    private RepoDao repoDao;
    private IGithubApi githubApi;
    private AppExecutors appExecutors;

    private RateLimiter<String> repoListRateLimit = new RateLimiter<>(10, TimeUnit.MINUTES);

    @Inject
    public RepoRepository(GithubDb githubDb, RepoDao repoDao, IGithubApi githubApi,
                          AppExecutors appExecutors) {

        this.githubDb = githubDb;
        this.repoDao = repoDao;
        this.githubApi = githubApi;
        this.appExecutors = appExecutors;
    }

    /**
     * Load list repositories data.
     *
     * @param owner The {@link String}
     * @return
     */
    public LiveData<Resource<List<Repo>>> loadRepos(String owner) {

        return new NetworkBoundResource<List<Repo>, List<Repo>>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull List<Repo> repos) {
                repoDao.insertRepos(repos);
            }

            @Override
            protected boolean shouldFetchData(@Nullable List<Repo> data) {
                return data == null || data.isEmpty() || repoListRateLimit.shouldFetch(owner);
            }

            @NonNull
            @Override
            protected LiveData<ResponseApi<List<Repo>>> createCall() {
                return githubApi.getRepositories(owner);
            }

            @NonNull
            @Override
            protected LiveData<List<Repo>> loadFromDb() {
                return repoDao.loadRepos(owner);
            }

            @Override
            protected void onFetchFailed() {
                repoListRateLimit.reset(owner);
            }
        }.asLiveData();
    }

    /**
     * Load one Repository data.
     *
     * @param owner The {@link String}
     * @param name  The {@link String}
     * @return
     */
    public LiveData<Resource<Repo>> loadRepo(String owner, String name) {

        return new NetworkBoundResource<Repo, Repo>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull Repo repo) {
                repoDao.insert(repo);
            }

            @Override
            protected boolean shouldFetchData(@Nullable Repo data) {
                return data == null;
            }

            @NonNull
            @Override
            protected LiveData<ResponseApi<Repo>> createCall() {
                return githubApi.getRepository(owner, name);
            }

            @NonNull
            @Override
            protected LiveData<Repo> loadFromDb() {
                return repoDao.loadRepo(owner, name);
            }
        }.asLiveData();
    }

    /**
     * Load list Contributors data.
     *
     * @param owner The {@link String}
     * @param name  The {@link String}
     * @return
     */
    public LiveData<Resource<List<Contributor>>> loadContributors(String owner, String name) {

        return new NetworkBoundResource<List<Contributor>, List<Contributor>>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull List<Contributor> contributors) {

                for (Contributor contributor : contributors) {
                    contributor.setRepoName(name);
                    contributor.setRepoOwner(owner);
                }

                githubDb.beginTransaction();

                try {

                    repoDao.createRepoIfNotExists(new Repo(Repo.UNKNOWN_ID,
                            name, owner + "/" + name, "",
                            0, new Repo.Owner(owner, null)));

                    repoDao.insertContributor(contributors);
                    githubDb.setTransactionSuccessful();

                } finally {
                    githubDb.endTransaction();
                }

                Timber.d("saved contributors to database");
            }

            @Override
            protected boolean shouldFetchData(@Nullable List<Contributor> data) {
                return data == null || data.isEmpty();
            }

            @NonNull
            @Override
            protected LiveData<ResponseApi<List<Contributor>>> createCall() {
                return githubApi.getContributors(owner, name);
            }

            @NonNull
            @Override
            protected LiveData<List<Contributor>> loadFromDb() {
                return repoDao.loadContributors(owner, name);
            }
        }.asLiveData();
    }

    /**
     * Search next page
     *
     * @param query The search query
     * @return
     */
    public LiveData<Resource<Boolean>> searchNextPage(String query) {
        FetchNextSearchPageTask fetchNextSearchPageTask =
                new FetchNextSearchPageTask(query, githubApi, githubDb);

        appExecutors.networkIO().execute(fetchNextSearchPageTask);
        return fetchNextSearchPageTask.getLiveData();
    }

    /**
     * Search repos
     *
     * @param query The search query
     * @return
     */
    public LiveData<Resource<List<Repo>>> search(String query) {

        return new NetworkBoundResource<List<Repo>, RepoSearchResponse>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull RepoSearchResponse item) {

                List<Integer> repoIds = item.getRepoIds();
                RepoSearchResult repoSearchResult = new RepoSearchResult(
                        query, repoIds, item.getTotal(), item.getNextPage());

                githubDb.beginTransaction();
                try {
                    repoDao.insertRepos(item.getItems());
                    repoDao.insert(repoSearchResult);
                    githubDb.setTransactionSuccessful();
                } finally {
                    githubDb.endTransaction();
                }
            }

            @Override
            protected boolean shouldFetchData(@Nullable List<Repo> data) {
                return data == null;
            }

            @NonNull
            @Override
            protected LiveData<ResponseApi<RepoSearchResponse>> createCall() {
                return githubApi.searchRepositories(query);
            }

            @NonNull
            @Override
            protected LiveData<List<Repo>> loadFromDb() {
                return Transformations.switchMap(repoDao.search(query), searchData -> {
                    if (searchData == null) {
                        return AbsentLiveData.create();
                    } else {
                        return repoDao.loadOrdered(searchData.repoIds);
                    }
                });
            }
        }.asLiveData();
    }
}