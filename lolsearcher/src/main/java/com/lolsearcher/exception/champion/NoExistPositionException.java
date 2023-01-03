package com.lolsearcher.exception.champion;

import com.lolsearcher.exception.common.NoExistDataException;

public class NoExistPositionException extends NoExistDataException {

    private final String position;

    public NoExistPositionException(String position) {
        super(position);
        this.position = position;
    }

    public NoExistPositionException(String msg, String position) {
        super(msg, position);
        this.position = position;
    }

    public NoExistPositionException(String msg, Throwable ex, String position) {
        super(msg, ex, position);
        this.position = position;
    }

    @Override
    public String getMessage() {
        return String.format("라인 포지션 : '%s' 는 존재하지 않습니다.", position);
    }
}
