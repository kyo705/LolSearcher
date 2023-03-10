package com.lolsearcher.constant.enumeration;

import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private static final Map<Integer, GameType> BY_QUEUE_ID =
            Stream.of(values()).collect(Collectors.toMap(GameType::getQueueId, e -> e));

    public static final GameType valueOfQueueId(int queueId){
        return BY_QUEUE_ID.get(queueId);
    }
}
