package com.lolsearcher.exception.summoner;

import org.springframework.dao.IncorrectResultSizeDataAccessException;

public class MoreSummonerException extends IncorrectResultSizeDataAccessException {
    public MoreSummonerException(int expectedSize) {
        super(expectedSize);
    }

    public MoreSummonerException(int expectedSize, int actualSize) {
        super(expectedSize, actualSize);
    }

    public MoreSummonerException(String msg, int expectedSize) {
        super(msg, expectedSize);
    }

    public MoreSummonerException(String msg, int expectedSize, Throwable ex) {
        super(msg, expectedSize, ex);
    }

    public MoreSummonerException(String msg, int expectedSize, int actualSize) {
        super(msg, expectedSize, actualSize);
    }

    public MoreSummonerException(String msg, int expectedSize, int actualSize, Throwable ex) {
        super(msg, expectedSize, actualSize, ex);
    }
}
