package com.lolsearcher.exception.ingame;

import com.lolsearcher.exception.common.NoExistDataException;
import lombok.Getter;

@Getter
public class NoInGameException extends NoExistDataException {

    private final String summonerName;

    public NoInGameException(String summonerName) {
        super(summonerName);
        this.summonerName = summonerName;
    }

    public NoInGameException(String msg, String summonerName) {
        super(msg, summonerName);
        this.summonerName = summonerName;
    }

    public NoInGameException(String msg, Throwable ex, String summonerName) {
        super(msg, ex, summonerName);
        this.summonerName = summonerName;
    }

    @Override
    public String getMessage() {
        return String.format("소환사 : '%s' 는 현재 게임 중이지 않습니다.", summonerName);
    }
}
