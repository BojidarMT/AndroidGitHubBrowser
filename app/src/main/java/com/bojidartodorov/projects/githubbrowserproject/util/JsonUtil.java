package com.bojidartodorov.projects.githubbrowserproject.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Bojidar on 24.11.2015 г..
 */
public class JsonUtil {

    public static JSONArray parseToJsonArray(String data) {

        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(data);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }

    public static JSONObject parseToJsonObject(String data) {

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(data);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public static int getJsonCount(String data) {

        JSONArray jsonArray = JsonUtil.parseToJsonArray(data);

        return jsonArray.length();
    }

    public static JSONArray convertToUsersJsonArray(String data) {

        JSONArray usersJson = null;

        try {
            JSONObject jsonObject = new JSONObject(data);

            usersJson = jsonObject.getJSONArray("items");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return usersJson;
    }
}
