package com.lolsearcher.exception.champion;

import com.lolsearcher.exception.common.NoExistDataException;

public class NoExistChampionException extends NoExistDataException {

    private final String championId;

    public NoExistChampionException(String championId) {
        super(championId);
        this.championId = championId;
    }

    public NoExistChampionException(String msg, String championId) {
        super(msg, championId);
        this.championId = championId;
    }

    public NoExistChampionException(String msg, Throwable ex, String championId) {
        super(msg, ex, championId);
        this.championId = championId;
    }

    @Override
    public String getMessage() {
        return String.format("챔피언 : '%s' 는 존재하지 않습니다.", championId);
    }
}
