package com.bojidartodorov.projects.githubbrowserproject.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bojidartodorov.projects.githubbrowserproject.R;
import com.bojidartodorov.projects.githubbrowserproject.api.GitHubApiService;
import com.bojidartodorov.projects.githubbrowserproject.asynctaks.LoadAvatarTask;
import com.bojidartodorov.projects.githubbrowserproject.asynctaks.PersistFollowersTask;
import com.bojidartodorov.projects.githubbrowserproject.asynctaks.PersistFollowingTask;
import com.bojidartodorov.projects.githubbrowserproject.asynctaks.PersistUserTask;
import com.bojidartodorov.projects.githubbrowserproject.asynctaks.taskresponses.AvatarBitmapResponse;
import com.bojidartodorov.projects.githubbrowserproject.fragments.OwnedReposFragment;
import com.bojidartodorov.projects.githubbrowserproject.fragments.StarredReposFragment;
import com.bojidartodorov.projects.githubbrowserproject.model.User;
import com.bojidartodorov.projects.githubbrowserproject.model.json_model.JsonUser;
import com.bojidartodorov.projects.githubbrowserproject.util.AndroidUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class UserProfileActivity extends AppCompatActivity implements OwnedReposFragment.OnFragmentInteractionListener, StarredReposFragment.OnFragmentInteractionListener, AvatarBitmapResponse {

    private Bundle bundle;
    private Intent intent;

    private JsonUser jsonUser;

    private List<JsonUser> jsonUserFollowersList;
    private List<JsonUser> jsonUserFollowingList;

    private ProgressDialog progressDialog;

    private Retrofit retrofit;
    private GitHubApiService gitHubApiService;

    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;

    private OwnedReposFragment ownedOwnedReposFragment;
    private StarredReposFragment starredReposFragment;

    private ImageView avatarImageView;
    private TextView usernameTV;
    private Button followersBtn;
    private Button followingBtn;

    private LoadAvatarTask loadAvatarTask;

    //SQLITE
    private User cacheUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton search = (FloatingActionButton) findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                navigateToUserSearchScreen();
            }
        });

        this.setTitle(getString(R.string.userProfileTitle));

        this.jsonUserFollowersList = new ArrayList<>();
        this.jsonUserFollowingList = new ArrayList<>();

        this.bundle = new Bundle();
        this.intent = getIntent();
        this.jsonUser = (JsonUser) this.intent.getSerializableExtra(getString(R.string.userObjectKey));

        //SQLITE--------------------------------------------

        this.cacheUser = new User();
        this.setCacheUser();

        // -------------------------------------------------

        this.createProgressDialog();

        this.createRetrofit();
        this.createGitHubService();

        this.findTextViewsById();
        this.findButtonById();
        this.findImageViewById();

        this.fragmentManager = getFragmentManager();

        this.setImageViewData();
        this.setTextViewsData();


        if (AndroidUtil.isNetworkAvailable(this)) {
            this.runGetUserFollowersService();
            this.runGetUserFollowingService();
        } else {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);

        return true;
    }

    @Override
    public void onBackPressed() {
        AndroidUtil.createAlertDialog(this);
    }

    public void logout(MenuItem menuItem) {
        AndroidUtil.createAlertDialog(this);
    }

    public void ownedFragment(View view) {

        if (starredReposFragment != null) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(starredReposFragment);
            fragmentTransaction.commit();
            starredReposFragment = null;
        }

        if (ownedOwnedReposFragment == null) {
            this.setRepoBundle();

            fragmentTransaction = fragmentManager.beginTransaction();
            ownedOwnedReposFragment = new OwnedReposFragment();
            ownedOwnedReposFragment.setArguments(this.bundle);
            fragmentTransaction.add(R.id.ownedContainer, ownedOwnedReposFragment);
            fragmentTransaction.commit();
        }


    }

    public void starredFragment(View view) {

        if (ownedOwnedReposFragment != null) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(ownedOwnedReposFragment);
            fragmentTransaction.commit();
            ownedOwnedReposFragment = null;
        }

        if (starredReposFragment == null) {
            this.setRepoBundle();

            fragmentTransaction = fragmentManager.beginTransaction();
            starredReposFragment = new StarredReposFragment();
            starredReposFragment.setArguments(this.bundle);
            fragmentTransaction.add(R.id.ownedContainer, starredReposFragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void avatarBitmapResponse(Bitmap bitmap) {
        this.avatarImageView.setImageBitmap(bitmap);

        if (this.jsonUser != null) {
            this.persistUserToSQLite(this.jsonUser, bitmap);
        }
    }

    private void setRepoBundle() {
        this.bundle.putString(getString(R.string.authPasswordKey), this.intent.getStringExtra(getString(R.string.authPasswordKey)));
        this.bundle.putString(getString(R.string.authLoginKey), this.intent.getStringExtra(getString(R.string.authLoginKey)));
        this.bundle.putSerializable(getString(R.string.userObjectKey), this.jsonUser);
    }

    private void findTextViewsById() {
        this.usernameTV = (TextView) findViewById(R.id.username);

    }

    private void findButtonById() {
        this.followersBtn = (Button) findViewById(R.id.followers);
        this.followingBtn = (Button) findViewById(R.id.following);
    }

    private void findImageViewById() {
        this.avatarImageView = (ImageView) findViewById(R.id.avatar);
    }

    private void setTextViewsData() {

        this.usernameTV.setText(this.jsonUser.getLogin());
        this.followersBtn.setText((getString(R.string.followersTitle, this.jsonUser.getFollowers())));
        this.followingBtn.setText((getString(R.string.followingTitle, this.jsonUser.getFollowing())));

    }

    private void setupLoadAvatarTask(String url) {
        loadAvatarTask = new LoadAvatarTask(this, getString(R.string.loadingData));
        loadAvatarTask.setAvatarBitmapResponse(this);
        loadAvatarTask.execute(url);
    }

    private void setImageViewData() {

        this.setupLoadAvatarTask(this.jsonUser.getAvatarUrl());

    }

    public void navigateToUserFollowersSearchScreen(View view) {

        Bundle userBundle = new Bundle();
        userBundle.putSerializable(getString(R.string.userListKey), (Serializable) this.jsonUserFollowersList);
        userBundle.putString(getString(R.string.authPasswordKey), this.intent.getStringExtra(getString(R.string.authPasswordKey)));
        userBundle.putString(getString(R.string.authLoginKey), this.intent.getStringExtra(getString(R.string.authLoginKey)));
        userBundle.putBoolean(getString(R.string.isForSearchOnly), false);

        Intent userSearchIntent = new Intent(this, UserSearchActivity.class);
        userSearchIntent.putExtras(userBundle);
        startActivity(userSearchIntent);
    }

    public void navigateToUserFollowingSearchScreen(View view) {

        Bundle userBundle = new Bundle();
        userBundle.putSerializable(getString(R.string.userListKey), (Serializable) this.jsonUserFollowingList);
        userBundle.putString(getString(R.string.authPasswordKey), this.intent.getStringExtra(getString(R.string.authPasswordKey)));
        userBundle.putString(getString(R.string.authLoginKey), this.intent.getStringExtra(getString(R.string.authLoginKey)));
        userBundle.putBoolean(getString(R.string.isForSearchOnly), false);

        Intent userSearchIntent = new Intent(this, UserSearchActivity.class);
        userSearchIntent.putExtras(userBundle);
        startActivity(userSearchIntent);
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

    private void runGetUserFollowersService() {

        if (!this.progressDialog.isShowing()) {
            this.showProgressDialog();
        }

        Call<List<JsonUser>> followers = this.gitHubApiService.getUserFollowers(this.jsonUser.getLogin());

        this.getUserFollowersResponse(followers);
    }

    private void runGetUserFollowingService() {

        if (!this.progressDialog.isShowing()) {
            this.showProgressDialog();
        }

        Call<List<JsonUser>> following = this.gitHubApiService.getUserFollowing(this.jsonUser.getLogin());

        this.getUserFollowingResponse(following);
    }

    private void getUserFollowersResponse(Call<List<JsonUser>> userFollowersResponse) {

        userFollowersResponse.enqueue(new Callback<List<JsonUser>>() {
            @Override
            public void onResponse(Response<List<JsonUser>> response, Retrofit retrofit) {
                int statusCode = response.code();

                if (progressDialog.isShowing()) {
                    dissmissProgressDialog();
                }

                if (statusCode == 200) {
                    jsonUserFollowersList = response.body();

                    setFollowersView(jsonUserFollowersList.size());

                    //SQLite implementation
                    setFollowersListForSQLite(jsonUserFollowersList);
                    //--------------------
                } else if (statusCode == 403) {
                    Toast.makeText(UserProfileActivity.this, R.string.limitExceeded, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Throwable t) {

                if (progressDialog.isShowing()) {
                    dissmissProgressDialog();
                }

                Toast.makeText(UserProfileActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }

    private void getUserFollowingResponse(Call<List<JsonUser>> userFollowingResponse) {

        userFollowingResponse.enqueue(new Callback<List<JsonUser>>() {
            @Override
            public void onResponse(Response<List<JsonUser>> response, Retrofit retrofit) {
                int statusCode = response.code();

                if (progressDialog.isShowing()) {
                    dissmissProgressDialog();
                }

                if (statusCode == 200) {
                    jsonUserFollowingList = response.body();

                    setFollowingView(jsonUserFollowingList.size());

                    //SQLite implementation
                    setFollowingListForSQLite(jsonUserFollowingList);
                    //--------------------
                } else if (statusCode == 403) {
                    Toast.makeText(UserProfileActivity.this, R.string.limitExceeded, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Throwable t) {

                if (progressDialog.isShowing()) {
                    dissmissProgressDialog();
                }

                Toast.makeText(UserProfileActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }

    private void setFollowersView(int followersSize) {
        this.followersBtn.setText((getString(R.string.followersTitle, String.format("%3d", followersSize))));
    }

    private void setFollowingView(int followingSize) {
        this.followingBtn.setText((getString(R.string.followingTitle, String.format("%3d", followingSize))));
    }

    // SQLITE IMPLEMENTATION

    private void setCacheUser() {
        this.cacheUser.setId(this.jsonUser.getId());
    }

    private void persistUserToSQLite(JsonUser jsonUser, Bitmap bitmap) {

        User cacheUser = new User();
        cacheUser.setId(jsonUser.getId());
        cacheUser.setLogin(jsonUser.getLogin());
        cacheUser.setAvatar(AndroidUtil.convertBitmapToByteArray(bitmap));

        this.setupPersistUserTask(cacheUser);

    }

    private void setupPersistUserTask(User cacheUser) {
        PersistUserTask persistUserTask = new PersistUserTask(this);
        persistUserTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, cacheUser);

    }

    private void setFollowersListForSQLite(List<JsonUser> userFollowersList) {

        List<User> cacheFollowersList = new ArrayList<>();

        for (JsonUser jsonUser : userFollowersList) {
            User cacheFollower = new User();
            cacheFollower.setId(jsonUser.getId());
            cacheFollower.setLogin(jsonUser.getLogin());

            cacheFollowersList.add(cacheFollower);
        }

        this.setupPersistFollowersTask(cacheFollowersList, this.cacheUser);

    }

    private void setFollowingListForSQLite(List<JsonUser> userFollowingList) {

        List<User> cacheFollowingList = new ArrayList<>();

        for (JsonUser jsonUser : userFollowingList) {
            User cacheFollowing = new User();
            cacheFollowing.setId(jsonUser.getId());
            cacheFollowing.setLogin(jsonUser.getLogin());

            cacheFollowingList.add(cacheFollowing);
        }

        this.setupPersistFollowingTask(cacheFollowingList);
    }

    private void setupPersistFollowersTask(List<User> followerList, User followingUser) {

        PersistFollowersTask persistFollowersTask = new PersistFollowersTask(this);
        persistFollowersTask.setFollowersList(followerList);
        persistFollowersTask.setFollowingUser(followingUser);
        persistFollowersTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void setupPersistFollowingTask(List<User> followingList) {

        PersistFollowingTask persistFollowingTask = new PersistFollowingTask(this);
        persistFollowingTask.setFollowingList(followingList);
        persistFollowingTask.setFollowerUser(this.cacheUser);
        persistFollowingTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

}
