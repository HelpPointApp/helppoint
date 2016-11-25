package com.helppoint.app.exception;

public class ExistentHelpException extends RuntimeException {
    public ExistentHelpException(){
        super("Tried to create a new Help instance when there is another one created.");
    }
}