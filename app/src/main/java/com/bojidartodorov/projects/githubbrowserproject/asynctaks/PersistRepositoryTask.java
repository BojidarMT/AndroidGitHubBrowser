package com.bojidartodorov.projects.githubbrowserproject.asynctaks;

import android.content.Context;
import android.os.AsyncTask;

import com.bojidartodorov.projects.githubbrowserproject.model.Follower;
import com.bojidartodorov.projects.githubbrowserproject.model.Repository;
import com.bojidartodorov.projects.githubbrowserproject.util.SQLitePersistUtil;

import java.util.List;

/**
 * Created by Bojidar on 29.11.2015 г..
 */
public class PersistRepositoryTask extends AsyncTask<Repository, Void, Void> {

    private Context context = null;

    public PersistRepositoryTask(Context context) {
        this.context = context;
    }


    @SafeVarargs
    @Override
    protected final Void doInBackground(Repository... params) {

        SQLitePersistUtil.persistRepository(context, params[0]);

        return null;
    }
}
