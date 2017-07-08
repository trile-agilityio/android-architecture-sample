package com.sample.androidarchitecture.di.module;

import com.sample.androidarchitecture.ui.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class MainActivityModule {

    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract MainActivity contributerMainActivity();
}
