package com.sample.androidarchitecture.ui.activity.repo;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.sample.androidarchitecture.data.local.entity.Contributor;
import com.sample.androidarchitecture.data.local.entity.Repo;
import com.sample.androidarchitecture.service.repository.RepoRepository;
import com.sample.androidarchitecture.util.common.AbsentLiveData;
import com.sample.androidarchitecture.util.common.Resource;

import java.util.List;
import java.util.Objects;

public class RepoViewModel extends ViewModel {

    private MutableLiveData<RepoId> repoId;
    private LiveData<Resource<Repo>> repo;
    private LiveData<Resource<List<Contributor>>> contributors;

    public RepoViewModel(RepoRepository repoRepository) {
        this.repoId = new MutableLiveData<>();

        // load Repository
        repo = Transformations.switchMap(repoId, input -> {
           if (input.isEmpty()) {
               return AbsentLiveData.create();
           }

           return repoRepository.loadRepo(input.owner, input.name);
        });

        // load Contributors
        contributors = Transformations.switchMap(repoId, input -> {
            if (input.isEmpty()) {
                return AbsentLiveData.create();
            }

            return repoRepository.loadContributors(input.owner, input.name);
        });
    }

    public LiveData<Resource<Repo>> getRepo() {
        return repo;
    }

    public LiveData<Resource<List<Contributor>>> getContributors() {
        return contributors;
    }

    public void retry() {
        RepoId current = repoId.getValue();
        if (current != null && !current.isEmpty()) {
            repoId.setValue(current);
        }
    }

    public void setId(String owner, String name) {
        RepoId update = new RepoId(owner, name);
        if (Objects.equals(repoId.getValue(), update)) {
            return;
        }
        repoId.setValue(update);
    }

    static class RepoId {

        public final String owner;
        public final String name;

        RepoId(String owner, String name) {
            this.owner = owner == null ? null : owner.trim();
            this.name = name == null ? null : name.trim();
        }

        boolean isEmpty() {
            return owner == null || name == null || owner.length() == 0 || name.length() == 0;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            RepoId repoId = (RepoId) o;

            if (owner != null ? !owner.equals(repoId.owner) : repoId.owner != null) {
                return false;
            }

            return name != null ? name.equals(repoId.name) : repoId.name == null;
        }

        @Override
        public int hashCode() {
            int result = owner != null ? owner.hashCode() : 0;
            result = 31 * result + (name != null ? name.hashCode() : 0);
            return result;
        }
    }
}
