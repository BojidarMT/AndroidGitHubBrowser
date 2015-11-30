package com.bojidartodorov.projects.githubbrowserproject.asynctaks;

import android.content.Context;
import android.os.AsyncTask;

import com.bojidartodorov.projects.githubbrowserproject.model.User;
import com.bojidartodorov.projects.githubbrowserproject.util.SQLitePersistUtil;

/**
 * Created by Bojidar on 29.11.2015 г..
 */
public class PersistUserTask extends AsyncTask<User, Void, Void> {

    private Context context = null;

    public PersistUserTask(Context context) {
        this.context = context;
    }


    @Override
    protected Void doInBackground(User... params) {

        SQLitePersistUtil.persistUser(this.context, params[0]);

        return null;
    }
}
