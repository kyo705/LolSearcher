package com.lolsearcher.exception.ingame;

import org.springframework.dao.EmptyResultDataAccessException;

public class NoInGameException extends EmptyResultDataAccessException {
    public NoInGameException(int expectedSize) {
        super(expectedSize);
    }

    public NoInGameException(String msg, int expectedSize) {
        super(msg, expectedSize);
    }

    public NoInGameException(String msg, int expectedSize, Throwable ex) {
        super(msg, expectedSize, ex);
    }
}
