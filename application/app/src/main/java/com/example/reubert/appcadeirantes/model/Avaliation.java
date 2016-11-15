package com.example.reubert.appcadeirantes.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Avaliation")
public class Avaliation extends ParseObject {

    public void setUserHelp(ParseUser user){
        put("userHelp", user);
    }

    public ParseUser getUserHelp(){
        return (ParseUser) get("userHelp");
    }

    public void setHelp(Help help){
        put("help", help);
    }

    public Help getHelp(){
        return (Help) get("help");
    }

    public void setRating(int stars){
        put("stars", stars);
    }

    public int getRating(){
        return getInt("stars");
    }

}
