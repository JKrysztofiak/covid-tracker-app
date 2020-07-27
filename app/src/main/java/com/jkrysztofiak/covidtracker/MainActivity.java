package com.jkrysztofiak.covidtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    TextView globalTotal;
    TextView globalDeaths;
    FloatingActionButton addButton;

    String jsonResponse;

    Set<String> countries;

    SharedPreferences  mPrefs;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPrefs = getPreferences(MODE_PRIVATE);



        listView = (ListView) findViewById(R.id.list_view);
        globalTotal = (TextView) findViewById(R.id.total_cases_number);
        globalDeaths = (TextView) findViewById(R.id.total_deaths_number);
        addButton = (FloatingActionButton) findViewById(R.id.add_location_button);

        //TODO: Read from storage\
        countries = mPrefs.getStringSet("countrySet",null);
        if(countries==null){
            countries = new HashSet<>();
        }
        Log.w("YO!", "Countries retrieved: "+countries.size());

        updateUI();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countries.add("Poland");
                updateUI();
            }
        });

    }

    public void updateUI(){
        OkHttpClient client = new OkHttpClient();

        String url = "https://api.covid19api.com/summary";

        Request request = new Request.Builder()
                .url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    Log.w("YO!", "GET Updated");
                    jsonResponse = response.body().string();
                    try {
                        final JSONObject object = new JSONObject(jsonResponse);

                        Log.w("YO!", "JSON ok");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    int globalTotalCount = object.getJSONObject("Global").getInt("TotalConfirmed");
                                    int globalDeathsCount = object.getJSONObject("Global").getInt("TotalDeaths");

                                    DecimalFormat df = new DecimalFormat("#,###,###");

                                    globalTotal.setText(df.format(globalTotalCount).replaceAll(","," "));
                                    globalDeaths.setText(df.format(globalDeathsCount).replaceAll(","," "));

                                    ArrayList<Stats> statsList = new ArrayList<>();

                                    JSONArray countriesArray = object.getJSONArray("Countries");

                                    for(int i=0; i<countriesArray.length(); i++){
                                        String currCountry = countriesArray.getJSONObject(i).getString("Country");
                                        if(countries.contains(currCountry)){
                                            int newCases = countriesArray.getJSONObject(i).getInt("NewConfirmed");
                                            int totalCases = countriesArray.getJSONObject(i).getInt("TotalConfirmed");
                                            String date = countriesArray.getJSONObject(i).getString("Date");
                                            Stats resp = new Stats(currCountry, newCases, totalCases, date);
                                            statsList.add(resp);
                                        }
                                    }

                                    StatsListAdapter adapter = new StatsListAdapter(MainActivity.this, R.layout.row, statsList);
                                    listView.setAdapter(adapter);

                                    Log.w("YO!", "Layout ok");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences.Editor prefsEditor = mPrefs.edit();

        prefsEditor.putStringSet("countrySet",countries);
        prefsEditor.apply();
    }
}

