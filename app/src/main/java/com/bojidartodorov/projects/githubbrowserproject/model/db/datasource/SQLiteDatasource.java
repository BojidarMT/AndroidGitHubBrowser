package com.bojidartodorov.projects.githubbrowserproject.model.db.datasource;

import android.content.Context;

import com.bojidartodorov.projects.githubbrowserproject.model.Contributor;
import com.bojidartodorov.projects.githubbrowserproject.model.Follower;
import com.bojidartodorov.projects.githubbrowserproject.model.Issue;
import com.bojidartodorov.projects.githubbrowserproject.model.Repository;
import com.bojidartodorov.projects.githubbrowserproject.model.User;
import com.bojidartodorov.projects.githubbrowserproject.model.db.DatabaseOpenHelper;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bojidar on 29.11.2015 г..
 */
public class SQLiteDatasource {

    private Context context;
    private DatabaseOpenHelper databaseOpenHelper;
    private RuntimeExceptionDao<User, Long> userDao = null;
    private RuntimeExceptionDao<Follower, Long> followerDao = null;
    private RuntimeExceptionDao<Repository, Long> repositoryDao = null;
    private RuntimeExceptionDao<Contributor, Long> contributorDao = null;
    private RuntimeExceptionDao<Issue, Long> issueDao = null;
    private QueryBuilder<User, Long> userQueryBuilder;
    private QueryBuilder<Follower, Long> followerQueryBuilder;
    private QueryBuilder<Contributor, Long> contributorQueryBuilder;
    private QueryBuilder<Repository, Long> repositoryQueryBuilder;

    public SQLiteDatasource(Context context) {

        this.context = context;

    }

    private void getOpenHelper() {
        if (this.databaseOpenHelper == null) {
            this.databaseOpenHelper = OpenHelperManager.getHelper(context, DatabaseOpenHelper.class);
        }
    }

    private void getUserRuntimeDao() {

        if (this.userDao == null) {
            this.userDao = this.databaseOpenHelper.getUserRuntimeDao();
        }

    }

    private void getIssuesRuntimeDao() {

        if (this.issueDao == null) {
            this.issueDao = this.databaseOpenHelper.getIssueRuntimeDao();
        }

    }

    private void getUserQueryBuilder() {
        if (this.userQueryBuilder == null) {
            this.userQueryBuilder = this.userDao.queryBuilder();
        }
    }

    private void getRepositoryQueryBuilder() {
        if (this.repositoryQueryBuilder == null) {
            this.repositoryQueryBuilder = this.repositoryDao.queryBuilder();
        }
    }

    private void getFollowerQueryBuilder() {
        if (this.followerQueryBuilder == null) {
            this.followerQueryBuilder = this.followerDao.queryBuilder();
        }
    }

    private void getContributorQueryBuilder() {
        if (this.contributorQueryBuilder == null) {
            this.contributorQueryBuilder = this.contributorDao.queryBuilder();
        }
    }

    private void getFollowerRuntimeDao() {

        if (this.followerDao == null) {
            this.followerDao = this.databaseOpenHelper.getFollowerRuntimeDao();
        }

    }

    private void getRepositoryRuntimeDao() {

        if (this.repositoryDao == null) {
            this.repositoryDao = this.databaseOpenHelper.getRepositoryRuntimeDao();
        }

    }

    private void getContributorRuntimeDao() {

        if (this.contributorDao == null) {
            this.contributorDao = this.databaseOpenHelper.getContributorRuntimeDao();
        }

    }

    public void persistUser(User user) {

        this.getOpenHelper();
        this.getUserRuntimeDao();

        this.userDao.createOrUpdate(user);

    }

    public void persistFollowers(List<User> followerList, User followingUser) {

        this.getOpenHelper();
        this.getUserRuntimeDao();
        this.getFollowerRuntimeDao();

        for (User user : followerList) {
            this.userDao.createOrUpdate(user);
            Follower follower = new Follower();
            follower.setFollowerUserId(user.getId());
            follower.setFollowingUserId(followingUser.getId());

            if (this.checkFollowerExists(follower.getFollowerUserId(), follower.getFollowingUserId())) {
                this.updateFollower(follower);
            } else {
                this.createFollower(follower);
            }
        }


    }

    private void updateFollower(Follower follower) {
        this.getOpenHelper();
        this.getFollowerRuntimeDao();

        this.followerDao.update(follower);


    }

