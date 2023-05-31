package com.lolsearcher.errors.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

public class LolSearcherException extends RuntimeException{

    private HttpStatus httpStatus;
    private HttpHeaders headers;
    private String message;

}
