package com.lolsearcher.search.match;

import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum MatchQueue {

    ALL_QUEUE_ID(-1),
    CUSTOM_MODE(0),
    NORMAL_MODE(2),
    SOLO_RANK_MODE(4),
    FLEX_RANK_MODE(42),
    AI_MODE(7);


    private final int queueId;

    MatchQueue(int queueId){
        this.queueId = queueId;
    }

    private static final Map<Integer, MatchQueue> BY_QUEUE_ID =
            Stream.of(values()).collect(Collectors.toMap(MatchQueue::getQueueId, e -> e));

    public static final MatchQueue valueOfQueueId(int queueId){
        return BY_QUEUE_ID.get(queueId);
    }
}
