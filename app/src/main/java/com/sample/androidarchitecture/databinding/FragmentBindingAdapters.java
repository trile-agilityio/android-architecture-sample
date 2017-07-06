package com.sample.androidarchitecture.databinding;

import android.databinding.BindingAdapter;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class FragmentBindingAdapters {

    final Fragment fragment;

    public FragmentBindingAdapters(Fragment fragment) {
        this.fragment = fragment;
    }

    @BindingAdapter("imageUrl")
    public void bindImage(ImageView imageView, String url) {
        Glide.with(fragment)
                .load(url)
                .centerCrop()
                .into(imageView);
    }

}
