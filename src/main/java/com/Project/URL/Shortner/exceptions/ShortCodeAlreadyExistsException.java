package com.Project.URL.Shortner.exceptions;

public class ShortCodeAlreadyExistsException extends RuntimeException {
    public ShortCodeAlreadyExistsException(String message) {
        super(message);
    }
}
