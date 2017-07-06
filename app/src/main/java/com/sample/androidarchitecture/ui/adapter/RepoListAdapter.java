package com.sample.androidarchitecture.ui.adapter;

import android.databinding.DataBindingComponent;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.sample.androidarchitecture.R;
import com.sample.androidarchitecture.data.local.entity.Repo;
import com.sample.androidarchitecture.databinding.RepoItemBinding;
import com.sample.androidarchitecture.ui.common.DataBoundListAdapter;

import java.util.Objects;

public class RepoListAdapter extends DataBoundListAdapter<Repo, RepoItemBinding> {

    private final DataBindingComponent dataBindingComponent;
    private final RepoClickCallback callback;
    private final boolean isShowFullName;

    public RepoListAdapter(DataBindingComponent dataBindingComponent,
                           RepoClickCallback repoClickCallback, boolean isShowFullName) {
        this.dataBindingComponent = dataBindingComponent;
        this.callback = repoClickCallback;
        this.isShowFullName = isShowFullName;
    }

    @Override
    protected RepoItemBinding createBinding(ViewGroup parent) {

        // binding
        RepoItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.repo_item, parent, false, dataBindingComponent);

        // show full name
        binding.setShowFullName(isShowFullName);

        // item clicked
        binding.getRoot().setOnClickListener(v -> {
            Repo repo = binding.getRepo();

            if (repo != null && callback != null) {
                callback.onClick(repo);
            }

        });

        return binding;
    }

    @Override
    protected void bind(RepoItemBinding binding, Repo repo) {
        binding.setRepo(repo);
    }

    @Override
    protected boolean areContentsTheSame(Repo oldItem, Repo newItem) {
        return Objects.equals(oldItem.owner, newItem.owner)
                && Objects.equals(oldItem.name, newItem.name);
    }

    @Override
    protected boolean areItemsTheSame(Repo oldItem, Repo newItem) {
        return Objects.equals(oldItem.description, newItem.description)
                && Objects.equals(oldItem.stars, newItem.stars);
    }

    public interface RepoClickCallback {
        void onClick(Repo repo);
    }

}