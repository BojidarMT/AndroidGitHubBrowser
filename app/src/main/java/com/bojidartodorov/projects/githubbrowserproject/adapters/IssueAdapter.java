package com.bojidartodorov.projects.githubbrowserproject.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bojidartodorov.projects.githubbrowserproject.R;
import com.bojidartodorov.projects.githubbrowserproject.model.json_model.JsonIssue;

import java.util.List;

/**
 * Created by Bojidar on 22.11.2015 г..
 */
public class IssueAdapter extends ArrayAdapter<JsonIssue> {

    private Context context;
    private List<JsonIssue> objects;

    public IssueAdapter(Context context, int resource, List<JsonIssue> objects) {
        super(context, resource, objects);

        this.context = context;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        JsonIssue jsonIssue = objects.get(position);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.item_issue, null);

        TextView issueTitle = (TextView) view.findViewById(R.id.tvTitle);

        TextView issueDate = (TextView) view.findViewById(R.id.tvDate);

        issueTitle.setText(jsonIssue.getTitle());

        issueDate.setText(jsonIssue.getCreatedAt());

        return view;
    }
}
