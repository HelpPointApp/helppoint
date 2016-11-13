package com.example.reubert.appcadeirantes.model;

import com.parse.Parse;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseClassName;
import com.parse.ParseRelation;

@ParseClassName("Help")
public class Help extends ParseObject{

    public User getUserTarget(){
        return (User) get("userTarget");
    }

    public void setUserTarget(User user){
        put("userTarget", user);
    }

    public User getUserHelp(){
        return (User) get("userHelp");
    }

    public void setUserHelp(User user){
        put("userHelp", user);
    }

    public int getTypeHelp(){
        return getInt("typeHelp");
    }

    public void setTypeHelp(int typeHelp){
        put("typeHelp", typeHelp);
    }

    public void setLocation(double latitude, double longitude){
        ParseGeoPoint point = new ParseGeoPoint(latitude, longitude);
        put("location", point);
    }

    public ParseGeoPoint getLocation(){
        return (ParseGeoPoint) get("location");
    }
}