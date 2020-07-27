package com.jkrysztofiak.covidtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    ListView cardListView;
    TextView globalTotal;
    TextView globalDeaths;
    FloatingActionButton addButton;

    String jsonResponse;

    Set<String> countries;
    ArrayList<String> countriesNames;

    SharedPreferences  mPrefs;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPrefs = getPreferences(MODE_PRIVATE);

        countriesNames = new ArrayList<>();

        OkHttpClient client = new OkHttpClient();

        String url = "https://api.covid19api.com/countries";

        Request request = new Request.Builder()
                .url(url).build();

        Log.w("YO!", "New request");

        client.newCall(request).enqueue(new Callback() {
                                            @Override
                                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                                Log.w("YO!", "went wrong");

                                            }

                                            @Override
                                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                                if (response.isSuccessful()) {

                                                    try {
                                                        jsonResponse = response.body().string();
                                                        JSONArray arr = new JSONArray(jsonResponse);

                                                        for (int i = 0; i < arr.length(); i++) {
                                                            countriesNames.add(arr.getJSONObject(i).getString("Country"));
                                                        }

                                                        countriesNames.sort(new Comparator<String>() {
                                                            @Override
                                                            public int compare(String s, String t1) {
                                                                return s.compareTo(t1);
                                                            }
                                                        });
                                                        Log.w("YO!", "All countries names downloaded");

                                                    } catch (Exception e) {

                                                        Log.w("YO!", "error");
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        });




        cardListView = (ListView) findViewById(R.id.list_view);
        globalTotal = (TextView) findViewById(R.id.total_cases_number);
        globalDeaths = (TextView) findViewById(R.id.total_deaths_number);
        addButton = (FloatingActionButton) findViewById(R.id.add_location_button);


        //TODO: Read from storage\
        countries = new HashSet<>(mPrefs.getStringSet("countrySet", new HashSet<String>()));
        Log.w("YO!", "Countries retrieved: "+countries.size());

        updateUI();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.w("YO!", "Button clicked");

                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                        MainActivity.this, R.style.BottomSheetDialogTheme
                );
                View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(
                        R.layout.add_country,
                        (LinearLayout)findViewById(R.id.bottomSheet)
                );

                Log.w("YO!", "Inflated");

                ListView listView = (ListView) bottomSheetView.findViewById(R.id.list_view);
                Button hideButton = (Button) bottomSheetView.findViewById(R.id.hide_button);

                CountriesListAdapter adapter = new CountriesListAdapter(MainActivity.this, R.layout.country_row, countriesNames);
                listView.setAdapter(adapter);

                Log.w("YO!", "Adapter set");


                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        countries.add(countriesNames.get(i));
                        updateUI();
                        bottomSheetDialog.hide();
                    }
                });

                hideButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.hide();
                    }
                });

                bottomSheetDialog.setCanceledOnTouchOutside(true);
                bottomSheetDialog.setCancelable(false);

                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });

        cardListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.w("YO!","Item long clicked");
                Stats tmp = (Stats) adapterView.getItemAtPosition(i);
                Log.w("YO!", "Country clicked: "+tmp.getCountry());
                countries.remove(tmp.getCountry());
                Toast.makeText(MainActivity.this, "COUNTRY REMOVED", Toast.LENGTH_LONG);
                updateUI();
                return false;
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
                                    cardListView.setAdapter(adapter);

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


        Log.w("YO!","Saving set: "+countries.size());

        prefsEditor.putStringSet("countrySet",countries);
        prefsEditor.apply();
    }
}

