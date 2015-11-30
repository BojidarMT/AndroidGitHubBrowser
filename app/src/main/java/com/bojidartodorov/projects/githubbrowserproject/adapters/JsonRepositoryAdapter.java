package com.bojidartodorov.projects.githubbrowserproject.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bojidartodorov.projects.githubbrowserproject.R;
import com.bojidartodorov.projects.githubbrowserproject.model.json_model.JsonRepository;

import java.util.List;

/**
 * Created by Bojidar on 22.11.2015 г..
 */
public class JsonRepositoryAdapter extends ArrayAdapter<JsonRepository> {

    private Context context;
    private List<JsonRepository> objects;

    public JsonRepositoryAdapter(Context context, int resource, List<JsonRepository> objects) {
        super(context, resource, objects);

        this.context = context;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        JsonRepository jsonRepository = objects.get(position);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.item_repository, null);

        TextView repoNameTextView = (TextView) view.findViewById(R.id.repositoryName);

        repoNameTextView.setText(jsonRepository.getName());

        return view;
    }
}
