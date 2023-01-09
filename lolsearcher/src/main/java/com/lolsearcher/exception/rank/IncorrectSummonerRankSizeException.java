package com.lolsearcher.exception.rank;

import org.springframework.dao.IncorrectResultSizeDataAccessException;

public class IncorrectSummonerRankSizeException extends IncorrectResultSizeDataAccessException {

    private final int realSize;

    public IncorrectSummonerRankSizeException(int realSize) {
        super(1);
        this.realSize = realSize;
    }

    @Override
    public String getMessage() {
        return String.format("소환사당 가질 수 있는 랭크 종류는 2가지 뿐이다. 그러나 현재 %d 개의 데이터가 조회되었음.", realSize);
    }
}
