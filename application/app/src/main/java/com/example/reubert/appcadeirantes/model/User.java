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

    public void setAge(int age){
        put("age", age);
    }

    public int getAge(){
        return getInt("age");
    }

    public void setLastPosition(double latitude, double longitude){
        ParseGeoPoint point = new ParseGeoPoint(latitude, longitude);
        put("lastLocation", point);
    }

    private ParseGeoPoint getLastPosition(){
        return (ParseGeoPoint) get("lastLocation");
    }
}
