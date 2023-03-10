package com.lolsearcher.constant.enumeration;

import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum PositionStatus {

    NONE("NONE", 0),
    TOP("TOP", 1),
    JUNGLE("JUNGLE", 2),
    MIDDLE("MIDDLE", 3),
    BOTTOM("BOTTOM", 4),
    UTILITY("UTILITY", 5);


    private final String name;
    private final int code;

    PositionStatus(String name, int code){
        this.name = name;
        this.code = code;
    }

    private static final Map<Integer, PositionStatus> BY_CODE =
            Stream.of(values()).collect(Collectors.toMap(PositionStatus::getCode, e -> e));

    public static final PositionStatus valueOfCode(int code){
        return BY_CODE.get(code);
    }
}
