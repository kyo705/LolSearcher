package com.lolsearcher.constant.enumeration;

import lombok.Getter;

@Getter
public enum PositionStatus {

    NONE("NONE", 0),
    TOP("TOP", 1),
    JUNGLE("JUNGLE", 2),
    MIDDLE("MIDDLE", 3),
    BOTTOM("BOTTOM", 4),
    UTILITY("UTILITY", 5);


    private final String name;
    private final int id;

    PositionStatus(String name, int id){
        this.name = name;
        this.id = id;
    }
}
