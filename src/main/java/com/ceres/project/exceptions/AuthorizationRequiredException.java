package com.ceres.project.exceptions;

public class AuthorizationRequiredException extends Exception {
    public AuthorizationRequiredException(String message) {
        super(message);
    }
}
