package com.example.reubert.appcadeirantes.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

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
