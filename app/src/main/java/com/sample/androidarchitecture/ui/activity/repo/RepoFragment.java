package com.sample.androidarchitecture.ui.activity.repo;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingComponent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sample.androidarchitecture.R;
import com.sample.androidarchitecture.db.entity.Repo;
import com.sample.androidarchitecture.databinding.FragmentDataBindingComponent;
import com.sample.androidarchitecture.databinding.RepoFragmentBinding;
import com.sample.androidarchitecture.ui.adapter.ContributorAdapter;
import com.sample.androidarchitecture.ui.common.NavigationController;
import com.sample.androidarchitecture.util.common.AutoClearedValue;
import com.sample.androidarchitecture.util.common.Resource;

import java.util.Collections;

import javax.inject.Inject;

public class RepoFragment extends Fragment implements LifecycleRegistryOwner {

    private static final String REPO_OWNER_KEY = "repo_owner";
    private static final String REPO_NAME_KEY = "repo_name";

    private LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    NavigationController navigationController;

    DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);
    private RepoViewModel repoViewModel;
    private AutoClearedValue<RepoFragmentBinding> binding;
    private AutoClearedValue<ContributorAdapter> contributorAdapter;

    @Override
    public LifecycleRegistry getLifecycle() {
        return lifecycleRegistry;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        RepoFragmentBinding dataBinding = DataBindingUtil.inflate(
                inflater, R.layout.repo_fragment, container, false);
        dataBinding.setRetryCallback(() -> repoViewModel.retry());
        binding = new AutoClearedValue<>(this, dataBinding);

        return dataBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // View model
        repoViewModel = ViewModelProviders.of(this, viewModelFactory).get(RepoViewModel.class);
        Bundle args = getArguments();
        if (args != null && args.containsKey(REPO_NAME_KEY)
                && args.containsKey(REPO_OWNER_KEY)) {
            repoViewModel.setId(args.getString(REPO_OWNER_KEY), args.getString(REPO_NAME_KEY));

        } else {
            repoViewModel.setId(null, null);
        }

        // Get Repositories
        LiveData<Resource<Repo>> repo = repoViewModel.getRepo();
        repo.observe(this, resource -> {
            // update UI
            binding.get().setRepo(resource == null ? null : resource.data);
            binding.get().setRepoResource(resource);
            binding.get().executePendingBindings();
        });

        // Contributors adapter
        ContributorAdapter adapter = new ContributorAdapter(dataBindingComponent, contributor ->
                navigationController.navigationToUser(contributor.getLogin()));

        this.contributorAdapter = new AutoClearedValue<>(this, adapter);
        binding.get().contributorList.setAdapter(adapter);
        initContributorList(repoViewModel);
    }

    /**
     * Create fragment
     *
     * @param owner
     * @param name
     * @return
     */
    public static RepoFragment create(String owner, String name) {
        RepoFragment repoFragment = new RepoFragment();
        Bundle args = new Bundle();
        args.putString(REPO_OWNER_KEY, owner);
        args.putString(REPO_NAME_KEY, name);
        repoFragment.setArguments(args);
        return repoFragment;
    }

    /**
     * Initialize Contributors list.
     *
     * @param viewModel
     */
    private void initContributorList(RepoViewModel viewModel) {

        viewModel.getContributors().observe(this, listResource -> {
            // don't need any null checks here for the adapter since LiveData guarantees that
            // it won't call us if fragment is stopped or not started.
            if (listResource != null && listResource.data != null) {
                contributorAdapter.get().replace(listResource.data);
            } else {
                contributorAdapter.get().replace(Collections.emptyList());
            }
        });
    }
}
