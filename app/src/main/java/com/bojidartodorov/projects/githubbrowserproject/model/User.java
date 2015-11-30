package com.bojidartodorov.projects.githubbrowserproject.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Collection;

/**
 * Created by Bojidar on 29.11.2015 г..
 */
@DatabaseTable(tableName = "User")
public class User implements Serializable {

    @DatabaseField(columnName = "Id", canBeNull = false, id = true, unique = true)
    private long id;

    @DatabaseField(columnName = "Login")
    private String login;

    @DatabaseField(columnName = "Avatar", dataType = DataType.BYTE_ARRAY)
    private byte[] avatar;

    @ForeignCollectionField(eager = true)
    private Collection<Repository> repositoriesCollection;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }


    public Collection<Repository> getRepositoriesCollection() {
        return repositoriesCollection;
    }

    public void setRepositoriesCollection(Collection<Repository> repositoriesCollection) {
        this.repositoriesCollection = repositoriesCollection;
    }
}
