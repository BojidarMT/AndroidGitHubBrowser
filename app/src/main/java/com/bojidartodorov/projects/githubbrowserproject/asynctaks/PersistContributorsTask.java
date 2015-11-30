package com.bojidartodorov.projects.githubbrowserproject.asynctaks;

import android.content.Context;
import android.os.AsyncTask;

import com.bojidartodorov.projects.githubbrowserproject.model.Contributor;
import com.bojidartodorov.projects.githubbrowserproject.model.Follower;
import com.bojidartodorov.projects.githubbrowserproject.model.Repository;
import com.bojidartodorov.projects.githubbrowserproject.model.User;
import com.bojidartodorov.projects.githubbrowserproject.util.SQLitePersistUtil;

import java.util.List;

/**
 * Created by Bojidar on 29.11.2015 г..
 */
public class PersistContributorsTask extends AsyncTask<Void, Void, Void> {

    private Context context = null;
    private List<User> userContributorsList;
    private Repository repository;

    public PersistContributorsTask(Context context) {
        this.context = context;
    }


    @Override
    protected final Void doInBackground(Void... params) {

        SQLitePersistUtil.persistContributor(context, this.userContributorsList, this.repository);

        return null;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public List<User> getUserContributorsList() {
        return userContributorsList;
    }

    public void setUserContributorsList(List<User> userContributorsList) {
        this.userContributorsList = userContributorsList;
    }
}
