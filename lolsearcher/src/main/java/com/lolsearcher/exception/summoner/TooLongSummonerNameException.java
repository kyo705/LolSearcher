package com.lolsearcher.exception.summoner;

import com.lolsearcher.exception.common.TooLargeDataException;

public class TooLongSummonerNameException extends TooLargeDataException {

    private final String summonerName;
    public TooLongSummonerNameException(String summonerName) {
        super(summonerName);
        this.summonerName = summonerName;
    }

    @Override
    public String getMessage() {
        return String.format("소환사 이름의 길이가 '%s'로 너무 깁니다.", summonerName.length());
    }
}
