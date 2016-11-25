package com.helppoint.app.model;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("User")
public class User extends ParseUser {

    public enum STATUS{
        Idle,
        RequestingHelp,
        HelpInProgress,
    }

    public User setFirstName (String name){
        put("firstName", name);
        return this;
    }

    public String getFirstName(){
        return getString("firstName");
    }

    public User setLastName(String lastName){
        put("lastName", lastName);
        return this;
    }

    public String getLastName(){
        return getString("lastName");
    }

    public User setBirthday(String date){
        put("birthday", date);
        return this;
    }

    public String getBirthday(){
        return getString("birthday");
    }

    public User setPoints(int points){
        put("points", points);
        return this;
    }

    public int getPoints(){
        return getInt("points");
    }

    public User setCPF(String cpf){
        put("cpf", cpf);
        return this;
    }

    public String getCPF(){
        return getString("cpf");
    }

    public User setLastPosition(double latitude, double longitude){
        ParseGeoPoint point = new ParseGeoPoint(latitude, longitude);
        put("lastLocation", point);
        return this;
    }

    public ParseGeoPoint getLastPosition(){
        return (ParseGeoPoint) get("lastLocation");
    }

    public User setStatus(STATUS status){
        put("status", status.ordinal());
        return this;
    }

    public STATUS getStatus(){
        return (STATUS) get("status");
    }

    public static void getNearUsers(double latitude,
                                    double longitude,
                                    FindCallback<ParseUser> callback){
        ParseGeoPoint geoPoint = new ParseGeoPoint(latitude, longitude);
        ParseQuery<ParseUser> userQuery = ParseQuery.getQuery("User");
        userQuery.whereWithinKilometers("lastPosition", geoPoint, 2);
        userQuery.findInBackground(callback);
    }
}
