package com.sample.androidarchitecture.ui.activity.search;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.sample.androidarchitecture.db.entity.Repo;
import com.sample.androidarchitecture.service.repository.RepoRepository;
import com.sample.androidarchitecture.util.common.AbsentLiveData;
import com.sample.androidarchitecture.util.common.Resource;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.inject.Inject;

public class SearchViewModel extends ViewModel {

    private MutableLiveData<String> query = new MutableLiveData<>();
    private LiveData<Resource<List<Repo>>> results;
    private NextPageHandler nextPageHandler;

    @Inject
    public SearchViewModel(RepoRepository repoRepository) {
        nextPageHandler = new NextPageHandler(repoRepository);

        results = Transformations.switchMap(query, search -> {
            if (search == null || search.trim().length() == 0) {
                return AbsentLiveData.create();
            } else {
                return repoRepository.search(search);
            }
        });
    }

    public LiveData<Resource<List<Repo>>> getResults() {
        return results;
    }

    public void setQuery(@NonNull String originalInput) {
        String input = originalInput.toLowerCase(Locale.getDefault()).trim();

        if (Objects.equals(input, query.getValue())) {
            return;
        }

        nextPageHandler.reset();
        query.setValue(input);
    }

    LiveData<NextPageHandler.LoadMoreState> getLoadMoreStatus() {
        return nextPageHandler.getLoadMoreState();
    }

    void loadNextPage() {
        String value = query.getValue();

        if (value == null || value.trim().length() == 0) {
            return;
        }

        nextPageHandler.queryNextPage(value);
    }

    void refresh() {
        if (query.getValue() != null) {
            query.setValue(query.getValue());
        }
    }
}