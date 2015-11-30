package com.bojidartodorov.projects.githubbrowserproject.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bojidartodorov.projects.githubbrowserproject.R;
import com.bojidartodorov.projects.githubbrowserproject.api.GitHubApiService;
import com.bojidartodorov.projects.githubbrowserproject.model.json_model.JsonUser;
import com.bojidartodorov.projects.githubbrowserproject.util.AndroidUtil;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class LoginActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    private Bundle bundle;

    private Button loginButton;
    private EditText usernameInput;
    private EditText passwordInput;

    private Retrofit retrofit;
    private GitHubApiService gitHubApiService;
    private JsonUser jsonUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.bundle = new Bundle();

        this.createProgressDialog();

        this.findButtonViewById();
        this.findTextViewInputsById();

        this.createRetrofit();
        this.createGitHubService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    public void login(View view) {

        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();
        boolean isLoginInputCorrect = this.checkLoginInput(username, password);

        if (isLoginInputCorrect) {
            if (AndroidUtil.isNetworkAvailable(this)) {
                this.runBasicAuthService();
            } else {
                Toast.makeText(this, R.string.noInternetConnectionError, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, R.string.pleaseFillFields, Toast.LENGTH_LONG).show();
        }
    }

    private void runBasicAuthService() {

        String authorization = AndroidUtil.createAuthorizationString(this.usernameInput.getText().toString(), this.passwordInput.getText().toString());

        if (!this.progressDialog.isShowing()) {
            this.showProgressDialog();
        }

        Call<JsonUser> user = this.gitHubApiService.basicAuth(authorization);

        this.getBasicAuthResponse(user);
    }

    private void createProgressDialog() {
        this.progressDialog = AndroidUtil.createProgressDialog("Logging in...", this);
    }

    private void showProgressDialog() {
        this.progressDialog.show();
    }

    private void dissmissProgressDialog() {
        this.progressDialog.dismiss();
    }

    private void getBasicAuthResponse(Call<JsonUser> userResponse) {

        userResponse.enqueue(new Callback<JsonUser>() {
            @Override
            public void onResponse(Response<JsonUser> response, Retrofit retrofit) {
                int statusCode = response.code();

                if (progressDialog.isShowing()) {
                    dissmissProgressDialog();
                }

                if (statusCode == 200) {
                    jsonUser = response.body();

                    navigateToUserProfileScreen();

                } else if (statusCode == 401) {

                    Toast.makeText(LoginActivity.this, "Wrong username and/or password!", Toast.LENGTH_LONG).show();
                } else if (statusCode == 403) {
                    Toast.makeText(LoginActivity.this, R.string.limitExceeded, Toast.LENGTH_LONG).show();
                }


            }

            @Override
            public void onFailure(Throwable t) {

                if (progressDialog.isShowing()) {
                    dissmissProgressDialog();
                }

                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void navigateToUserProfileScreen() {

        this.setBundleData();

        Intent userProfileIntent = new Intent(this, UserProfileActivity.class);
        userProfileIntent.putExtras(this.bundle);
        startActivity(userProfileIntent);
    }

    private boolean checkLoginInput(String username, String password) {

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            return false;
        } else {
            return true;
        }
    }

    public void exit(View view) {
        this.exitLogin();
    }

    private void exitLogin() {
        this.finish();
    }

    private void findButtonViewById() {
        loginButton = (Button) this.findViewById(R.id.loginBtn);
    }

    private void findTextViewInputsById() {
        usernameInput = (EditText) this.findViewById(R.id.usernameInput);
        passwordInput = (EditText) this.findViewById(R.id.passwordInput);
    }

    private void setBundleData() {
        this.bundle.putString(getString(R.string.authPasswordKey), this.passwordInput.getText().toString());
        this.bundle.putString(getString(R.string.authLoginKey), this.usernameInput.getText().toString());
        this.bundle.putSerializable(getString(R.string.userObjectKey), this.jsonUser);
    }
}
