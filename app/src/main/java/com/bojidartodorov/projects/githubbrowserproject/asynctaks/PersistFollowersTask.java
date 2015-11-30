package com.bojidartodorov.projects.githubbrowserproject.asynctaks;

import android.content.Context;
import android.os.AsyncTask;

import com.bojidartodorov.projects.githubbrowserproject.model.User;
import com.bojidartodorov.projects.githubbrowserproject.util.SQLitePersistUtil;

import java.util.List;

/**
 * Created by Bojidar on 29.11.2015 г..
 */
public class PersistFollowersTask extends AsyncTask<Void, Void, Void> {

    private Context context = null;
    private List<User> followersList;
    private User followingUser;

    public PersistFollowersTask(Context context) {
        this.context = context;
    }


    @Override
    protected final Void doInBackground(Void... params) {

        SQLitePersistUtil.persistFollowers(context, this.followersList, this.followingUser);

        return null;
    }

    public List<User> getFollowersList() {
        return followersList;
    }

    public void setFollowersList(List<User> followersList) {
        this.followersList = followersList;
    }

    public User getFollowingUser() {
        return followingUser;
    }

    public void setFollowingUser(User followingUser) {
        this.followingUser = followingUser;
    }
}
