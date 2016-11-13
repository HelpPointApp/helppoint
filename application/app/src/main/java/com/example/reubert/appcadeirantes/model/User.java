package com.example.reubert.appcadeirantes.model;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

@ParseClassName("User")
public class User extends ParseUser{
    public void setName (String name){
        put("name", name);
    }

    public String getName(){
        return getString("name");
    }

    public void setBirthday(String date){
        put("birthday", date);
    }

    public String getBirthday(){
        return getString("birthday");
    }

    public void setAge(int age){
        put("age", age);
    }

    public int getAge(){
        return getInt("age");
    }

    public void setPoints(int points){
        put("points", points);
    }

    public int getPoints(){
        return getInt("points");
    }

    public void setCPF(String cpf){
        put("cpf", cpf);
    }

    public String getCPF(){
        return getString("cpf");
    }

    public void setLastPosition(double latitude, double longitude){
        ParseGeoPoint point = new ParseGeoPoint(latitude, longitude);
        put("lastLocation", point);
    }

    public ParseGeoPoint getLastPosition(){
        return (ParseGeoPoint) get("lastLocation");
    }

    public void setStatus(STATUS status){
        put("status", status);
    }

    public STATUS getStatus(){
        return (STATUS) get("status");
    }

    public enum STATUS{
        Hidden,
        Helping,
        RequestingHelp,
    }
}
