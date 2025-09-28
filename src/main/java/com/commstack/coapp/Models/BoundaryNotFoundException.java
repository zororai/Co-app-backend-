package com.commstack.coapp.Models;

public class BoundaryNotFoundException extends RuntimeException {

    public BoundaryNotFoundException(String message) {
        super(message);
    }

    public BoundaryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}