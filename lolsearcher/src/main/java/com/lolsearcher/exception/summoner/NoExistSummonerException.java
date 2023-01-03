package com.lolsearcher.exception.summoner;

import com.lolsearcher.exception.common.NoExistDataException;
import lombok.Getter;

@Getter
public class NoExistSummonerException extends NoExistDataException {

    private final String summonerName;

    public NoExistSummonerException(String summonerName) {
        super(summonerName);
        this.summonerName = summonerName;
    }

    public NoExistSummonerException(String msg, String summonerName) {
        super(msg, summonerName);
        this.summonerName = summonerName;
    }

    public NoExistSummonerException(String msg, Throwable ex, String summonerName) {
        super(msg, ex, summonerName);
        this.summonerName = summonerName;
    }

    @Override
    public String getMessage() {
        return String.format("소환사 : '%s' 는 존재하지 않습니다.", summonerName);
    }
}
