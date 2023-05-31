package com.lolsearcher.search.champion;

import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum PositionState {

    NONE("NONE", 0),
    TOP("TOP", 1),
    JUNGLE("JUNGLE", 2),
    MIDDLE("MIDDLE", 3),
    BOTTOM("BOTTOM", 4),
    UTILITY("UTILITY", 5);


    private final String name;
    private final int code;

    PositionState(String name, int code){
        this.name = name;
        this.code = code;
    }

    private static final Map<Integer, PositionState> BY_CODE =
            Stream.of(values()).collect(Collectors.toMap(PositionState::getCode, e -> e));

    public static final PositionState valueOfCode(int code){

        return BY_CODE.getOrDefault(code, null);
    }
}
