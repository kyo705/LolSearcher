package com.lolsearcher.errors.exception.summoner;

import lombok.Getter;
import org.springframework.dao.EmptyResultDataAccessException;

@Getter
public class NotExistedSummonerInDBException extends EmptyResultDataAccessException {

    private final String summonerName;

    public NotExistedSummonerInDBException(String summonerName) {
        super(1);
        this.summonerName = summonerName;
    }

    public NotExistedSummonerInDBException(String msg, String summonerName) {
        super(msg, 1);
        this.summonerName = summonerName;
    }

    public NotExistedSummonerInDBException(String msg, Throwable ex, String summonerName) {
        super(msg, 1, ex);
        this.summonerName = summonerName;
    }

    @Override
    public String getMessage() {
        return String.format("소환사 : '%s' 는 데이터베이스에 존재하지 않습니다.", summonerName);
    }
}
