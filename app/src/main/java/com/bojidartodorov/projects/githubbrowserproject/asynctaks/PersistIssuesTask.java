package com.bojidartodorov.projects.githubbrowserproject.asynctaks;

import android.content.Context;
import android.os.AsyncTask;

import com.bojidartodorov.projects.githubbrowserproject.model.Issue;
import com.bojidartodorov.projects.githubbrowserproject.model.User;
import com.bojidartodorov.projects.githubbrowserproject.util.SQLitePersistUtil;

import java.util.List;

/**
 * Created by Bojidar on 29.11.2015 г..
 */
public class PersistIssuesTask extends AsyncTask<Void, Void, Void> {

    private Context context = null;
    private List<Issue> issueList;

    public PersistIssuesTask(Context context) {
        this.context = context;
    }


    @Override
    protected final Void doInBackground(Void... params) {

        SQLitePersistUtil.persistIssues(context, this.issueList);

        return null;
    }

    public List<Issue> getIssueList() {
        return issueList;
    }

    public void setIssueList(List<Issue> issueList) {
        this.issueList = issueList;
    }
}
