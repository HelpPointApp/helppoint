package com.example.reubert.appcadeirantes.model;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseRelation;

/**
 * Created by aron on 12/11/16.
 */

@ParseClassName("Avaliation")
public class Avaliation extends ParseObject {

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
