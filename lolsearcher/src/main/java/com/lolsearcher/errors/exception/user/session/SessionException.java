package com.lolsearcher.errors.exception.user.session;

public class SessionException extends RuntimeException {

    private final String message;

    public SessionException(String message){
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
