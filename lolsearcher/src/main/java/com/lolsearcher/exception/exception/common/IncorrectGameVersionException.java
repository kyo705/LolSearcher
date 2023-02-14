package com.lolsearcher.exception.exception.common;

public class IncorrectGameVersionException extends RuntimeException {

    private final String incorrectVersion;

    public IncorrectGameVersionException(String incorrectVersion) {
        this.incorrectVersion = incorrectVersion;
    }

    @Override
    public String getMessage() {
        return String.format("게임 버전 : '%s' 는 존재하지 않습니다.", incorrectVersion);
    }
}
