package com.lolsearcher.errors.exception.summoner;

import lombok.Getter;
import org.springframework.dao.EmptyResultDataAccessException;

@Getter
public class NotExistedSummonerInGameServerException extends EmptyResultDataAccessException {


    private final String summonerName;

    public NotExistedSummonerInGameServerException(String summonerName) {
        super(1);
        this.summonerName = summonerName;
    }

    public NotExistedSummonerInGameServerException(String msg, String summonerName) {
        super(msg, 1);
        this.summonerName = summonerName;
    }

    public NotExistedSummonerInGameServerException(String msg, Throwable ex, String summonerName) {
        super(msg, 1, ex);
        this.summonerName = summonerName;
    }

    @Override
    public String getMessage() {
        return String.format("소환사 : '%s' 는 실제 게임 환경 내에 존재하지 않습니다.", summonerName);
    }
}
