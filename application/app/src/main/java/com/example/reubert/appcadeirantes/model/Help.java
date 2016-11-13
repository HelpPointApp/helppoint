package com.example.reubert.appcadeirantes.model;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseClassName;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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

    public TYPE getTypeHelp(){
        return TYPE.values()[getInt("typeHelp")];
    }

    public void setTypeHelp(TYPE typeHelp){
        put("typeHelp", typeHelp.ordinal());
    }

    public void setLocation(double latitude, double longitude){
        ParseGeoPoint point = new ParseGeoPoint(latitude, longitude);
        put("location", point);
    }

    public ParseGeoPoint getLocation(){
        return (ParseGeoPoint) get("location");
    }

    public void setStatus(STATUS status){
        put("status", status.ordinal());
    }

    public STATUS getStatus(){
        return STATUS.values()[getInt("status")];
    }

    public void sendAvaliation(int stars, SaveCallback saveCallback){
        Avaliation avaliation = new Avaliation();
        avaliation.setHelp(this);
        avaliation.setStars(stars);
        avaliation.setUserHelp(this.getUserHelp());
        avaliation.saveInBackground(saveCallback);
    }

    public void finish(SaveCallback saveCallback){
        this.setStatus(STATUS.Finished);
        this.saveInBackground(saveCallback);
    }

    public static Help getHelp(String objectId){
        try {
            ParseQuery<Help> helpQuery = ParseQuery.getQuery("Help");
            return helpQuery.get(objectId);
        }catch(ParseException e){
            Log.e("gethelp", e.toString());
        }

        return null;
    }

    public static void GetHelpOutOfCloseness(double latitude,
                                             double longitude,
                                             int zoom, FindCallback<Help> callback) {

        ParseGeoPoint userLocation = new ParseGeoPoint(latitude, longitude);
        ParseQuery<Help> query = ParseQuery.getQuery("Help");
        ParseQuery<Help> helps = query.whereWithinKilometers(
                "location", userLocation, (zoom > 17 ? zoom - 17 : 1) * 2);
        helps.findInBackground(callback);
    }

    public static void createHelp(User user,
                                  TYPE type,
                                  String description,
                                  SaveCallback saveCallback){
        Help help = new Help();
        help.setTypeHelp(type);
        help.setDescription(description);
        help.setUserHelp((ParseUser) user);
        help.setStatus(STATUS.Requesting);
        help.saveInBackground(saveCallback);
    }

    public static void UserRequestHelped(String helpObjectId,
                                         final ParseUser user,
                                         final RequestHelpedCallback callback){
        final Help help = getHelp(helpObjectId);
        if (help == null) return;

        ParseUser userHelp = help.getUserHelp();

        if (userHelp == null){
            help.setUserHelp((ParseUser) user);
            help.setStatus(STATUS.Helping);
            help.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    callback.requestHelped(help);
                    try {
                        user.put("status", User.STATUS.Helping);
                        user.save();
                    }catch(ParseException eUser){
                        Log.e("change status", eUser.toString());
                    }
                }
            });
        }else{
            callback.requestHelped(null);
        }
    }

    public enum TYPE {
        CarryBag,
        ClimbLadder,
    }

    public enum STATUS {
        Requesting,
        Helping,
        Finished,
    }

    public static class RequestHelpedCallback{
        public void requestHelped(Help help){}
    }
}