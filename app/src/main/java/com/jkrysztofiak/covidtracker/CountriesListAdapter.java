package com.jkrysztofiak.covidtracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CountriesListAdapter extends ArrayAdapter<String> {

    private Context context;
    private int resource;

    public CountriesListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<String> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String country = getItem(position);

        LayoutInflater inflater = LayoutInflater.from(context);
        //TODO: Smother scrolling
        convertView = inflater.inflate(resource, parent, false);

        TextView label = (TextView) convertView.findViewById(R.id.country_label_bottom);

        label.setText(country);
        return convertView;
    }
}
