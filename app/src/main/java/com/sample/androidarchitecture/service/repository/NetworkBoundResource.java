package com.sample.androidarchitecture.service.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.sample.androidarchitecture.networking.base.ResponseApi;
import com.sample.androidarchitecture.util.common.AppExecutors;
import com.sample.androidarchitecture.util.common.Resource;

/**
 * A generic class that can provide a resource backed by both the sqlite database and the network.
 *
 * @param <ResultType>
 * @param <RequestType>
 */
public abstract class NetworkBoundResource<ResultType, RequestType> {

    private final AppExecutors appExecutors;

    private MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();

    @MainThread
    protected NetworkBoundResource(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;

        result.setValue(Resource.loading(null));
        LiveData<ResultType> dbSource = loadFromDb();

        result.addSource(dbSource, data -> {
            result.removeSource(dbSource);

            if (shouldFetchData(data)) {
                fetchDataFromNetWork(dbSource);
            } else {
                result.addSource(dbSource, newData ->
                        result.setValue(Resource.success(newData)));
            }
        });
    }

    /**
     * Fetch data from network.
     *
     * @param dbSource The {@link LiveData<ResultType>}
     */
    protected void fetchDataFromNetWork(final LiveData<ResultType> dbSource) {
        LiveData<ResponseApi<RequestType>> responseApi = createCall();

        // re-attach dbSource as a new source, it will dispatch its latest value quickly
        result.addSource(dbSource, newData -> result.setValue(Resource.loading(newData)));

        result.addSource(responseApi, response -> {

            result.removeSource(responseApi);
            result.removeSource(dbSource);

            if (response != null && response.isSuccessful()) {
                appExecutors.diskIO().execute(() -> {
                    saveCallResult(processResponse(response));
                    appExecutors.mainThread().execute(() ->
                            // specially request a new live data,
                            // otherwise we will get immediately last cached value,
                            // which may not be updated with latest results received from network.
                            result.addSource(loadFromDb(), newData ->
                                    result.setValue(Resource.success(newData))));
                });
            } else {
                onFetchFailed();
                result.addSource(dbSource, newData ->
                        result.setValue(Resource.error(response.message, newData)));
            }

        });
    }

    protected void onFetchFailed() {
    }

    public LiveData<Resource<ResultType>> asLiveData() {
        return result;
    }

    @WorkerThread
    protected abstract void saveCallResult(@NonNull RequestType requestType);

    @MainThread
    protected abstract boolean shouldFetchData(@Nullable ResultType data);

    @WorkerThread
    protected RequestType processResponse(ResponseApi<RequestType> response) {
        return response.body;
    }

    @NonNull
    @MainThread
    protected abstract LiveData<ResponseApi<RequestType>> createCall();

    @NonNull
    @MainThread
    protected abstract LiveData<ResultType> loadFromDb();
}