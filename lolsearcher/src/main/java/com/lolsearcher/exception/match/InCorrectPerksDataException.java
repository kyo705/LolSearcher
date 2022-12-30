package com.lolsearcher.exception.match;

import org.springframework.dao.IncorrectResultSizeDataAccessException;

public class InCorrectPerksDataException extends IncorrectResultSizeDataAccessException {
    public InCorrectPerksDataException(int expectedSize) {
        super(expectedSize);
    }

    public InCorrectPerksDataException(int expectedSize, int actualSize) {
        super(expectedSize, actualSize);
    }

    public InCorrectPerksDataException(String msg, int expectedSize) {
        super(msg, expectedSize);
    }

    public InCorrectPerksDataException(String msg, int expectedSize, Throwable ex) {
        super(msg, expectedSize, ex);
    }

    public InCorrectPerksDataException(String msg, int expectedSize, int actualSize) {
        super(msg, expectedSize, actualSize);
    }

    public InCorrectPerksDataException(String msg, int expectedSize, int actualSize, Throwable ex) {
        super(msg, expectedSize, actualSize, ex);
    }
}
