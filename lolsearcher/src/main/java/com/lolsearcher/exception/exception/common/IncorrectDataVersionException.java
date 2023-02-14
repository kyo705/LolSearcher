package com.lolsearcher.exception.exception.common;

import com.lolsearcher.constant.LolSearcherConstants;

public class IncorrectDataVersionException extends RuntimeException {

    private final String requestDataVersion;

    public IncorrectDataVersionException(String requestDataVersion){
        this.requestDataVersion = requestDataVersion;
    }

    @Override
    public String getMessage() {
        return String.format(
                "데이터의 현재 버전 '%s'과 요청 버전 '%s'가 다릅니다.",
                LolSearcherConstants.MATCH_DATA_VERSION,
                requestDataVersion
        );
    }
}
