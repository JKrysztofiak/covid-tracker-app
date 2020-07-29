package com.jkrysztofiak.covidtracker;

import java.util.ArrayList;

public class CountryStatsModel {

    ArrayList<Stats> countries;

    public CountryStatsModel(){
        countries = new ArrayList<>();
    }

    public CountryStatsModel(ArrayList<Stats> countries) {
        this.countries = countries;
    }

    public Stats getCountryAtIndex(int i){
        return countries.get(i);
    }

    public void addCountry(Stats country){
        countries.add(country);
    }

    public void removeCountry(int i){
        countries.remove(i);
    }

    public void swapCountries(int fromPosition, int toPostion){
        //TODO: implement swap
        Stats tmp = countries.get(fromPosition);
        countries.remove(fromPosition);
        countries.add(toPostion, tmp);
    }

    public int getSize(){
        return countries.size();
    }

    public ArrayList<String> getOrder(){
        ArrayList<String> orderedList = new ArrayList<>();
        for(Stats c: countries){
            orderedList.add(c.getId()+"");
        }
        return orderedList;
    }

}
