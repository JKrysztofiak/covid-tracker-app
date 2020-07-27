package com.jkrysztofiak.covidtracker;

public class Stats {

    private int id;
    private String country;
    private int newCases;
    private int totalCases;
    private String date;

    public Stats(int id, String country, int newCases, int totalCases, String date) {
        this.id = id;
        this.country = country;
        this.newCases = newCases;
        this.totalCases = totalCases;
        this.date = date;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getNewCases() {
        return newCases;
    }

    public void setNewCases(int newCases) {
        this.newCases = newCases;
    }

    public int getTotalCases() {
        return totalCases;
    }

    public void setTotalCases(int totalCases) {
        this.totalCases = totalCases;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
