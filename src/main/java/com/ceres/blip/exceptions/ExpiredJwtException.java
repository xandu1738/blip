package com.ceres.blip.exceptions;

public class ExpiredJwtException extends Exception{
    public ExpiredJwtException(String message) {
        super(message);
    }
}
