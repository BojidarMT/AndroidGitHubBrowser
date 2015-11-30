package com.bojidartodorov.projects.githubbrowserproject.activities;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.bojidartodorov.projects.githubbrowserproject.R;
import com.bojidartodorov.projects.githubbrowserproject.adapters.UserAdapter;
import com.bojidartodorov.projects.githubbrowserproject.api.GitHubApiService;
import com.bojidartodorov.projects.githubbrowserproject.converter.ToStringConverter;
import com.bojidartodorov.projects.githubbrowserproject.model.json_model.JsonUser;
import com.bojidartodorov.projects.githubbrowserproject.util.AndroidUtil;
import com.bojidartodorov.projects.githubbrowserproject.util.GsonUtil;
import com.bojidartodorov.projects.githubbrowserproject.util.JsonUtil;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class UserSearchActivity extends AppCompatActivity {

    private Intent intent;
    private Bundle bundle;

    private ListView usersListView;
    private List<JsonUser> usersList;
    private UserAdapter userAdapter;

    private String usersResponseStr;
    private JSONArray usersJsonArray;

    private ProgressDialog progressDialog;

    private Retrofit retrofit;
    private GitHubApiService gitHubApiService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.setTitle(getString(R.string.userSearch));

        this.intent = getIntent();
        this.bundle = new Bundle();

        this.usersList = new ArrayList<>();
        this.initListView();

        this.setUsersOnItemClickListener();

        this.checkForPredefinedData();

        this.createProgressDialog();

        this.createRetrofit();
        this.createGitHubService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_search, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                runGetRepositoriesService(query);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    public void logout(MenuItem menuItem) {
        AndroidUtil.createAlertDialog(this);
    }

    private void checkForPredefinedData() {

        boolean isForSearchOnly = intent.getBooleanExtra(getString(R.string.isForSearchOnly), false);

        if (!isForSearchOnly) {
            this.usersList = (List<JsonUser>) this.intent.getSerializableExtra(getString(R.string.userListKey));
            addAdapterToUsersListView();
        }

    }

    private void createProgressDialog() {
        this.progressDialog = AndroidUtil.createProgressDialog(getString(R.string.loadingRepos), this);
    }

    private void showProgressDialog() {
        this.progressDialog.show();
    }

    private void dissmissProgressDialog() {
        this.progressDialog.dismiss();
    }

    private void initListView() {
        this.usersListView = (ListView) findViewById(R.id.usersListView);
    }

    private void createRetrofit() {
        this.retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.githubApiRootUrl))
                .addConverterFactory(new ToStringConverter())
                .build();
    }

    private void createGitHubService() {
        this.gitHubApiService = this.retrofit.create(GitHubApiService.class);
    }

    private void runGetRepositoriesService(String username) {

        if (!progressDialog.isShowing()) {
            this.showProgressDialog();
        }

        Call<String> users = this.gitHubApiService.getUsers(username, "login");

        this.getUsersResponse(users);
    }

    private void getUsersResponse(Call<String> usersResponse) {

        usersResponse.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response, Retrofit retrofit) {
                int statusCode = response.code();

                if (progressDialog.isShowing()) {
                    dissmissProgressDialog();
                }

                if (statusCode == 200) {
                    usersResponseStr = response.body();
                    usersJsonArray = JsonUtil.convertToUsersJsonArray(usersResponseStr);
                    usersList = GsonUtil.convertJsonToUserObject(usersJsonArray.toString());


                    addAdapterToUsersListView();

                } else if (statusCode == 403) {
                    Toast.makeText(UserSearchActivity.this, R.string.limitExceeded, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(UserSearchActivity.this, t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addAdapterToUsersListView() {
        this.userAdapter = new UserAdapter(this, R.layout.item_user, this.usersList);
        this.usersListView.setAdapter(this.userAdapter);
        this.userAdapter.notifyDataSetChanged();

        AndroidUtil.justifyListViewHeightBasedOnChildren(this.usersListView);
    }

    private void setUsersOnItemClickListener() {
        this.usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                JsonUser jsonUser = (JsonUser) usersListView.getItemAtPosition(position);
                navigateToUserProfileScreen(jsonUser);

            }
        });
    }

    private void navigateToUserProfileScreen(JsonUser jsonUser) {
        Intent intent = new Intent(this, UserProfileActivity.class);
        this.setBundleData(jsonUser);

        intent.putExtras(this.bundle);
        startActivity(intent);
    }

    private void setBundleData(JsonUser jsonUser) {
        this.bundle.putString(getString(R.string.authPasswordKey), this.intent.getStringExtra(getString(R.string.authPasswordKey)));
        this.bundle.putString(getString(R.string.authLoginKey), this.intent.getStringExtra(getString(R.string.authLoginKey)));
        this.bundle.putSerializable(getString(R.string.userObjectKey), jsonUser);
    }
}
