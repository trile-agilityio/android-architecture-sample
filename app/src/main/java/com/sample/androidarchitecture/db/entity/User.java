package com.sample.androidarchitecture.db.entity;

import android.arch.persistence.room.Entity;

import com.google.gson.annotations.SerializedName;

@Entity(primaryKeys = "id")
public class User {

    @SerializedName("id")
    public String id;

    @SerializedName("login")
    public String login;

    @SerializedName("avatar_url")
    public String avatarUrl;

    @SerializedName("name")
    public String name;

    @SerializedName("company")
    public String company;

    @SerializedName("repos_url")
    public String reposUrl;

    @SerializedName("blog")
    public String blog;

    public User(String login, String avatarUrl, String name, String company,
                String reposUrl, String blog) {
        this.login = login;
        this.avatarUrl = avatarUrl;
        this.name = name;
        this.company = company;
        this.reposUrl = reposUrl;
        this.blog = blog;
    }
}