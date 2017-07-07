package com.sample.androidarchitecture.ui.activity.search;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.sample.androidarchitecture.service.repository.RepoRepository;
import com.sample.androidarchitecture.util.common.Resource;

import java.util.Objects;

@VisibleForTesting
public class NextPageHandler implements Observer<Resource<Boolean>> {

    @Nullable
    private LiveData<Resource<Boolean>> nextPageLiveData;

    private MutableLiveData<LoadMoreState> loadMoreState = new MutableLiveData<>();
    private String query;
    private RepoRepository repoRepository;

    @VisibleForTesting
    private boolean hasMore;

    @VisibleForTesting
    NextPageHandler(RepoRepository repository) {
        this.repoRepository = repository;
        reset();
    }

    public void queryNextPage(String query) {

        if (Objects.equals(this.query, query)) {
            return;
        }

        unregister();
        this.query = query;
        nextPageLiveData = repoRepository.searchNextPage(query);
        loadMoreState.setValue(new LoadMoreState(true, null));

        if (nextPageLiveData != null) {
            nextPageLiveData.observeForever(this);
        }
    }

    @Override
    public void onChanged(@Nullable Resource<Boolean> result) {
        if (result  == null) {
            reset();
        } else {
            switch (result.status) {

                case SUCCESS:
                    hasMore = Boolean.TRUE.equals(result.data);
                    unregister();
                    loadMoreState.setValue(new LoadMoreState(false, null));
                    break;

                case ERROR:
                    hasMore = true;
                    unregister();
                    loadMoreState.setValue(new LoadMoreState(false, result.message));
                    break;

                default:
                    break;
            }
        }
    }

    private void unregister() {
        if (nextPageLiveData != null) {
            nextPageLiveData.removeObserver(this);
            nextPageLiveData = null;
            if (hasMore) {
                query = null;
            }
        }
    }

    public void reset() {
        unregister();
        hasMore = true;
        loadMoreState.setValue(new LoadMoreState(false, null));
    }

    MutableLiveData<LoadMoreState> getLoadMoreState() {
        return loadMoreState;
    }

    static class LoadMoreState {
        private final boolean running;
        private final String errorMessage;
        private boolean handledError = false;

        LoadMoreState(boolean running, String errorMessage) {
            this.running = running;
            this.errorMessage = errorMessage;
        }

        boolean isRunning() {
            return running;
        }

        String getErrorMessage() {
            return errorMessage;
        }

        String getErrorMessageIfNotHandled() {
            if (handledError) {
                return null;
            }
            handledError = true;
            return errorMessage;
        }
    }
}
