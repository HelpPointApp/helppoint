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
        ParseUser userHelp = this.getUserHelp();
        ParseUser userTarget = this.getUserTarget();

        userTarget.put("status", User.STATUS.Hidden.ordinal());
        userHelp.put("status", User.STATUS.Hidden.ordinal());

        this.setStatus(STATUS.Finished);
        this.saveInBackground(saveCallback);

        userTarget.saveInBackground();
        userHelp.saveInBackground();
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
        helps = helps.whereEqualTo("status", STATUS.Requesting.ordinal());
        helps.findInBackground(callback);
    }

    public static void createHelp(ParseUser user,
                                  int type,
                                  String description,
                                  double latitude,
                                  double longitude,
                                  SaveCallback saveCallback){
        Help help = new Help();
        help.setTypeHelp(TYPE.values()[type]);
        help.setDescription(description);
        help.setUserTarget(user);
        help.setStatus(STATUS.Requesting);
        help.setLocation(latitude, longitude);
        help.saveInBackground(saveCallback);

        user.put("status", User.STATUS.RequestingHelp.ordinal());
    }

    public static void getHelpByUserHelper(ParseUser user, FindCallback<Help> callback){
        ParseQuery<Help> helpQuery = ParseQuery.getQuery("Help");
        helpQuery = helpQuery
                .whereEqualTo("userHelp", user)
                .whereEqualTo("status", STATUS.Helping.ordinal());
        helpQuery.findInBackground(callback);
    }

    public static void UserRequestHelped(String helpObjectId,
                                         final ParseUser user,
                                         final RequestHelpedCallback callback){
        final Help help = getHelp(helpObjectId);
        if (help == null) return;

        final ParseUser userHelp = help.getUserHelp();
        final ParseUser userTarget = help.getUserTarget();

        if (userHelp == null){
            help.setUserHelp(user);
            help.setStatus(STATUS.Helping);
            help.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    callback.requestHelped(help);
                    user.put("status", User.STATUS.HelpInProgress.ordinal());
                    user.saveInBackground();
                    userTarget.put("status", User.STATUS.HelpInProgress.ordinal());
                    userTarget.saveInBackground();
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