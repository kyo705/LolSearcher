package com.lolsearcher.service.producer;

import java.util.List;

public interface ProducerService<T> {

    void send(List<T> data, String summonerId, String lastMatchId);
}
