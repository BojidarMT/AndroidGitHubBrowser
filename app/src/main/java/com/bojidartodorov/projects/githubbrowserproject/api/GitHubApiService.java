package com.bojidartodorov.projects.githubbrowserproject.api;

import com.bojidartodorov.projects.githubbrowserproject.model.json_model.JsonRepository;
import com.bojidartodorov.projects.githubbrowserproject.model.json_model.JsonUser;
import com.bojidartodorov.projects.githubbrowserproject.model.json_model.JsonIssue;
import com.squareup.okhttp.Response;

import java.util.List;

import retrofit.Call;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Bojidar on 27.11.2015 г..
 */
public interface GitHubApiService {

    @GET("user")
    Call<JsonUser> basicAuth(@Header("Authorization") String authorization);

    @GET("users/{user}/repos")
    Call<List<JsonRepository>> getOwnedRepositories(@Path("user") String userName);

    @GET("users/{user}/starred")
    Call<List<JsonRepository>> getStarredRepositories(@Path("user") String userName);

    @GET("repos/{user}/{repository}/commits")
    Call<List<JsonRepository>> getCommits(@Path("user") String userName, @Path("repository") String repository);

    @GET("repos/{user}/{repository}/branches")
    Call<List<JsonRepository>> getBranches(@Path("user") String userName, @Path("repository") String repository);

    @GET("repos/{user}/{repository}/releases")
    Call<List<JsonRepository>> getReleases(@Path("user") String userName, @Path("repository") String repository);

    @GET("repos/{user}/{repository}/contributors")
    Call<List<JsonUser>> getContributors(@Path("user") String userName, @Path("repository") String repository);

    @GET("user/starred/{user}/{repository}")
    Call<Response> checkRepoIsStarred(@Header("Authorization") String authorization, @Path("user") String userName, @Path("repository") String repository);

    @PUT("user/starred/{user}/{repository}")
    Call<Response> starRepo(@Header("Authorization") String authorization, @Header("Content-Length") String contentLength, @Path("user") String userName, @Path("repository") String repository);

    @DELETE("user/starred/{user}/{repository}")
    Call<Response> ustarRepo(@Header("Authorization") String authorization, @Header("Content-Length") String contentLength, @Path("user") String userName, @Path("repository") String repository);

    @GET("repos/{user}/{repository}/languages")
    Call<String> getLanguages(@Path("user") String userName, @Path("repository") String repository);

    @GET("repos/{user}/{repository}/issues")
    Call<List<JsonIssue>> getRepoIssues(@Path("user") String userName, @Path("repository") String repository, @Query("state") String state);

    @GET("search/users")
    Call<String> getUsers(@Query("q") String username, @Query("in") String searchCriteria);

    @GET("users/{user}/followers")
    Call<List<JsonUser>> getUserFollowers(@Path("user") String userName);

    @GET("users/{user}/following")
    Call<List<JsonUser>> getUserFollowing(@Path("user") String userName);
}
