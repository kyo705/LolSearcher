package com.lolsearcher.exception.exception.search.summoner;

import com.lolsearcher.exception.exception.common.NoExistDataException;
import lombok.Getter;

@Getter
public class NotExistedSummonerInGameServerException extends NoExistDataException {


    private final String summonerName;

    public NotExistedSummonerInGameServerException(String summonerName) {
        super(summonerName);
        this.summonerName = summonerName;
    }

    public NotExistedSummonerInGameServerException(String msg, String summonerName) {
        super(msg, summonerName);
        this.summonerName = summonerName;
    }

    public NotExistedSummonerInGameServerException(String msg, Throwable ex, String summonerName) {
        super(msg, ex, summonerName);
        this.summonerName = summonerName;
    }

    @Override
    public String getMessage() {
        return String.format("소환사 : '%s' 는 실제 게임 환경 내에 존재하지 않습니다.", summonerName);
    }
}
