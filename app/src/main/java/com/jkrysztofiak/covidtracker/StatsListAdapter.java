package com.jkrysztofiak.covidtracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class StatsListAdapter extends ArrayAdapter<Stats> {

    private Context context;
    private int resource;

    public StatsListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Stats> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        String country = getItem(position).getCountry();
//        String newCases = String.valueOf(getItem(position).getNewCases());
//        String totalCases = String.valueOf(getItem(position).getTotalCases());

        int newCases = getItem(position).getNewCases();
        int totalCases = getItem(position).getTotalCases();

        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(resource, parent, false);

        TextView countryLabel = (TextView) convertView.findViewById(R.id.country_label);
        TextView newCasesLabel = (TextView) convertView.findViewById(R.id.new_cases_number);
        TextView totalCasesLabel = (TextView) convertView.findViewById(R.id.total_cases_number);
        TextView date = (TextView) convertView.findViewById(R.id.date);

        DecimalFormat df = new DecimalFormat("#,###,###");

        countryLabel.setText(country);
        newCasesLabel.setText(df.format(newCases).replaceAll(","," "));
        totalCasesLabel.setText(df.format(totalCases).replaceAll(","," "));
        date.setText(getItem(position).getDate().split("T")[0]);

        return convertView;
    }
}
