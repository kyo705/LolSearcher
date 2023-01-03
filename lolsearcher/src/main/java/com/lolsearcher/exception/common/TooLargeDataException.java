package com.lolsearcher.exception.common;

public class TooLargeDataException extends RuntimeException {

    private final String data;

    public TooLargeDataException(String data){
        this.data = data;
    }

    @Override
    public String getMessage() {
        return String.format("데이터의 길이가 '%s'로 너무 큽니다.", data.length());
    }
}
