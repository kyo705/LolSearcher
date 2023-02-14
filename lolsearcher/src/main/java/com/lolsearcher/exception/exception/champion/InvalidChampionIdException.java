package com.lolsearcher.exception.exception.champion;

public class InvalidChampionIdException extends RuntimeException {

    private final int championId;

    public InvalidChampionIdException(int championId) {
        this.championId = championId;
    }

    @Override
    public String getMessage() {
        return String.format("챔피언 ID : '%s' 는 존재하지 않는 값입니다.", championId);
    }
}
