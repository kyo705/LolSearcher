package com.lolsearcher.constant.enumeration;

import lombok.Getter;

@Getter
public enum GameResultStatus {

    WIN("wins", 0),
    LOSS("losses", 1),
    DRAW("draw", 2);

    private final String name;
    private final int value;

    GameResultStatus(String name, int value) {
        this.name = name;
        this.value = value;
    }
}
