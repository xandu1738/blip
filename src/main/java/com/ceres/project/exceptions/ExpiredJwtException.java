package com.ceres.project.exceptions;

public class ExpiredJwtException extends Exception{
    public ExpiredJwtException(String message) {
        super(message);
    }
}
