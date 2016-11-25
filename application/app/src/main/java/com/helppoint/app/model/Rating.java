package com.helppoint.app.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Rating")
public class Rating extends ParseObject {

    public void setHelperUser(ParseUser user){
        put("helperUser", user);
    }

    public ParseUser getHelperUser(){
        return (ParseUser) get("helperUser");
    }

    public void setHelp(Help help){
        put("help", help);
    }

    public Help getHelp(){
        return (Help) get("help");
    }

    public void setStars(int stars){
        put("stars", stars);
    }

    public int getStars(){
        return getInt("stars");
    }

}