package com.example.reubert.appcadeirantes.model;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseClassName;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

@ParseClassName("Help")
public class Help extends ParseObject{

    public enum TYPE {
        CarryBag,
        ClimbLadder,
    }

    public enum STATUS {
        Requesting,
        Helping,
        Finished,
        Canceled,
    }

    public ParseUser getHelpedParseUser(){
        return (ParseUser) get("userTarget");
    }

    public void setHelpedParseUser(ParseUser user){
        put("helpedUser", user);
    }

    public ParseUser getHelperParseUser(){
        return (ParseUser) get("helpedUser");
    }

    public void setHelperParseUser(ParseUser user){
        put("helperUser", user);
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

    public void avaliation(int stars, SaveCallback saveCallback){
        Avaliation avaliation = new Avaliation();
        avaliation.setHelp(this);
        avaliation.setRating(stars);
        avaliation.setUserHelp(this.getHelperParseUser());
        avaliation.saveInBackground(saveCallback);
    }

    public void cancel(SaveCallback callback){
        this.setStatus(STATUS.Canceled);
        this.saveInBackground(callback);
    }

    public void finish(SaveCallback saveCallback){
        ParseUser userHelp = this.getHelperParseUser();
        ParseUser userTarget = this.getHelpedParseUser();

        userTarget.put("status", User.STATUS.Idle.ordinal());
        userHelp.put("status", User.STATUS.Idle.ordinal());

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

    public static void getHelpInBackground(String objectId, GetCallback<Help> callback){
        ParseQuery<Help> query = ParseQuery.getQuery("Help");
        query.getInBackground(objectId, callback);
    }

    public static void getHelpOutOfCloseness(
            double latitude, double longitude, int zoom,
            FindCallback<Help> callback
    ){
        ParseGeoPoint userLocation = new ParseGeoPoint(latitude, longitude);
        ParseQuery<Help> query = ParseQuery.getQuery("Help");
        ParseQuery<Help> helps = query.whereWithinKilometers(
                "location", userLocation, (zoom < 17 ? (17 - zoom) * 10 : 1) * 2);
        helps = helps.whereEqualTo("status", STATUS.Requesting.ordinal());
        helps.findInBackground(callback);
    }

    public static Help createHelp(ParseUser user,
                                  int type,
                                  String description,
                                  double latitude,
                                  double longitude,
                                  SaveCallback saveCallback){
        Help help = new Help();
        help.setTypeHelp(TYPE.values()[type]);
        help.setDescription(description);
        help.setHelpedParseUser(user);
        help.setStatus(STATUS.Requesting);
        help.setLocation(latitude, longitude);
        help.saveInBackground(saveCallback);
        user.put("status", User.STATUS.RequestingHelp.ordinal());
        user.saveInBackground();
        return help;
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

        if (help == null) {
            callback.requestHelper(null);
            return;
        }

        final ParseUser userHelp = help.getHelperParseUser();
        final ParseUser userTarget = help.getHelpedParseUser();

        if (userHelp == null){
            help.setHelperParseUser(user);
            help.setStatus(STATUS.Helping);
            help.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null){
                        callback.requestHelper(help);
                        user.put("status", User.STATUS.HelpInProgress.ordinal());
                        user.saveInBackground();
                        userTarget.put("status", User.STATUS.HelpInProgress.ordinal());
                        userTarget.saveInBackground();
                    }else{
                        callback.requestHelper(null);
                    }
                }
            });
        } else {
            callback.requestHelper(null);
        }
    }

    public static void getRequestHelpByUser(ParseUser user, FindCallback<Help> callback){
        ParseQuery<Help> helpQuery = ParseQuery.getQuery("Help");
        helpQuery.whereEqualTo("status", STATUS.Requesting.ordinal());
        helpQuery.whereEqualTo("userTarget", user);
        helpQuery.findInBackground(callback);
    }

    public static class RequestHelpedCallback{
        public void requestHelper(Help help){}
    }
}