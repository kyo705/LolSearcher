package com.lolsearcher.errors.exception.user.identification;

public class IdentificationException extends RuntimeException {

    private final int statusCode;
    private final String message;

    public IdentificationException(int statusCode, String message){
        this.statusCode = statusCode;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    public int getStatusCode(){
        return this.statusCode;
    }
}
