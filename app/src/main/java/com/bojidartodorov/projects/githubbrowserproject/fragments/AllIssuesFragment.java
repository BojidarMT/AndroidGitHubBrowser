package com.bojidartodorov.projects.githubbrowserproject.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.bojidartodorov.projects.githubbrowserproject.R;
import com.bojidartodorov.projects.githubbrowserproject.adapters.IssueAdapter;
import com.bojidartodorov.projects.githubbrowserproject.api.GitHubApiService;
import com.bojidartodorov.projects.githubbrowserproject.model.json_model.JsonIssue;
import com.bojidartodorov.projects.githubbrowserproject.model.json_model.JsonUser;
import com.bojidartodorov.projects.githubbrowserproject.model.json_model.JsonRepository;
import com.bojidartodorov.projects.githubbrowserproject.util.AndroidUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class AllIssuesFragment extends Fragment {

    private Intent intent;

    private ProgressDialog progressDialog;

    private ListView issuesListView;
    private IssueAdapter issueAdapter;

    private Retrofit retrofit;
    private GitHubApiService gitHubApiService;

    private JsonUser jsonUser;
    private JsonRepository jsonRepository;

    private List<JsonIssue> allIssuesList;

    public AllIssuesFragment() {
        // Required empty public constructor
    }

    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;

    public static AllIssuesFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        AllIssuesFragment fragment = new AllIssuesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_opened_issues, container, false);

        this.intent = getActivity().getIntent();

        this.jsonUser = (JsonUser) this.intent.getSerializableExtra(getString(R.string.userObjectKey));
        this.jsonRepository = (JsonRepository) intent.getSerializableExtra(getString(R.string.repoObject));

        this.allIssuesList = new ArrayList<>();
        this.initListView(view);

        this.createProgressDialog();

        this.createRetrofit();
        this.createGitHubService();

        this.runGetRepoIssuesService();

        return view;
    }

    private void createProgressDialog() {
        this.progressDialog = AndroidUtil.createProgressDialog(getString(R.string.loadingData), getActivity());
    }

    private void showProgressDialog() {
        this.progressDialog.show();
    }

    private void dissmissProgressDialog() {
        this.progressDialog.dismiss();
    }

    private void initListView(View view) {
        this.issuesListView = (ListView) view.findViewById(R.id.openedIssuesListView);
    }

    private void createRetrofit() {
        this.retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.githubApiRootUrl))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private void createGitHubService() {
        this.gitHubApiService = this.retrofit.create(GitHubApiService.class);
    }

    private void runGetRepoIssuesService() {

        if (!this.progressDialog.isShowing()) {
            this.showProgressDialog();
        }

        Call<List<JsonIssue>> repositories = this.gitHubApiService.getRepoIssues(this.jsonUser.getLogin(), this.jsonRepository.getName(), "all");

        this.getRepoIssuesResponse(repositories);
    }

    private void getRepoIssuesResponse(Call<List<JsonIssue>> issuesResponse) {

        issuesResponse.enqueue(new Callback<List<JsonIssue>>() {
            @Override
            public void onResponse(Response<List<JsonIssue>> response, Retrofit retrofit) {
                int statusCode = response.code();

                if (progressDialog.isShowing()) {
                    dissmissProgressDialog();
                }

                if (statusCode == 200) {
                    allIssuesList = response.body();

                    addAdapterToIssuesListView();

                } else if (statusCode == 403) {
                    Toast.makeText(getActivity(), R.string.limitExceeded, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Throwable t) {

                if (progressDialog.isShowing()) {
                    dissmissProgressDialog();
                }

                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }

    private void addAdapterToIssuesListView() {
        this.issueAdapter = new IssueAdapter(getActivity(), R.layout.item_issue, this.allIssuesList);
        this.issuesListView.setAdapter(this.issueAdapter);
        this.issueAdapter.notifyDataSetChanged();

        AndroidUtil.justifyListViewHeightBasedOnChildren(this.issuesListView);
    }

}
