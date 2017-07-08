package com.sample.androidarchitecture.util;

import com.sample.androidarchitecture.db.entity.Contributor;
import com.sample.androidarchitecture.db.entity.Repo;
import com.sample.androidarchitecture.db.entity.User;

import java.util.ArrayList;
import java.util.List;

public class TestUtil {

    /**
     * Create User
     *
     * @param login
     * @return
     */
    public static User createUser(String login) {
        return new User(login, null,
                login + " name", null, null, null);
    }

    /**
     * createRepos
     *
     * @param count
     * @param owner
     * @param name
     * @param description
     * @return
     */
    public static List<Repo> createRepos(int count, String owner, String name, String description) {
        List<Repo> repos = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            repos.add(createRepo(owner + i, name + i, description + i));
        }

        return repos;
     }

    /**
     * createRepo
     *
     * @param owner
     * @param name
     * @param description
     * @return
     */
    public static Repo createRepo(String owner, String name, String description) {
        return createRepo(Repo.UNKNOWN_ID, owner, name, description);
    }

    /**
     * createRepo
     *
     * @param id
     * @param owner
     * @param name
     * @param description
     * @return
     */
    public static Repo createRepo(int id, String owner, String name, String description) {
        return new Repo(id, name, owner + "/" + name,
                description, new Repo.Owner(owner, null), 3);
    }

    /**
     * createContributor
     *
     * @param repo
     * @param login
     * @param contributions
     * @return
     */
    public static Contributor createContributor(Repo repo, String login, int contributions) {
        Contributor contributor = new Contributor(login, contributions, null);
        contributor.setRepoName(repo.name);
        contributor.setRepoOwner(repo.owner.login);
        return contributor;
    }
}