package com.lolsearcher.exception.ingame;

import org.springframework.dao.IncorrectResultSizeDataAccessException;

public class MoreInGameException extends IncorrectResultSizeDataAccessException {
    public MoreInGameException(int expectedSize) {
        super(expectedSize);
    }

    public MoreInGameException(int expectedSize, int actualSize) {
        super(expectedSize, actualSize);
    }

    public MoreInGameException(String msg, int expectedSize) {
        super(msg, expectedSize);
    }

    public MoreInGameException(String msg, int expectedSize, Throwable ex) {
        super(msg, expectedSize, ex);
    }

    public MoreInGameException(String msg, int expectedSize, int actualSize) {
        super(msg, expectedSize, actualSize);
    }

    public MoreInGameException(String msg, int expectedSize, int actualSize, Throwable ex) {
        super(msg, expectedSize, actualSize, ex);
    }
}
