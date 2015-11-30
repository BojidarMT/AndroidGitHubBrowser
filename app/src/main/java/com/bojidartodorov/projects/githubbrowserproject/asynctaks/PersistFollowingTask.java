package com.bojidartodorov.projects.githubbrowserproject.asynctaks;

import android.content.Context;
import android.os.AsyncTask;

import com.bojidartodorov.projects.githubbrowserproject.model.User;
import com.bojidartodorov.projects.githubbrowserproject.util.SQLitePersistUtil;

import java.util.List;

/**
 * Created by Bojidar on 29.11.2015 г..
 */
public class PersistFollowingTask extends AsyncTask<Void, Void, Void> {

    private Context context = null;
    private List<User> followingList;
    private User followerUser;

    public PersistFollowingTask(Context context) {
        this.context = context;
    }


    @Override
    protected final Void doInBackground(Void... params) {

        SQLitePersistUtil.persistFollowing(context, this.followingList, this.followerUser);

        return null;
    }

    public User getFollowerUser() {
        return followerUser;
    }

    public void setFollowerUser(User followerUser) {
        this.followerUser = followerUser;
    }

    public List<User> getFollowingList() {
        return followingList;
    }

    public void setFollowingList(List<User> followingList) {
        this.followingList = followingList;
    }
}
