package com.example.reubert.appcadeirantes.exception;

public class ExistentHelpException extends RuntimeException {
    public ExistentHelpException(){
        super("Tried to create a new Help instance when there is another one created.");
    }
}