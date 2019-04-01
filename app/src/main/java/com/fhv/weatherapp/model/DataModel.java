package com.fhv.weatherapp.model;

public class DataModel {

    String location;
    String degree;
    String type;
    public DataModel(String location, String degree, String type) {
        this.location = location;
        this.degree = degree;
        this.type = type;

    }

    public String getLocation() {
        return location;
    }

    public String getDegree() {
        String deg = degree + "\u2103";
        return deg;
    }
    public String getType() {
        return type;
    }

}
