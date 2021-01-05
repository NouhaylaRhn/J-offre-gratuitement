package com.example.projet_v1.Model;

public class Adresse {
    public String adrName;
    public double latitude;
    public double longitude;

    public Adresse() {
        adrName="";
        latitude =0;
        longitude =0;
    }

    public Adresse(String adrName, double latitude, double longitude) {
        this.adrName = adrName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Adresse{" +
                "adrName='" + adrName + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
