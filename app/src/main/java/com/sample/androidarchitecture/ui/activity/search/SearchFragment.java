package com.sample.androidarchitecture.ui.activity.search;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingComponent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import com.sample.androidarchitecture.R;
import com.sample.androidarchitecture.databinding.FragmentDataBindingComponent;
import com.sample.androidarchitecture.databinding.SearchFragmentBinding;
import com.sample.androidarchitecture.ui.adapter.RepoListAdapter;
import com.sample.androidarchitecture.ui.common.NavigationController;
import com.sample.androidarchitecture.util.common.AutoClearedValue;
import com.sample.androidarchitecture.util.view.ViewUtils;

import javax.inject.Inject;

import timber.log.Timber;

public class SearchFragment extends LifecycleFragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    NavigationController navigationController;

    DataBindingComponent dataBindingComponent =
            new FragmentDataBindingComponent(this);
    AutoClearedValue<SearchFragmentBinding> binding;
    AutoClearedValue<RepoListAdapter> adapter;
    SearchViewModel searchViewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        SearchFragmentBinding dataBinding = DataBindingUtil.inflate(
                inflater, R.layout.search_fragment, container, false, dataBindingComponent);
        binding = new AutoClearedValue<>(this, dataBinding);

        return dataBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // View model
        searchViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(SearchViewModel.class);

        initRecyclerView();

        // Repository adapter
        RepoListAdapter repoAdapter = new RepoListAdapter(dataBindingComponent, true,
                repo -> navigationController.navigationToRepo(repo.owner.login, repo.name));
        binding.get().repoList.setAdapter(repoAdapter);

        initSearchInputListener();

        binding.get().setCallback(() -> searchViewModel.refresh());
    }

    /**
     * initRecyclerView
     */
    private void initRecyclerView() {

        // Load next page
        binding.get().repoList.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager)
                        recyclerView.getLayoutManager();

                int lastPosition = layoutManager.findLastVisibleItemPosition();
                if (lastPosition == adapter.get().getItemCount() -1 ) {
                    searchViewModel.loadNextPage();
                }
            }
        });

        // Listener search result change
        searchViewModel.getResults().observe(this, result -> {
            Timber.d("update UI");

            binding.get().setSearchResource(result);
            binding.get().setResultCount(result == null || result.data == null
                    ? 0 : result.data.size());

            adapter.get().replace(result == null ? null : result.data);
            binding.get().executePendingBindings();
        });

        // Listener load more
        searchViewModel.getLoadMoreStatus().observe(this, loadMoreState -> {

            if (loadMoreState == null) {
                binding.get().setLoadingMore(false);
            } else {
                binding.get().setLoadingMore(loadMoreState.isRunning());
                String error = loadMoreState.getErrorMessageIfNotHandled();

                if (error != null) {
                    Snackbar.make(binding.get().loadMoreBar, error, Snackbar.LENGTH_LONG).show();
                }
            }

            binding.get().executePendingBindings();
        });
    }

    /**
     * initSearchInputListener
     */
    private void initSearchInputListener() {

        // EditorActionListener
        binding.get().edtInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                doSearch(v);
                return true;
            }
            return false;
        });

        // OnKeyListener
        binding.get().edtInput.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN)
                    && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                doSearch(v);
                return true;
            }
            return false;
        });
    }

    /**
     * Search Repositories.
     */
    private void doSearch(View v) {
        String query = binding.get().edtInput.getText().toString();

        // Dismiss keyboard
        ViewUtils.dismissKeyboard(v.getWindowToken(), getActivity());
        binding.get().setQuery(query);
        searchViewModel.setQuery(query);
    }
}