package com.bojidartodorov.projects.githubbrowserproject.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bojidartodorov.projects.githubbrowserproject.R;
import com.bojidartodorov.projects.githubbrowserproject.model.Repository;
import com.bojidartodorov.projects.githubbrowserproject.model.json_model.JsonRepository;

import java.util.List;

/**
 * Created by Bojidar on 22.11.2015 г..
 */
public class RepositoryAdapter extends ArrayAdapter<Repository> {

    private Context context;
    private List<Repository> objects;

    public RepositoryAdapter(Context context, int resource, List<Repository> objects) {
        super(context, resource, objects);

        this.context = context;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Repository repository = objects.get(position);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.item_repository, null);

        TextView repoNameTextView = (TextView) view.findViewById(R.id.repositoryName);

        repoNameTextView.setText(repository.getName());

        return view;
    }
}
