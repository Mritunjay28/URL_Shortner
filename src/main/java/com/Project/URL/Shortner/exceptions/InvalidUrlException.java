package com.Project.URL.Shortner.exceptions;

public class InvalidUrlException extends RuntimeException{

    public InvalidUrlException(String message){
        super(message);
    }
}
