package com.lolsearcher.errors.exception.user.identification;

public class JWTIdentificationException extends IdentificationException {

    public JWTIdentificationException(int statusCode, String message){
        super(statusCode, message);
    }
}
