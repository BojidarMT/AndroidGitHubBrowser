package com.bojidartodorov.projects.githubbrowserproject.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Collection;

/**
 * Created by Bojidar on 29.11.2015 г..
 */

@DatabaseTable(tableName = "Repository")
public class Repository {

    @DatabaseField(columnName = "Id", canBeNull = false, id = true, unique = true)
    private long id;

    @DatabaseField(columnName = "Name")
    private String name;

    @DatabaseField(columnName = "IsStarred")
    private boolean isStarred;

    @DatabaseField(columnName = "CommitsCount")
    private int commitsCount;

    @DatabaseField(columnName = "BranchesCount")
    private int branchesCount;

    @DatabaseField(columnName = "ReleasesCount")
    private int releasesCount;

    @DatabaseField(columnName = "StarsCount")
    private int starsCount;

    @DatabaseField(columnName = "ForksCount")
    private int forksCount;

    @DatabaseField(columnName = "IsOwned")
    private boolean isOwned;

    @DatabaseField(foreign = true, columnName = "User_Id")
    private User user;

    @ForeignCollectionField(eager = true)
    private Collection<Contributor> contributorsCollection;

    @ForeignCollectionField(eager = true)
    private Collection<Issue> issuesCollection;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isStarred() {
        return isStarred;
    }

    public void setIsStarred(boolean isStarred) {
        this.isStarred = isStarred;
    }

    public int getCommitsCount() {
        return commitsCount;
    }

    public void setCommitsCount(int commitsCount) {
        this.commitsCount = commitsCount;
    }

    public int getBranchesCount() {
        return branchesCount;
    }

    public void setBranchesCount(int branchesCount) {
        this.branchesCount = branchesCount;
    }

    public int getReleasesCount() {
        return releasesCount;
    }

    public void setReleasesCount(int releasesCount) {
        this.releasesCount = releasesCount;
    }

    public int getStarsCount() {
        return starsCount;
    }

    public void setStarsCount(int starsCount) {
        this.starsCount = starsCount;
    }

    public int getForksCount() {
        return forksCount;
    }

    public void setForksCount(int forksCount) {
        this.forksCount = forksCount;
    }

    public boolean isOwned() {
        return isOwned;
    }

    public void setIsOwned(boolean isOwned) {
        this.isOwned = isOwned;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Collection<Contributor> getContributorsCollection() {
        return contributorsCollection;
    }

    public void setContributorsCollection(Collection<Contributor> contributorsCollection) {
        this.contributorsCollection = contributorsCollection;
    }

    public Collection<Issue> getIssuesCollection() {
        return issuesCollection;
    }

    public void setIssuesCollection(Collection<Issue> issuesCollection) {
        this.issuesCollection = issuesCollection;
    }
}
