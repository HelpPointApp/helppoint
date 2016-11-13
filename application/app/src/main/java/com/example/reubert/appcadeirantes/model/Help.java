package com.example.reubert.appcadeirantes.model;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseClassName;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


/*  Example
    Help help = new Help();
    help.setDescription("asd asd sad as da");
    help.setTypeHelp(1);
    help.setUserTarget(this.user);
    help.setLocation(-19.88619068, -44.01296422);
    help.save();*/

@ParseClassName("Help")
public class Help extends ParseObject{

    public ParseUser getUserTarget(){
        return (ParseUser) get("userTarget");
    }

    public void setUserTarget(ParseUser user){
        put("userTarget", user);
    }

    public ParseUser getUserHelp(){
        return (ParseUser) get("userHelp");
    }

    public void setUserHelp(ParseUser user){
        put("userHelp", user);
    }

    public void setDescription(String description){
        put("description", description);
    }

    public String getDescription(){
        return getString("description");
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

    public static void GetHelpOutOfCloseness(double latitude,
                                             double longitude,
                                             int zoom, FindCallback<Help> callback) {

        ParseGeoPoint userLocation = new ParseGeoPoint(latitude, longitude);
        ParseQuery<Help> query = ParseQuery.getQuery("Help");
        ParseQuery<Help> helps = query.whereWithinKilometers("location", userLocation, (zoom > 17 ? zoom - 17 : 1) * 2);
        helps.findInBackground(callback);
    }
}