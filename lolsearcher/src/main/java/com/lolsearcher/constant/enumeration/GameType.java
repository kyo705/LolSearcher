package com.lolsearcher.constant.enumeration;

import lombok.Getter;

@Getter
public enum GameType {

    ALL_QUEUE_ID(-1),
    SOLO_RANK_MODE(4),
    FLEX_RANK_MODE(42),
    NORMAL_MODE(2),
    AI_MODE(7),
    CUSTOM_MODE(0);

    private final int queueId;

    GameType(int queueId){
        this.queueId = queueId;
    }
}
