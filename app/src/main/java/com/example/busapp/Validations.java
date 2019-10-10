package com.example.busapp;

public class Validations {

    public Validations(){}

    public boolean EmailValidation(String email){
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    public boolean PlateValidation(String plate){
        String regx = "^[a-zA-Z]{2,3}[\\-][0-9]{4}$";
        return  plate.matches(regx);
    }

    public boolean notNullValidate(String text){
        return !text.isEmpty();
    }

    public boolean passwordValidation(String password){
        int length = password.length();
        return length>=6;
    }

    public boolean phoneNumberValidation(String number){
        int length = number.length();
        return length>=10;
    }
}
