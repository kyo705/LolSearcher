package com.lolsearcher.exception.exception.search.summoner;

import com.lolsearcher.exception.exception.common.NoExistDataException;
import lombok.Getter;

@Getter
public class NotExistedSummonerInDBException extends NoExistDataException {

    private final String summonerName;

    public NotExistedSummonerInDBException(String summonerName) {
        super(summonerName);
        this.summonerName = summonerName;
    }

    public NotExistedSummonerInDBException(String msg, String summonerName) {
        super(msg, summonerName);
        this.summonerName = summonerName;
    }

    public NotExistedSummonerInDBException(String msg, Throwable ex, String summonerName) {
        super(msg, ex, summonerName);
        this.summonerName = summonerName;
    }

    @Override
    public String getMessage() {
        return String.format("소환사 : '%s' 는 데이터베이스에 존재하지 않습니다.", summonerName);
    }
}
