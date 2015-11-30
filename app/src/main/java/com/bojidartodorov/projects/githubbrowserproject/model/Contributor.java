package com.bojidartodorov.projects.githubbrowserproject.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Bojidar on 29.11.2015 г..
 */

@DatabaseTable(tableName = "Contributor")
public class Contributor {

    @DatabaseField(columnName = "Id", canBeNull = false, generatedId = true)
    private long id;

    @DatabaseField(columnName = "ContributorUserId", canBeNull = false, unique = false)
    private long contributorUserId;

    @DatabaseField(columnName = "RepositoryId", canBeNull = false, unique = false, foreign = true)
    private Repository repository;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getContributorUserId() {
        return contributorUserId;
    }

    public void setContributorUserId(long contributorUserId) {
        this.contributorUserId = contributorUserId;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }
}
