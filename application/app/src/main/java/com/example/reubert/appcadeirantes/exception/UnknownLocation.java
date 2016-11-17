package com.example.reubert.appcadeirantes.exception;

public class UnknownLocation extends RuntimeException {

    public UnknownLocation(){
        super("Location could not be found! Sometimes, device's GPS " +
                "system takes a while until getting the user's location.");
    }

}
