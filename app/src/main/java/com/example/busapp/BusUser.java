package com.example.busapp;

public class BusUser {

    private String Bus_Number,Driver_Name,Phone_Number,RouteNumber;
    private double Latitude, Longitude;
    private boolean isLive;

    public BusUser(String bus_Number, String driver_Name, double latitude, double longitude, String phone_Number,boolean islive,String Route){

        Bus_Number = bus_Number;
        Driver_Name = driver_Name;
        Latitude = latitude;
        Longitude = longitude;
        Phone_Number = phone_Number;
        isLive = islive;
        Route = RouteNumber;
    }

    public String getBus_Number() {
        return Bus_Number;
    }

    public void setBus_Number(String bus_Number) {
        Bus_Number = bus_Number;
    }

    public String getDriver_Name() {
        return Driver_Name;
    }

    public void setDriver_Name(String driver_Name) {
        Driver_Name = driver_Name;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public String getPhone_Number() {
        return Phone_Number;
    }

    public void setPhone_Number(String phone_Number) {
        Phone_Number = phone_Number;
    }

    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean live) {
        isLive = live;
    }

    public String getRouteNumber() {
        return RouteNumber;
    }

    public void setRouteNumber(String routeNumber) {
        RouteNumber = routeNumber;
    }
}
