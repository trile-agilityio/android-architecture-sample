package com.sample.androidarchitecture.di.module;

import com.sample.androidarchitecture.ui.activity.repo.RepoFragment;
import com.sample.androidarchitecture.ui.activity.search.SearchFragment;
import com.sample.androidarchitecture.ui.activity.user.UserFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract RepoFragment contributeRepoFragment();

    @ContributesAndroidInjector
    abstract UserFragment contributeUserFragment();

    @ContributesAndroidInjector
    abstract SearchFragment contributeSearchFragment();
}
