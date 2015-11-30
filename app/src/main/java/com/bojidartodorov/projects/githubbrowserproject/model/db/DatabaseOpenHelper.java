package com.bojidartodorov.projects.githubbrowserproject.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bojidartodorov.projects.githubbrowserproject.model.Contributor;
import com.bojidartodorov.projects.githubbrowserproject.model.Follower;
import com.bojidartodorov.projects.githubbrowserproject.model.Issue;
import com.bojidartodorov.projects.githubbrowserproject.model.Repository;
import com.bojidartodorov.projects.githubbrowserproject.model.User;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by Bojidar on 29.11.2015 г..
 */
public class DatabaseOpenHelper extends OrmLiteSqliteOpenHelper {

    public static final String DATABASE_NAME = "githubbrowser.db";
    public static final int DATABASE_VERSION = 5;

    private RuntimeExceptionDao<User, Long> userRuntimeDao = null;
    private RuntimeExceptionDao<Repository, Long> repositoryRuntimeDao = null;
    private RuntimeExceptionDao<Issue, Long> issueRuntimeDao = null;
    private RuntimeExceptionDao<Follower, Long> followerRuntimeDao = null;
    private RuntimeExceptionDao<Contributor, Long> contributorRuntimeDao = null;

    private Context context;

    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {

        try {
            TableUtils.createTable(connectionSource, User.class);
            TableUtils.createTable(connectionSource, Repository.class);
            TableUtils.createTable(connectionSource, Issue.class);
            TableUtils.createTable(connectionSource, Follower.class);
            TableUtils.createTable(connectionSource, Contributor.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, User.class, true);
            TableUtils.dropTable(connectionSource, Repository.class, true);
            TableUtils.dropTable(connectionSource, Issue.class, true);
            TableUtils.dropTable(connectionSource, Follower.class, true);
            TableUtils.dropTable(connectionSource, Contributor.class, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public RuntimeExceptionDao<User, Long> getUserRuntimeDao() {
        if (this.userRuntimeDao == null) {
            this.userRuntimeDao = getRuntimeExceptionDao(User.class);
        }

        return this.userRuntimeDao;
    }

    public RuntimeExceptionDao<Repository, Long> getRepositoryRuntimeDao() {
        if (this.repositoryRuntimeDao == null) {
            this.repositoryRuntimeDao = getRuntimeExceptionDao(Repository.class);
        }

        return this.repositoryRuntimeDao;
    }

    public RuntimeExceptionDao<Issue, Long> getIssueRuntimeDao() {
        if (this.issueRuntimeDao == null) {
            this.issueRuntimeDao = getRuntimeExceptionDao(Issue.class);
        }

        return this.issueRuntimeDao;
    }

    public RuntimeExceptionDao<Follower, Long> getFollowerRuntimeDao() {
        if (this.followerRuntimeDao == null) {
            this.followerRuntimeDao = getRuntimeExceptionDao(Follower.class);
        }

        return this.followerRuntimeDao;
    }

    public RuntimeExceptionDao<Contributor, Long> getContributorRuntimeDao() {
        if (this.contributorRuntimeDao == null) {
            this.contributorRuntimeDao = getRuntimeExceptionDao(Contributor.class);
        }

        return this.contributorRuntimeDao;
    }
}
