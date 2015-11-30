package com.bojidartodorov.projects.githubbrowserproject.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Bojidar on 29.11.2015 г..
 */

@DatabaseTable(tableName = "Issue")
public class Issue {

    @DatabaseField(columnName = "Id", canBeNull = false, id = true ,unique = true)
    private long id;

    @DatabaseField(columnName = "Title")
    private String title;

    @DatabaseField(columnName = "CreatedAt")
    private String createdAt;

    @DatabaseField(columnName = "State")
    private String state;

    @DatabaseField(foreign = true, columnName = "Repository_Id")
    private Repository repository;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }
}
