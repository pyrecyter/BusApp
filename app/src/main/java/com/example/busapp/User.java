package com.example.busapp;

public class User {
    private String Name,Email,PhoneNumber,Type,NumberPlate,RouteNumber;
    private double Latitude, Longitude;
    private Boolean isLive;

    public User(){}
    public User(String Name,String Email,String PhoneNumber,String Type){
        this.Name = Name;
        this.Email = Email;
        this.PhoneNumber = PhoneNumber;
        this.Type = Type;
    }
    public User(String Name,String PhoneNumber,String Type,String NumberPlate,String RouteNumber,Double Latitude,Double Longitude,Boolean isLive){
        this.Name = Name;
        this.PhoneNumber = PhoneNumber;
        this.Type = Type;
        this.NumberPlate = NumberPlate;
        this.RouteNumber = RouteNumber;
        this.Latitude = Latitude;
        this.Longitude = Longitude;
        this.isLive = isLive;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getNumberPlate() {
        return NumberPlate;
    }

    public void setNumberPlate(String numberPlate) {
        NumberPlate = numberPlate;
    }

    public String getRouteNumber() {
        return RouteNumber;
    }

    public void setRouteNumber(String routeNumber) {
        RouteNumber = routeNumber;
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

    public Boolean getLive() {
        return isLive;
    }

    public void setLive(Boolean live) {
        isLive = live;
    }
}
