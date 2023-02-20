package com.lolsearcher.aop.idgeneration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IdGenerator {

    private static final long DEFAULT_INITIAL_TIMESTAMP = 1647702000L; /* Sun Mar 20 2022 00:00:00 GMT+0900 (한국 표준시) */
    private static final int NODE_ID_BIT = 10;
    private static final int SEQUENCE_ID_BIT = 10;
    private static final long MAX_NODE_ID = 1 << NODE_ID_BIT -1;
    private static final long MAX_SEQUENCE = 1 << SEQUENCE_ID_BIT - 1;

    private final long nodeId;
    private final long customInitialTimeStamp;
    private volatile long sequence = 0L;
    private volatile long lastTimeStamp = -1L;

    public IdGenerator(long nodeId, long customInitialTimeStamp){
        if(nodeId > MAX_NODE_ID || nodeId < 0){
            log.error("허용되지 않는 Node Id 입니다. request node id : {}", nodeId);
            throw new IllegalArgumentException();
        }
        this.nodeId = nodeId;
        this.customInitialTimeStamp = customInitialTimeStamp;
    }

    public IdGenerator(long nodeId){
        this(nodeId, DEFAULT_INITIAL_TIMESTAMP);
    }

    public synchronized long generateId() {

        long currentTimeStamp = getCurrentTimStamp();

        if(currentTimeStamp < lastTimeStamp){
            log.error("타임 스탬프 이상 현상 발생");
            throw new RuntimeException();
        }else if(currentTimeStamp == lastTimeStamp){
            sequence = (sequence + 1) % MAX_SEQUENCE;
            if(sequence == 0){
                currentTimeStamp = waitAndGetNextTimeStamp(currentTimeStamp);
            }
        }else{
            sequence = 0;
        }

        lastTimeStamp = currentTimeStamp;

        return (lastTimeStamp << (NODE_ID_BIT + SEQUENCE_ID_BIT)) + (nodeId << SEQUENCE_ID_BIT) + sequence;
    }

    private long waitAndGetNextTimeStamp(long currentTimeStamp) {

        while(currentTimeStamp==lastTimeStamp){
            currentTimeStamp = getCurrentTimStamp();
        }
        return currentTimeStamp;
    }

    private long getCurrentTimStamp(){

        return System.currentTimeMillis() - customInitialTimeStamp;
    }
}
