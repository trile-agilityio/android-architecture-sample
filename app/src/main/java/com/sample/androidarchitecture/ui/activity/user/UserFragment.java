package com.sample.androidarchitecture.ui.activity.user;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingComponent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sample.androidarchitecture.R;
import com.sample.androidarchitecture.databinding.FragmentDataBindingComponent;
import com.sample.androidarchitecture.databinding.UserFragmentBinding;
import com.sample.androidarchitecture.ui.adapter.RepoListAdapter;
import com.sample.androidarchitecture.ui.common.NavigationController;
import com.sample.androidarchitecture.util.common.AutoClearedValue;

import javax.inject.Inject;

public class UserFragment extends LifecycleFragment {

    private static final String LOGIN_KEY = "login";

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    NavigationController navigationController;

    private DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);
    private UserViewModel userViewModel;
    private AutoClearedValue<UserFragmentBinding> binding;
    private AutoClearedValue<RepoListAdapter> repoAdapter;

    public static UserFragment create(String login) {
        UserFragment userFragment = new UserFragment();
        Bundle bundle = new Bundle();
        bundle.putString(LOGIN_KEY, login);
        userFragment.setArguments(bundle);
        return userFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // data binding
        UserFragmentBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.user_fragment,
                container, false, dataBindingComponent);

        // set function retryCallback
        dataBinding.setRetryCallback(() -> userViewModel.retry());

        // binding
        binding = new AutoClearedValue<>(this, dataBinding);

        return dataBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // User view model
        userViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(UserViewModel.class);
        userViewModel.setLogin(getArguments().getString(LOGIN_KEY));

        // observe
        userViewModel.getUser().observe(this, userResource -> {
            // update UI
            binding.get().setUser(userResource == null ? null : userResource.data);
            binding.get().setUserResource(userResource);
            binding.get().executePendingBindings();
        });

        // Repo adapter
        RepoListAdapter adapter = new RepoListAdapter(dataBindingComponent, false,
                repo -> navigationController.navigationToUser(repo.owner.login));
        binding.get().repoList.setAdapter(adapter);
        this.repoAdapter = new AutoClearedValue<>(this, adapter);

        initRepoList();
    }

    /**
     * initRepoList
     */
    private void initRepoList() {
        userViewModel.getRepositories().observe(this, repos -> {
            // no null checks for adapter.get() since LiveData guarantees that we'll not receive
            // the event if fragment is now show.
            if (repos == null) {
                repoAdapter.get().replace(null);
            } else {
                repoAdapter.get().replace(repos.data);
            }
        });
    }

}