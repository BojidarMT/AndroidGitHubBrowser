package com.bojidartodorov.projects.githubbrowserproject.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bojidartodorov.projects.githubbrowserproject.R;
import com.bojidartodorov.projects.githubbrowserproject.model.json_model.JsonUser;

import java.util.List;

/**
 * Created by Bojidar on 28.11.2015 г..
 */
public class UserAdapter extends ArrayAdapter<JsonUser> {

    private Context context;
    private List<JsonUser> objects;

    public UserAdapter(Context context, int resource, List<JsonUser> objects) {
        super(context, resource, objects);

        this.context = context;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        JsonUser jsonUser = objects.get(position);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.item_user, null);

        TextView username = (TextView) view.findViewById(R.id.username);

        username.setText(jsonUser.getLogin());

        return view;
    }
}
