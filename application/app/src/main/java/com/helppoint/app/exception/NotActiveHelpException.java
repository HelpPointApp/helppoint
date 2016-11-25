package com.helppoint.app.exception;

public class NotActiveHelpException extends RuntimeException {
    public NotActiveHelpException(){
        super("There is not any active help.");
    }
}
