package com.lolsearcher.errors.exception.user;

import lombok.Getter;
import org.springframework.dao.EmptyResultDataAccessException;

@Getter
public class NotExistingUserException extends EmptyResultDataAccessException {

    private final Long userId;
    public NotExistingUserException(Long userId, int expectedSize) {
        super(expectedSize);
        this.userId = userId;
    }

    public NotExistingUserException(Long userId, String msg, int expectedSize) {
        super(msg, expectedSize);
        this.userId = userId;
    }

    public NotExistingUserException(Long userId, String msg, int expectedSize, Throwable ex) {
        super(msg, expectedSize, ex);
        this.userId = userId;
    }
}
