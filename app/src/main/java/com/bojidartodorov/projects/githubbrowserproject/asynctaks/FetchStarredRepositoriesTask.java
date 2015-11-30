package com.bojidartodorov.projects.githubbrowserproject.asynctaks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.bojidartodorov.projects.githubbrowserproject.asynctaks.taskresponses.FetchRepositoriesResponse;
import com.bojidartodorov.projects.githubbrowserproject.model.Repository;
import com.bojidartodorov.projects.githubbrowserproject.util.SQLitePersistUtil;

import java.util.List;

/**
 * Created by Bojidar on 30.11.2015 г..
 */
public class FetchStarredRepositoriesTask extends AsyncTask<Long, Void, List<Repository>> {

    private String processMessage;
    private ProgressDialog pDlg;
    private Context context;

    private FetchRepositoriesResponse fetchRepositoriesResponse;

    public FetchStarredRepositoriesTask(Context context, String processMessage) {
        this.context = context;
        this.processMessage = processMessage;
    }

    private void showProgressDialog() {

        this.pDlg = new ProgressDialog(context);
        this.pDlg.setMessage(processMessage);
        this.pDlg.show();
        this.pDlg.setCancelable(false);
    }

    @Override
    protected void onPreExecute() {

        if (this.pDlg == null) {
            this.showProgressDialog();
        }

    }

    @Override
    protected List<Repository> doInBackground(Long... params) {
        return SQLitePersistUtil.fetchStarredRepositories(this.context, params[0]);
    }

    @Override
    protected void onPostExecute(List<Repository> repositoryList) {

        fetchRepositoriesResponse.repositoriesList(repositoryList);

        if (this.pDlg != null && this.pDlg.isShowing()) {
            this.pDlg.dismiss();
            this.pDlg = null;

        }
    }

    public FetchRepositoriesResponse getFetchRepositoriesResponse() {
        return fetchRepositoriesResponse;
    }

    public void setFetchRepositoriesResponse(FetchRepositoriesResponse fetchRepositoriesResponse) {
        this.fetchRepositoriesResponse = fetchRepositoriesResponse;
    }
}
