package com.sample.androidarchitecture.ui.common;

import android.support.v4.app.FragmentManager;

import com.sample.androidarchitecture.R;
import com.sample.androidarchitecture.ui.MainActivity;

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
        // TODO: goto search fragment
    }

    /**
     * Go to Repo fragment.
     */
    public void navigationToRepo() {
        // TODO: goto repo fragment
    }

    /**
     * Go to User fragment.
     * @param login
     */
    public void navigationToUser(String login) {
        // TODO: goto user fragment
    }

}