package com.bojidartodorov.projects.githubbrowserproject.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.bojidartodorov.projects.githubbrowserproject.R;
import com.bojidartodorov.projects.githubbrowserproject.activities.RepositoryActivity;
import com.bojidartodorov.projects.githubbrowserproject.adapters.JsonRepositoryAdapter;
import com.bojidartodorov.projects.githubbrowserproject.adapters.RepositoryAdapter;
import com.bojidartodorov.projects.githubbrowserproject.api.GitHubApiService;
import com.bojidartodorov.projects.githubbrowserproject.asynctaks.FetchStarredRepositoriesTask;
import com.bojidartodorov.projects.githubbrowserproject.asynctaks.taskresponses.FetchRepositoriesResponse;
import com.bojidartodorov.projects.githubbrowserproject.model.Repository;
import com.bojidartodorov.projects.githubbrowserproject.model.json_model.JsonRepository;
import com.bojidartodorov.projects.githubbrowserproject.model.json_model.JsonUser;
import com.bojidartodorov.projects.githubbrowserproject.util.AndroidUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class StarredReposFragment extends Fragment implements FetchRepositoriesResponse {

    private OnFragmentInteractionListener mListener;

    private JsonUser jsonUser;

    private ProgressDialog progressDialog;

    private Retrofit retrofit;
    private GitHubApiService gitHubApiService;

    private Bundle bundle;

    private ListView repositoriesListView;
    private List<JsonRepository> jsonRepositoryList;
    private JsonRepositoryAdapter jsonRepositoryAdapter;
    private RepositoryAdapter repositoryAdapter;

    public StarredReposFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_starred_repos, container, false);

        this.bundle = getArguments();

        this.jsonUser = (JsonUser) this.bundle.getSerializable(getString(R.string.userObjectKey));

        this.createProgressDialog();

        this.jsonRepositoryList = new ArrayList<>();
        this.initListView(view);

        this.setReposOnItemClickListener();

        this.createRetrofit();
        this.createGitHubService();

        if (AndroidUtil.isNetworkAvailable(getActivity())) {
            this.runGetRepositoriesService();
        } else {
            this.setupFetchStarredRepositoriesTask(this.jsonUser.getId());
        }

        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void initListView(View view) {
        repositoriesListView = (ListView) view.findViewById(R.id.repositoriesListView);
    }

    private void createProgressDialog() {
        this.progressDialog = AndroidUtil.createProgressDialog(getString(R.string.loadingRepos), getActivity());
    }

    private void showProgressDialog() {
        this.progressDialog.show();
    }

    private void dissmissProgressDialog() {
        this.progressDialog.dismiss();
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

    private void runGetRepositoriesService() {

        if (!progressDialog.isShowing()) {
            this.showProgressDialog();
        }

        Call<List<JsonRepository>> repositories = this.gitHubApiService.getStarredRepositories(this.jsonUser.getLogin());

        this.getRepositoriesResponse(repositories);
    }

    private void getRepositoriesResponse(Call<List<JsonRepository>> repositoriesResponse) {

        repositoriesResponse.enqueue(new Callback<List<JsonRepository>>() {
            @Override
            public void onResponse(Response<List<JsonRepository>> response, Retrofit retrofit) {
                int statusCode = response.code();

                if (progressDialog.isShowing()) {
                    dissmissProgressDialog();
                }

                if (statusCode == 200) {
                    jsonRepositoryList = response.body();

                    addJsonAdapterToRepositoriesListView();

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

    private void addJsonAdapterToRepositoriesListView() {
        this.jsonRepositoryAdapter = new JsonRepositoryAdapter(getActivity(), R.layout.item_repository, this.jsonRepositoryList);
        this.repositoriesListView.setAdapter(this.jsonRepositoryAdapter);
        this.jsonRepositoryAdapter.notifyDataSetChanged();

        AndroidUtil.justifyListViewHeightBasedOnChildren(this.repositoriesListView);
    }

    private void addAdapterToRepositoriesListView(List<Repository> repositoryList) {
        this.repositoryAdapter = new RepositoryAdapter(getActivity(), R.layout.item_repository, repositoryList);
        this.repositoriesListView.setAdapter(this.repositoryAdapter);
        this.repositoryAdapter.notifyDataSetChanged();

        AndroidUtil.justifyListViewHeightBasedOnChildren(this.repositoriesListView);
    }

    private void setReposOnItemClickListener() {
        this.repositoriesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                JsonRepository jsonRepository = (JsonRepository) repositoriesListView.getItemAtPosition(position);
                navigateToRepositoryScreen(jsonRepository);

            }
        });
    }

    private void navigateToRepositoryScreen(JsonRepository jsonRepository) {
        Intent intent = new Intent(getActivity(), RepositoryActivity.class);
        Bundle repoBundle = setBundle(jsonRepository);

        intent.putExtras(repoBundle);
        startActivity(intent);
    }

    private Bundle setBundle(JsonRepository jsonRepository) {
        Bundle repoBundle = new Bundle();
        repoBundle.putString(getString(R.string.authPasswordKey), this.bundle.getString(getString(R.string.authPasswordKey)));
        repoBundle.putString(getString(R.string.authLoginKey), this.bundle.getString(getString(R.string.authLoginKey)));
        repoBundle.putSerializable(getString(R.string.userObjectKey), this.jsonUser);
        repoBundle.putSerializable(getString(R.string.repoObject), jsonRepository);
        repoBundle.putBoolean(getActivity().getString(R.string.isFromOwnedScreen), false);

        return repoBundle;
    }

    //SQLite implementation

    private void setupFetchStarredRepositoriesTask(long userId) {
        FetchStarredRepositoriesTask fetchStarredRepositoriesTask = new FetchStarredRepositoriesTask(getActivity(), getString(R.string.loadingRepos));
        fetchStarredRepositoriesTask.setFetchRepositoriesResponse(this);
        fetchStarredRepositoriesTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, userId);
    }

    @Override
    public void repositoriesList(List<Repository> repositoryList) {
        this.addAdapterToRepositoriesListView(repositoryList);
    }
}
