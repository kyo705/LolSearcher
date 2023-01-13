package com.lolsearcher.service.message.producer;

import java.util.List;

public interface MessageProducerService {

    void send(Object data, String summonerId, String lastMatchId);

    void sendBatch(List<Object> data, String summonerId, String lastMatchId);

    void validateDataType(Object data);
}
