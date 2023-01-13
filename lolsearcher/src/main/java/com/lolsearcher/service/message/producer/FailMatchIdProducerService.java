package com.lolsearcher.service.message.producer;

import com.lolsearcher.service.summoner.SummonerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaProducerException;
import org.springframework.kafka.core.KafkaSendCallback;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class FailMatchIdProducerService implements MessageProducerService {

    @Value("${lolsearcher.kafka.topics.fail_match_id.name}")
    private String TOPIC_NAME;

    private final KafkaTemplate<String, String> failMatchIdKafkaTemplate;

    private final SummonerService summonerService;

    @Override
    public void send(Object failMatchId, String summonerId, String beforeLastMatchId) {

        validateDataType(failMatchId);

        String validFailMatchId = (String) failMatchId;
        ProducerRecord<String, String> record = createRecord(validFailMatchId);

        ListenableFuture<SendResult<String, String>> future = failMatchIdKafkaTemplate.send(record);
        future.addCallback(callback(summonerId, beforeLastMatchId));
    }

    @Override
    public void sendBatch(List<Object> failMatchIds, String summonerId, String lastMatchId) {

        failMatchIds.forEach(failMatchId-> send(failMatchId, summonerId, lastMatchId));
    }

    @Override
    public void validateDataType(Object data) {
        if(!(data instanceof String)) {
            log.error("입력 받은 failMatchId 데이터 타입이 String 클래스가 아닙니다.");
            throw new ClassCastException();
        }
    }

    private ProducerRecord<String, String> createRecord(String data){
        return new ProducerRecord<>(TOPIC_NAME, data);
    }

    private KafkaSendCallback<String, String> callback(String summonerId, String beforeLastMatchId){
        return new KafkaSendCallback<>(){
            @Override
            public void onSuccess(SendResult<String, String> result) {
                log.info("fail match id : {} 카프카 토픽에 저장 성공!!", result.getProducerRecord().value());
            }

            @SuppressWarnings("NullableProblems")
            @Override
            public void onFailure(KafkaProducerException ex) {
                log.error("fail match id : {} 카프카 토픽에 저장 실패...", ex.getFailedProducerRecord().value());

                summonerService.rollbackLastMatchId(summonerId, beforeLastMatchId);
            }
        };
    }
}
