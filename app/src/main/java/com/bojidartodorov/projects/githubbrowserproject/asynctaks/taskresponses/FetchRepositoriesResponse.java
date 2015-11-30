package com.bojidartodorov.projects.githubbrowserproject.asynctaks.taskresponses;

import com.bojidartodorov.projects.githubbrowserproject.model.Repository;

import java.util.List;

/**
 * Created by Bojidar on 30.11.2015 г..
 */
public interface FetchRepositoriesResponse {

    void repositoriesList(List<Repository> repositoryList);
}
