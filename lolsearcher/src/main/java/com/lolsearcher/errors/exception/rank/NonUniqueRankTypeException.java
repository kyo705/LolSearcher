package com.lolsearcher.errors.exception.rank;

import com.lolsearcher.search.rank.RankTypeState;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

public class NonUniqueRankTypeException extends IncorrectResultSizeDataAccessException {

    private final String rankType;

    public NonUniqueRankTypeException(RankTypeState rankType) {
        this(rankType.toString());
    }

    public NonUniqueRankTypeException(String rankType) {
        super(1);
        this.rankType = rankType;
    }

    @Override
    public String getMessage() {
        return String.format("개인 유저당 %s 의 랭크 데이터는 반드시 하나만 존재해야한다.", rankType);
    }
}
