package com.bojidartodorov.projects.githubbrowserproject.util;

import android.content.Context;

import com.bojidartodorov.projects.githubbrowserproject.model.Contributor;
import com.bojidartodorov.projects.githubbrowserproject.model.Follower;
import com.bojidartodorov.projects.githubbrowserproject.model.Issue;
import com.bojidartodorov.projects.githubbrowserproject.model.Repository;
import com.bojidartodorov.projects.githubbrowserproject.model.User;
import com.bojidartodorov.projects.githubbrowserproject.model.db.datasource.SQLiteDatasource;

import java.util.List;

/**
 * Created by Bojidar on 29.11.2015 г..
 */
public class SQLitePersistUtil {

    public static void persistUser(Context context, User user) {
        SQLiteDatasource sqLiteDatasource = new SQLiteDatasource(context);

        sqLiteDatasource.persistUser(user);
    }

    public static void persistFollowers(Context context, List<User> followerList, User followingUser) {
        SQLiteDatasource sqLiteDatasource = new SQLiteDatasource(context);

        sqLiteDatasource.persistFollowers(followerList, followingUser);
    }

    public static void persistFollowing(Context context, List<User> followingList, User followerUser) {
        SQLiteDatasource sqLiteDatasource = new SQLiteDatasource(context);

        sqLiteDatasource.persistFollowing(followingList, followerUser);
    }

    public static void persistRepository(Context context, Repository repository) {
        SQLiteDatasource sqLiteDatasource = new SQLiteDatasource(context);

        sqLiteDatasource.persistRepository(repository);
    }

    public static void persistContributor(Context context, List<User> userContributorList, Repository repository) {
        SQLiteDatasource sqLiteDatasource = new SQLiteDatasource(context);

        sqLiteDatasource.persistContributors(userContributorList, repository);
    }

    public static void persistIssues(Context context, List<Issue> issueList) {
        SQLiteDatasource sqLiteDatasource = new SQLiteDatasource(context);

        sqLiteDatasource.persistIssues(issueList);
    }

    public static List<Repository> fetchOwnedRepositories(Context context, long userId) {
        SQLiteDatasource sqLiteDatasource = new SQLiteDatasource(context);

        return sqLiteDatasource.fetchUserOwnedRepositories(userId);
    }

    public static List<Repository> fetchStarredRepositories(Context context, long userId) {
        SQLiteDatasource sqLiteDatasource = new SQLiteDatasource(context);

        return sqLiteDatasource.fetchUserStarredRepositories(userId);
    }
}
