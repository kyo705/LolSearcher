package com.lolsearcher.service.message.producer;

import com.lolsearcher.model.output.kafka.RemainingMatchId;
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
public class RemainingMatchIdProducerService implements MessageProducerService {
    @Value("${lolsearcher.kafka.topics.remaining_match_id.name}")
    private String TOPIC_NAME;

    private final KafkaTemplate<String, RemainingMatchId> failMatchIdKafkaTemplate;

    private final SummonerService summonerService;

    @Override
    public void send(Object remainingMatchId, String summonerId, String beforeLastMatchId) {

        validateDataType(remainingMatchId);

        RemainingMatchId validRemainingMatchId = (RemainingMatchId) remainingMatchId;
        ProducerRecord<String, RemainingMatchId> record = createRecord(validRemainingMatchId);

        ListenableFuture<SendResult<String, RemainingMatchId>> future = failMatchIdKafkaTemplate.send(record);
        future.addCallback(callback(summonerId, beforeLastMatchId));
    }

    @Override
    public void sendBatch(List<Object> remainingMatchIds, String summonerId, String lastMatchId) {

        remainingMatchIds.forEach(remainingMatchId -> send(remainingMatchId, summonerId, lastMatchId));
    }

    @Override
    public void validateDataType(Object data) {
        if(!(data instanceof RemainingMatchId)) {
            log.error("입력 받은 remainingMatchId 데이터 타입이 RemainingMatchId 클래스가 아닙니다.");
            throw new ClassCastException();
        }
    }

    private ProducerRecord<String, RemainingMatchId> createRecord(RemainingMatchId data){
        return new ProducerRecord<>(TOPIC_NAME, data);
    }

    private KafkaSendCallback<String, RemainingMatchId> callback(String summonerId, String beforeLastMatchId){
        return new KafkaSendCallback<>(){
            @Override
            public void onSuccess(SendResult<String, RemainingMatchId> result) {

                log.info("remaining matchId : {} ~ {} 카프카 토픽에 저장 성공!!",
                        result.getProducerRecord().value().getStartMatchId(),
                        result.getProducerRecord().value().getEndMatchId()
                );
            }

            @SuppressWarnings("NullableProblems")
            @Override
            public void onFailure(KafkaProducerException ex) {

                log.error("remaining matchId : {} ~ {} 카프카 토픽에 저장 실패...",
                        ((RemainingMatchId) ex.getFailedProducerRecord().value()).getStartMatchId(),
                        ((RemainingMatchId) ex.getFailedProducerRecord().value()).getEndMatchId()
                );

                summonerService.rollbackLastMatchId(summonerId, beforeLastMatchId);
            }
        };
    }
}
