package com.bojidartodorov.projects.githubbrowserproject.util;

import com.bojidartodorov.projects.githubbrowserproject.model.json_model.JsonUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by Bojidar on 28.11.2015 г..
 */
public class GsonUtil {

    public static List<JsonUser> convertJsonToUserObject(String data) {

        Gson gson = new Gson();

        List<JsonUser> jsonUserList = gson.fromJson(data, new TypeToken<List<JsonUser>>() {
        }.getType());

        return jsonUserList;
    }
}
