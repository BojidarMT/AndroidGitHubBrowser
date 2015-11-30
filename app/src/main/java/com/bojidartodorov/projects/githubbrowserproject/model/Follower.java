package com.bojidartodorov.projects.githubbrowserproject.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


/**
 * Created by Bojidar on 29.11.2015 г..
 */

@DatabaseTable(tableName = "Follower")
public class Follower {

    @DatabaseField(columnName = "Id", canBeNull = false, unique = false, generatedId = true)
    private long id;

    @DatabaseField(columnName = "FollowerId", canBeNull = false, unique = false)
    private long followerUserId;

    @DatabaseField(columnName = "FollowingId", canBeNull = false)
    private long followingUserId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getFollowerUserId() {
        return followerUserId;
    }

    public void setFollowerUserId(long followerUserId) {
        this.followerUserId = followerUserId;
    }

    public long getFollowingUserId() {
        return followingUserId;
    }

    public void setFollowingUserId(long followingUserId) {
        this.followingUserId = followingUserId;
    }
}