    private void createFollower(Follower follower) {
        this.getOpenHelper();
        this.getFollowerRuntimeDao();

        this.followerDao.create(follower);


    }

    private boolean checkFollowerExists(long followerId, long followingUserId) {
        this.getOpenHelper();
        this.getFollowerRuntimeDao();
        this.getFollowerQueryBuilder();

        Follower follower = null;
        try {
            this.followerQueryBuilder.where().eq("followerId", followerId).and().eq("FollowingId", followingUserId);
            follower = followerQueryBuilder.queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (follower == null) {
            return false;
        } else {
            return true;
        }


    }

    private boolean checkFollowingExists(long followerUserId, long followingId) {
        this.getOpenHelper();
        this.getFollowerRuntimeDao();
        this.getFollowerQueryBuilder();

        Follower follower = null;
        try {
            this.followerQueryBuilder.where().eq("followerId", followerUserId).and().eq("FollowingId", followingId);
            follower = followerQueryBuilder.queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (follower == null) {
            return false;
        } else {
            return true;
        }


    }

    public void persistFollowing(List<User> followingList, User followerUser) {

        this.getOpenHelper();
        this.getUserRuntimeDao();
        this.getFollowerRuntimeDao();

        for (User user : followingList) {
            this.userDao.createOrUpdate(user);
            Follower follower = new Follower();
            follower.setFollowerUserId(followerUser.getId());
            follower.setFollowingUserId(user.getId());

            if (this.checkFollowingExists(follower.getFollowerUserId(), follower.getFollowingUserId())) {
                this.updateFollower(follower);
            } else {
                this.createFollower(follower);
            }
        }

    }

    public void persistContributors(List<User> contributorList, Repository repository) {
        this.getOpenHelper();
        this.getUserRuntimeDao();
        this.getContributorRuntimeDao();

        for (User user : contributorList) {
            this.userDao.createOrUpdate(user);

            Contributor contributor = new Contributor();
            contributor.setContributorUserId(user.getId());
            contributor.setRepository(repository);

            if (this.checkContributorExists(contributor.getContributorUserId(), repository)) {
                this.updateContributor(contributor);
            } else {
                this.createContributor(contributor);
            }
        }

    }

    private boolean checkContributorExists(long userContributorId, Repository repository) {
        this.getOpenHelper();
        this.getContributorRuntimeDao();
        this.getContributorQueryBuilder();

        Contributor contributor = null;
        try {
            this.contributorQueryBuilder.where().eq("contributorUserId", userContributorId).and().eq("RepositoryId", repository);
            contributor = contributorQueryBuilder.queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (contributor == null) {
            return false;
        } else {
            return true;
        }


    }

    private void createContributor(Contributor contributor) {
        this.getOpenHelper();
        this.getContributorRuntimeDao();

        this.contributorDao.create(contributor);

    }

    private void updateContributor(Contributor contributor) {
        this.getOpenHelper();
        this.getContributorRuntimeDao();

        this.contributorDao.update(contributor);

    }

    public void persistRepository(Repository repository) {
        this.getOpenHelper();
        this.getRepositoryRuntimeDao();

        this.repositoryDao.createOrUpdate(repository);
    }

    public void persistIssues(List<Issue> issueList) {
        this.getOpenHelper();
        this.getIssuesRuntimeDao();

        for (Issue issue : issueList) {
            this.issueDao.createOrUpdate(issue);
        }
    }

    public List<Repository> fetchUserOwnedRepositories(long userId) {
        this.getOpenHelper();
        this.getRepositoryRuntimeDao();
        this.getRepositoryQueryBuilder();

        List<Repository> repositoryList = null;

        try {
            this.repositoryQueryBuilder.where().eq("User_Id", userId).and().eq("IsOwned", true);
            repositoryList = this.repositoryQueryBuilder.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return repositoryList;
    }

    public List<Repository> fetchUserStarredRepositories(long userId) {
        this.getOpenHelper();
        this.getRepositoryRuntimeDao();
        this.getRepositoryQueryBuilder();

        List<Repository> repositoryList = null;

        try {
            this.repositoryQueryBuilder.where().eq("User_Id", userId).and().eq("IsOwned", false);
            repositoryList = this.repositoryQueryBuilder.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return repositoryList;
    }

}
