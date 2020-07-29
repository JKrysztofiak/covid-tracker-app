package com.jkrysztofiak.covidtracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
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
    TextView dateView;

    String jsonResponse;

//    Set<String> countries;
//    Set<String> countriesID;
    ArrayList<String> countriesIDList;
    ArrayList<String> countriesNames;

    CountryStatsModel model;

    SharedPreferences  mPrefs;

    String dataJson;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPrefs = getPreferences(MODE_PRIVATE);

        countriesNames = new ArrayList<>();
        countriesIDList = new ArrayList<>();

        OkHttpClient client = new OkHttpClient();

        String url = "https://api.covid19api.com/summary";

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
//                                                        jsonResponse = response.body().string();
                                                        dataJson = response.body().string();
                                                        JSONObject object = new JSONObject(dataJson);
                                                        JSONArray arr = object.getJSONArray("Countries");

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
        dateView = (TextView) findViewById(R.id.date);


        String json = mPrefs.getString("Set","");

        Log.w("YO!","JSON: "+json);

        try {
            JSONArray arrTmp = new JSONArray(json);

            for(int i=0; i<arrTmp.length(); i++){
                countriesIDList.add((String)arrTmp.get(i));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RecyclerView recyclerView = findViewById(R.id.recycle_list_view);

        model = new CountryStatsModel();

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(MainActivity.this, model);

        ItemTouchHelper.Callback callback = new MyItemTouchHelper(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        adapter.setTouchHelper(itemTouchHelper);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        initializeUI();

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
//                        countries.add(countriesNames.get(i));
//                        countriesID.add(i+"");
                        countriesIDList = model.getOrder();
                        if(!countriesIDList.contains(i+"")){
                            Log.w("YO!", "Clicked: "+i+" "+countriesNames.get(i));
                            countriesIDList.add(i+"");
                            updateUI();
                            bottomSheetDialog.hide();
                        }else{
                            Toast.makeText(MainActivity.this, "This country is already selected", Toast.LENGTH_SHORT).show();
                        }


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

//        cardListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Log.w("YO!","Item long clicked");
//                Stats tmp = (Stats) adapterView.getItemAtPosition(i);
//                Log.w("YO!", "Country clicked: "+tmp.getCountry());
////                countries.remove(tmp.getCountry());
//                countriesIDList.remove(i);
//                Toast.makeText(MainActivity.this, "COUNTRY REMOVED", Toast.LENGTH_LONG);
//                updateUI();
//                return false;
//            }
//        });
    }

    public void initializeUI(){
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


                                    JSONArray countriesArray = object.getJSONArray("Countries");

                                    String date = countriesArray.getJSONObject(0).getString("Date");

                                    dateView.setText(date.split("T")[0]);

                                    for(String i: countriesIDList){
                                        int id = Integer.parseInt(i);
                                        Log.w("YO!","Adding country. ID: "+id);
                                        String currCountry = countriesArray.getJSONObject(id).getString("Country");
                                        Log.w("YO!","Adding country. Name: "+currCountry);
                                        int newCases = countriesArray.getJSONObject(id).getInt("NewConfirmed");
                                        int totalCases = countriesArray.getJSONObject(id).getInt("TotalConfirmed");
                                        String dateSpec = countriesArray.getJSONObject(id).getString("Date");
                                        Stats resp = new Stats(id, currCountry, newCases, totalCases, dateSpec);
                                        model.addCountry(resp);
                                    }

                                    countriesIDList = model.getOrder();
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


                                    JSONArray countriesArray = object.getJSONArray("Countries");

                                    String date = countriesArray.getJSONObject(0).getString("Date");

                                    dateView.setText(date.split("T")[0]);

                                    String i = countriesIDList.get(countriesIDList.size()-1);


                                    int id = Integer.parseInt(i);
                                    Log.w("YO!","Adding country. ID: "+id);
                                    String currCountry = countriesArray.getJSONObject(id).getString("Country");
                                    Log.w("YO!","Adding country. Name: "+currCountry);
                                    int newCases = countriesArray.getJSONObject(id).getInt("NewConfirmed");
                                    int totalCases = countriesArray.getJSONObject(id).getInt("TotalConfirmed");
                                    String dateSpec = countriesArray.getJSONObject(id).getString("Date");
                                    Stats resp = new Stats(id, currCountry, newCases, totalCases, dateSpec);
                                    model.addCountry(resp);

                                    countriesIDList = model.getOrder();
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

        countriesIDList = model.getOrder();

        Gson gson = new Gson();
        String json = gson.toJson(countriesIDList);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString("Set",json );
        editor.commit();

    }



}

