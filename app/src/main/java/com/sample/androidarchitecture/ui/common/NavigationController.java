package com.sample.androidarchitecture.ui.common;

import android.support.v4.app.FragmentManager;

import com.sample.androidarchitecture.R;
import com.sample.androidarchitecture.ui.MainActivity;
import com.sample.androidarchitecture.ui.activity.repo.RepoFragment;
import com.sample.androidarchitecture.ui.activity.search.SearchFragment;

import javax.inject.Inject;

public class NavigationController {

    private final int containerId;
    private final FragmentManager fragmentManager;

    @Inject
    public NavigationController(MainActivity mainActivity) {
        this.containerId = R.id.container;
        this.fragmentManager = mainActivity.getSupportFragmentManager();
    }

    /**
     * Go to Search fragment.
     */
    public void navigationToSearch() {
        SearchFragment searchFragment = new SearchFragment();
        fragmentManager.beginTransaction()
                .replace(containerId, searchFragment)
                .commitAllowingStateLoss();
    }

    /**
     * Go to Repo fragment.
     */
    public void navigationToRepo(String owner, String name) {
        RepoFragment fragment = RepoFragment.create(owner, name);
        String tag = "repo" + "/" + owner + "/" + name;
        fragmentManager.beginTransaction()
                .replace(containerId, fragment, tag)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    /**
     * Go to User fragment.
     *
     * @param login
     */
    public void navigationToUser(String login) {
        // TODO: goto user fragment
    }

}