package com.lolsearcher.exception.summoner;

import org.springframework.dao.EmptyResultDataAccessException;

public class NoSummonerException extends EmptyResultDataAccessException {

    public NoSummonerException(int expectedSize) {
        super(expectedSize);
    }

    public NoSummonerException(String msg, int expectedSize) {
        super(msg, expectedSize);
    }

    public NoSummonerException(String msg, int expectedSize, Throwable ex) {
        super(msg, expectedSize, ex);
    }
}
