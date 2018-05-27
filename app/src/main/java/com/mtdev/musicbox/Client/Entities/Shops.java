package com.mtdev.musicbox.Client.Entities;


public class Shops {

    private String name;
    private String address;
    private double lat;
    private double longi;
    private int number;


    public Shops() {
    }

    public Shops(String name, double lat, double longi, String address) {
        this.name = name;
        this.lat = lat;
        this.longi = longi;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLongi() {
        return longi;
    }

    public void setLongi(double longi) {
        this.longi = longi;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }


    @Override
    public String toString() {
        return "Shops{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", lat=" + lat +
                ", longi=" + longi +
                ", number=" + number +
                '}';
    }
}
