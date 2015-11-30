package com.bojidartodorov.projects.githubbrowserproject.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.bojidartodorov.projects.githubbrowserproject.R;
import com.bojidartodorov.projects.githubbrowserproject.adapters.ViewPagerAdapter;
import com.bojidartodorov.projects.githubbrowserproject.api.GitHubApiService;
import com.bojidartodorov.projects.githubbrowserproject.asynctaks.LoadAvatarTask;
import com.bojidartodorov.projects.githubbrowserproject.asynctaks.PersistContributorsTask;
import com.bojidartodorov.projects.githubbrowserproject.asynctaks.PersistRepositoryTask;
import com.bojidartodorov.projects.githubbrowserproject.asynctaks.taskresponses.AvatarBitmapResponse;
import com.bojidartodorov.projects.githubbrowserproject.model.Repository;
import com.bojidartodorov.projects.githubbrowserproject.model.User;
import com.bojidartodorov.projects.githubbrowserproject.model.json_model.JsonRepository;
import com.bojidartodorov.projects.githubbrowserproject.model.json_model.JsonUser;
import com.bojidartodorov.projects.githubbrowserproject.model.json_model.JsonIssue;
import com.bojidartodorov.projects.githubbrowserproject.util.AndroidUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class RepositoryActivity extends AppCompatActivity implements AvatarBitmapResponse {

    private boolean isRepoStarred;
    private boolean isFromOwned;

    private Intent intent;

    private TextView commits;
    private TextView branches;
    private TextView releases;
    private TextView stars;
    private TextView forks;
    private TextView owner;
    private TextView languages;
    private TextView repositoryName;

    private ViewPager viewPager;
    private PagerSlidingTabStrip tabsStrip;

    private ImageView avatarImageView;

    private MenuItem starRepo;

    private Button contributorsButton;

    private JsonUser jsonUser;

    private ProgressDialog progressDialog;

    private String languagesResponse;

    private String contentLength;

    private JsonRepository jsonRepository;

    private Retrofit retrofit;
    private GitHubApiService gitHubApiService;

    private List<JsonIssue> allIssuesList;
    private List<JsonUser> repoContributors;

    private LoadAvatarTask loadAvatarTask;

    //SQLITE
    private Repository cacheRepository;
    private User cacheUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repository);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton search = (FloatingActionButton) findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToUserSearchScreen();
            }
        });

        this.setTitle(getString(R.string.repoTitle));

        this.intent = getIntent();

        this.isFromOwned = this.intent.getBooleanExtra(getString(R.string.isFromOwnedScreen), false);
        this.jsonUser = (JsonUser) this.intent.getSerializableExtra(getString(R.string.userObjectKey));
        this.jsonRepository = (JsonRepository) intent.getSerializableExtra(getString(R.string.repoObject));

        //SQLITE--------------------------------------------

        this.cacheRepository = new Repository();
        this.cacheUser = new User();
        this.setCacheUser();
        this.setCacheRepository(this.isFromOwned);
        this.persistRepositoryToSQLite();

        // -------------------------------------------------

        this.repoContributors = new ArrayList<>();

        this.findViewPagerById();
        this.setTabsAdapter();

        this.findSlidingTabsById();

        this.setViewPager();

        this.createProgressDialog();

        this.findTextViewsById();
        this.findButtonsById();
        this.findImageViewsById();

        this.contentLength = "0";

        this.setOwnerView();
        this.setRepositoryNameView();

        this.setStarsCountToView(this.jsonRepository.getStargazersCount());
        this.setForksCountToView(this.jsonRepository.getForksCount());

        this.createRetrofit();
        this.createGitHubService();

        this.runGetRepoCommitsService();
        this.runGetRepoBranchesService();
        this.runGetRepoReleasesService();
        this.runGetRepoContributorsService();
        this.runCheckRepoIsStarredService();
        //this.runGetLanguagesService();

        this.setupLoadAvatarTask(this.jsonUser.getAvatarUrl());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_repository, menu);

        this.starRepo = menu.findItem(R.id.starRepo);

        return true;
    }

    public void logout(MenuItem menuItem) {
        AndroidUtil.createAlertDialog(this);
    }

    private void navigateToUserSearchScreen() {
        Bundle userBundle = new Bundle();
        userBundle.putString(getString(R.string.authPasswordKey), this.intent.getStringExtra(getString(R.string.authPasswordKey)));
        userBundle.putString(getString(R.string.authLoginKey), this.intent.getStringExtra(getString(R.string.authLoginKey)));
        userBundle.putBoolean(getString(R.string.isForSearchOnly), true);

        Intent userSearchIntent = new Intent(this, UserSearchActivity.class);
        userSearchIntent.putExtras(userBundle);
        startActivity(userSearchIntent);
    }

    public void starRepo(MenuItem menuItem) {

        if (this.isRepoStarred) {
            this.runUnstarRepoService();
        } else {
            this.runStarRepoService();
        }

    }

    private void findTextViewsById() {
        this.commits = (TextView) findViewById(R.id.commits);
        this.branches = (TextView) findViewById(R.id.branches);
        this.releases = (TextView) findViewById(R.id.releases);
        this.stars = (TextView) findViewById(R.id.stars);
        this.forks = (TextView) findViewById(R.id.forks);
        this.owner = (TextView) findViewById(R.id.owner);
        this.languages = (TextView) findViewById(R.id.languages);
        this.repositoryName = (TextView) findViewById(R.id.repositoryName);
    }

    private void findButtonsById() {
        this.contributorsButton = (Button) findViewById(R.id.contributorsBtn);
    }

    private void findImageViewsById() {
        this.avatarImageView = (ImageView) findViewById(R.id.avatar);
    }

    private void findViewPagerById() {
        this.viewPager = (ViewPager) findViewById(R.id.viewpager);
    }

    private void setTabsAdapter() {
        this.viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        this.viewPager.setOffscreenPageLimit(2);
    }

    private void findSlidingTabsById() {
        this.tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
    }

    private void setViewPager() {
        this.tabsStrip.setViewPager(viewPager);
    }

    private void createProgressDialog() {
        this.progressDialog = AndroidUtil.createProgressDialog(getString(R.string.loadingData), this);
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

    private void runGetRepoCommitsService() {

        if (!this.progressDialog.isShowing()) {
            this.showProgressDialog();
        }

        Call<List<JsonRepository>> repositories = this.gitHubApiService.getCommits(this.jsonRepository.getOwner().getLogin(), this.jsonRepository.getName());

        this.getRepoCommitsResponse(repositories);
    }

    //TODO implement languages service
    private void runGetLanguagesService() {

        if (!this.progressDialog.isShowing()) {
            this.showProgressDialog();
        }

        Call<String> repositories = this.gitHubApiService.getLanguages(this.jsonRepository.getOwner().getLogin(), this.jsonRepository.getName());

        this.getLanguagesResponse(repositories);
    }

    private void runGetRepoBranchesService() {

        if (!this.progressDialog.isShowing()) {
            this.showProgressDialog();
        }

        Call<List<JsonRepository>> repositories = this.gitHubApiService.getBranches(this.jsonRepository.getOwner().getLogin(), this.jsonRepository.getName());

        this.getRepoBranchesResponse(repositories);
    }

    private void runGetRepoReleasesService() {

        if (!this.progressDialog.isShowing()) {
            this.showProgressDialog();
        }

        Call<List<JsonRepository>> repositories = this.gitHubApiService.getReleases(this.jsonRepository.getOwner().getLogin(), this.jsonRepository.getName());

        this.getRepoReleasesResponse(repositories);
    }

    private void runCheckRepoIsStarredService() {

        String authorization = AndroidUtil.createAuthorizationString(this.intent.getStringExtra(getString(R.string.authLoginKey)), this.intent.getStringExtra(getString(R.string.authPasswordKey)));

        Call<com.squareup.okhttp.Response> responseCall = this.gitHubApiService.checkRepoIsStarred(authorization, this.jsonRepository.getOwner().getLogin(), this.jsonRepository.getName());

        this.checkIsRepoStarredResponse(responseCall);
    }

    private void runGetRepoContributorsService() {

        if (!this.progressDialog.isShowing()) {
            this.showProgressDialog();
        }

        Call<List<JsonUser>> repositories = this.gitHubApiService.getContributors(this.jsonRepository.getOwner().getLogin(), this.jsonRepository.getName());

        this.getRepoContributorsResponse(repositories);
    }

    private void runStarRepoService() {

        String authorization = AndroidUtil.createAuthorizationString(this.intent.getStringExtra(getString(R.string.authLoginKey)), this.intent.getStringExtra(getString(R.string.authPasswordKey)));

        if (!this.progressDialog.isShowing()) {
            this.showProgressDialog();
        }

        Call<com.squareup.okhttp.Response> responseCall = this.gitHubApiService.starRepo(authorization, this.contentLength, this.jsonRepository.getOwner().getLogin(), this.jsonRepository.getName());

        this.starRepoResponse(responseCall);
    }

    private void runUnstarRepoService() {

        String authorization = AndroidUtil.createAuthorizationString(this.intent.getStringExtra(getString(R.string.authLoginKey)), this.intent.getStringExtra(getString(R.string.authPasswordKey)));

        if (!this.progressDialog.isShowing()) {
            this.showProgressDialog();
        }

        Call<com.squareup.okhttp.Response> responseCall = this.gitHubApiService.ustarRepo(authorization, this.contentLength, this.jsonRepository.getOwner().getLogin(), this.jsonRepository.getName());

        this.unstarRepoResponse(responseCall);
    }

    private void getRepoCommitsResponse(Call<List<JsonRepository>> repositoriesResponse) {

        repositoriesResponse.enqueue(new Callback<List<JsonRepository>>() {
            @Override
            public void onResponse(Response<List<JsonRepository>> response, Retrofit retrofit) {
                int statusCode = response.code();

                if (progressDialog.isShowing()) {
                    dissmissProgressDialog();
                }

                if (statusCode == 200) {
                    List<JsonRepository> repoCommits = response.body();

                    setCommitsView(repoCommits.size());

                    //SQLITE-------------
                    setCacheRepositoryCommits(repoCommits.size());
                    persistRepositoryToSQLite();
                    //-------------------

                } else if (statusCode == 403) {
                    Toast.makeText(RepositoryActivity.this, R.string.limitExceeded, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Throwable t) {

                if (progressDialog.isShowing()) {
                    dissmissProgressDialog();
                }

                Toast.makeText(RepositoryActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }

    private void getLanguagesResponse(Call<String> languagesResponse) {

        languagesResponse.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response, Retrofit retrofit) {
                String languagesResponse = response.body();
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });

    }

    private void getRepoBranchesResponse(Call<List<JsonRepository>> repositoriesResponse) {

        repositoriesResponse.enqueue(new Callback<List<JsonRepository>>() {
            @Override
            public void onResponse(Response<List<JsonRepository>> response, Retrofit retrofit) {
                int statusCode = response.code();

                if (progressDialog.isShowing()) {
                    dissmissProgressDialog();
                }

                if (statusCode == 200) {
                    List<JsonRepository> repoBranches = response.body();

                    setBranchesView(repoBranches.size());

                    //SQLITE-------------
                    setCacheRepositoryBranches(repoBranches.size());
                    persistRepositoryToSQLite();
                    //-------------------
                } else if (statusCode == 403) {
                    Toast.makeText(RepositoryActivity.this, R.string.limitExceeded, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Throwable t) {

                if (progressDialog.isShowing()) {
                    dissmissProgressDialog();
                }

                Toast.makeText(RepositoryActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }

    private void getRepoReleasesResponse(Call<List<JsonRepository>> repositoriesResponse) {

        repositoriesResponse.enqueue(new Callback<List<JsonRepository>>() {
            @Override
            public void onResponse(Response<List<JsonRepository>> response, Retrofit retrofit) {
                int statusCode = response.code();

                if (progressDialog.isShowing()) {
                    dissmissProgressDialog();
                }

                if (statusCode == 200) {
                    List<JsonRepository> repoReleases = response.body();

                    setReleasesView(repoReleases.size());

                    //SQLITE-------------
                    setCacheRepositoryReleases(repoReleases.size());
                    persistRepositoryToSQLite();
                    //-------------------
                } else if (statusCode == 403) {
                    Toast.makeText(RepositoryActivity.this, R.string.limitExceeded, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Throwable t) {

                if (progressDialog.isShowing()) {
                    dissmissProgressDialog();
                }

                Toast.makeText(RepositoryActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }

    private void getRepoContributorsResponse(Call<List<JsonUser>> repositoriesResponse) {

        repositoriesResponse.enqueue(new Callback<List<JsonUser>>() {
            @Override
            public void onResponse(Response<List<JsonUser>> response, Retrofit retrofit) {
                int statusCode = response.code();

                if (progressDialog.isShowing()) {
                    dissmissProgressDialog();
                }

                if (statusCode == 200) {
                    repoContributors = response.body();

                    setContributorsView(repoContributors.size());

                    //SQLite implementation
                    setContributosListForSQLite(repoContributors);
                    //----------------------
                } else if (statusCode == 403) {
                    Toast.makeText(RepositoryActivity.this, R.string.limitExceeded, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Throwable t) {

                if (progressDialog.isShowing()) {
                    dissmissProgressDialog();
                }

                Toast.makeText(RepositoryActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }

    private void checkIsRepoStarredResponse(Call<com.squareup.okhttp.Response> responseCall) {

        responseCall.enqueue(new Callback<com.squareup.okhttp.Response>() {
            @Override
            public void onResponse(Response<com.squareup.okhttp.Response> response, Retrofit retrofit) {
                int statusCode = response.code();

                if (statusCode == 204) {
                    isRepoStarred = true;

                    starRepo.setIcon(R.drawable.ic_stars_black_36dp);

                    //SQLITE-------------
                    setCacheRepositoryIsStarred(isRepoStarred);
                    persistRepositoryToSQLite();
                    //-------------------
                } else if (statusCode == 404) {
                    isRepoStarred = false;

                    starRepo.setIcon(R.drawable.ic_stars_white_36dp);


                    //SQLITE-------------
                    setCacheRepositoryIsStarred(isRepoStarred);
                    persistRepositoryToSQLite();
                    //-------------------
                } else if (statusCode == 403) {
                    Toast.makeText(RepositoryActivity.this, R.string.limitExceeded, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(RepositoryActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void starRepoResponse(Call<com.squareup.okhttp.Response> responseCall) {

        responseCall.enqueue(new Callback<com.squareup.okhttp.Response>() {
            @Override
            public void onResponse(Response<com.squareup.okhttp.Response> response, Retrofit retrofit) {
                int statusCode = response.code();

                if (progressDialog.isShowing()) {
                    dissmissProgressDialog();
                }

                if (statusCode == 204) {
                    isRepoStarred = true;

                    starRepo.setIcon(R.drawable.ic_stars_black_36dp);

                    //SQLITE-------------
                    setCacheRepositoryIsStarred(isRepoStarred);
                    persistRepositoryToSQLite();
                    //-------------------
                } else if (statusCode == 403) {
                    Toast.makeText(RepositoryActivity.this, R.string.limitExceeded, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {

                if (progressDialog.isShowing()) {
                    dissmissProgressDialog();
                }

                Toast.makeText(RepositoryActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void unstarRepoResponse(Call<com.squareup.okhttp.Response> responseCall) {

        responseCall.enqueue(new Callback<com.squareup.okhttp.Response>() {
            @Override
            public void onResponse(Response<com.squareup.okhttp.Response> response, Retrofit retrofit) {
                int statusCode = response.code();

                if (progressDialog.isShowing()) {
                    dissmissProgressDialog();
                }

                if (statusCode == 204) {
                    isRepoStarred = false;

                    starRepo.setIcon(R.drawable.ic_stars_white_36dp);

                    //SQLITE-------------
                    setCacheRepositoryIsStarred(isRepoStarred);
                    persistRepositoryToSQLite();
                    //-------------------
                } else if (statusCode == 403) {
                    Toast.makeText(RepositoryActivity.this, R.string.limitExceeded, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {

                if (progressDialog.isShowing()) {
                    dissmissProgressDialog();
                }

                Toast.makeText(RepositoryActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setCommitsView(int commitsSize) {
        this.commits.setText((getString(R.string.commitsTitle, String.format("%3d", commitsSize))));
    }

    private void setBranchesView(int branchesSize) {
        this.branches.setText((getString(R.string.branchesTitle, String.format("%3d", branchesSize))));
    }

    private void setReleasesView(int releasesSize) {
        this.releases.setText((getString(R.string.releasesTitle, String.format("%3d", releasesSize))));
    }

    private void setContributorsView(int contributorsSize) {
        this.contributorsButton.setText((getString(R.string.contributorsTitle, String.format("%3d", contributorsSize))));
    }

    private void setOwnerView() {
        this.owner.setText(this.jsonUser.getLogin());
    }

    private void setRepositoryNameView() {
        this.repositoryName.setText(this.jsonRepository.getName());
    }

    private void setupLoadAvatarTask(String url) {
        this.loadAvatarTask = new LoadAvatarTask(this, getString(R.string.loadingData));
        this.loadAvatarTask.setAvatarBitmapResponse(this);
        this.loadAvatarTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
    }

    private void setStarsCountToView(int starsCount) {
        this.stars.setText((getString(R.string.starTitle, Integer.toString(starsCount))));
    }

    private void setForksCountToView(int forksCount) {
        this.forks.setText((getString(R.string.forkTitle, Integer.toString(forksCount))));
    }

    @Override
    public void avatarBitmapResponse(Bitmap bitmap) {
        this.avatarImageView.setImageBitmap(bitmap);
    }

    private Bundle setBundleData() {
        Bundle userProfileBundle = new Bundle();
        userProfileBundle.putString(getString(R.string.authPasswordKey), this.intent.getStringExtra(getString(R.string.authPasswordKey)));
        userProfileBundle.putString(getString(R.string.authLoginKey), this.intent.getStringExtra(getString(R.string.authLoginKey)));
        userProfileBundle.putSerializable(getString(R.string.userObjectKey), this.jsonUser);

        return userProfileBundle;
    }

    public void toUserProfile(View view) {

        Bundle userProfileBundle = this.setBundleData();

        Intent userProfileIntent = new Intent(this, UserProfileActivity.class);
        userProfileIntent.putExtras(userProfileBundle);
        startActivity(userProfileIntent);
    }

    public void navigateToUserContributorsSearchScreen(View view) {
        Bundle userBundle = new Bundle();
        userBundle.putSerializable(getString(R.string.userListKey), (Serializable) this.repoContributors);
        userBundle.putString(getString(R.string.authPasswordKey), this.intent.getStringExtra(getString(R.string.authPasswordKey)));
        userBundle.putString(getString(R.string.authLoginKey), this.intent.getStringExtra(getString(R.string.authLoginKey)));
        userBundle.putBoolean(getString(R.string.isForSearchOnly), false);

        Intent userSearchIntent = new Intent(this, UserSearchActivity.class);
        userSearchIntent.putExtras(userBundle);
        startActivity(userSearchIntent);
    }

    //SQLITE IMPLEMENTATION

    private void setCacheUser() {
        this.cacheUser.setId(this.jsonUser.getId());
    }

    private void setCacheRepository(boolean isFromOwned) {
        this.cacheRepository.setId(this.jsonRepository.getId());
        this.cacheRepository.setName(this.jsonRepository.getName());
        this.cacheRepository.setStarsCount(this.jsonRepository.getStargazersCount());
        this.cacheRepository.setForksCount(this.jsonRepository.getForksCount());
        this.cacheRepository.setIsOwned(isFromOwned);
        this.cacheRepository.setUser(this.cacheUser);
    }

    private void setCacheRepositoryCommits(int commitsCount) {
        this.cacheRepository.setCommitsCount(commitsCount);
    }

    private void setCacheRepositoryBranches(int branchesCount) {
        this.cacheRepository.setBranchesCount(branchesCount);
    }

    private void setCacheRepositoryReleases(int releasesCount) {
        this.cacheRepository.setReleasesCount(releasesCount);
    }

    private void setCacheRepositoryIsStarred(boolean isStarred) {
        this.cacheRepository.setIsStarred(isStarred);
    }

    private void persistRepositoryToSQLite() {

        this.setupPersistRepositoryTask(this.cacheRepository);

    }

    private void setupPersistRepositoryTask(Repository repository) {
        PersistRepositoryTask persistRepositoryTask = new PersistRepositoryTask(this);
        persistRepositoryTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, repository);
    }

    private void setContributosListForSQLite(List<JsonUser> jsonContributorsList) {

        List<User> cacheContributorList = new ArrayList<>();

        for (JsonUser jsonUser : jsonContributorsList) {
            User cacheContributor = new User();
            cacheContributor.setId(jsonUser.getId());
            cacheContributor.setLogin(jsonUser.getLogin());

            cacheContributorList.add(cacheContributor);
        }

        this.setupPersistContributorsTask(cacheContributorList);
    }

    private void setupPersistContributorsTask(List<User> cacheContributorList) {

        PersistContributorsTask persistContributorsTask = new PersistContributorsTask(this);
        persistContributorsTask.setUserContributorsList(cacheContributorList);
        persistContributorsTask.setRepository(this.cacheRepository);
        persistContributorsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

}
