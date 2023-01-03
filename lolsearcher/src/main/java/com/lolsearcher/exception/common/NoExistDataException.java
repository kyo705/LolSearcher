package com.lolsearcher.exception.common;

import org.springframework.dao.EmptyResultDataAccessException;

public class NoExistDataException extends EmptyResultDataAccessException {

    private final String data;

    public NoExistDataException(String data) {
        super(1);
        this.data = data;
    }

    public NoExistDataException(String msg, String data) {
        super(msg, 1);
        this.data = data;
    }

    public NoExistDataException(String msg, Throwable ex, String data) {
        super(msg, 1, ex);
        this.data = data;
    }

    @Override
    public String getMessage() {
        return String.format("데이터 : '%s' 는 존재하지 않습니다.", data);
    }
}
