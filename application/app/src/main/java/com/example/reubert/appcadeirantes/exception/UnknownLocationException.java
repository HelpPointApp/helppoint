package com.example.reubert.appcadeirantes.exception;

public class UnknownLocationException extends RuntimeException {

    public UnknownLocationException(){
        super("Location could not be found! Sometimes, device's GPS " +
                "system takes a while until getting the user's location.");
    }

}
