package com.Project.URL.Shortner.exceptions;

public class UrlNotFoundException extends RuntimeException{

    public UrlNotFoundException(String message){
        super(message);
    }
}
