package com.lolsearcher.service.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaProducerException;
import org.springframework.kafka.core.KafkaSendCallback;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class FailMatchIdProducerService implements ProducerService<String> {

    @Value("${lolsearcher.kafka.topics.fail_match_id.name}")
    private String TOPIC_NAME;

    private final KafkaTemplate<String, String> failMatchIdKafkaTemplate;

    @Transactional(transactionManager = "failMatchIdKafkaTransactionManager")
    @Override
    public void send(String failMatchId) {

        ProducerRecord<String, String> record = createRecord(failMatchId);

        ListenableFuture<SendResult<String, String>> future = failMatchIdKafkaTemplate.send(record);

        future.addCallback(callback());
    }

    @Transactional(transactionManager = "failMatchIdKafkaTransactionManager")
    @Override
    public void sendBatch(List<String> failMatchIds) {

        KafkaSendCallback<String, String> callback = callback();

        for(String failMatchId : failMatchIds){
            ProducerRecord<String, String> record = createRecord(failMatchId);

            ListenableFuture<SendResult<String, String>> future = failMatchIdKafkaTemplate.send(record);

            future.addCallback(callback);
        }
    }

    private ProducerRecord<String, String> createRecord(String data){
        return new ProducerRecord<>(TOPIC_NAME, data);
    }

    private KafkaSendCallback<String, String> callback(){
        return new KafkaSendCallback<>(){

            @Override
            public void onSuccess(SendResult<String, String> result) {
                log.info("fail match id : {} 카프카 토픽에 저장 성공!!", result.getProducerRecord().value());
            }

            @Override
            public void onFailure(KafkaProducerException ex) {
                log.error("fail match id : {} 카프카 토픽에 저장 실패...", ex.getFailedProducerRecord().value());
            }
        };
    }
}
