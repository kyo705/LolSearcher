package com.lolsearcher.service.message.producer;

import java.util.List;

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

import com.lolsearcher.model.entity.match.Match;

@Slf4j
@RequiredArgsConstructor
@Service
public class SuccessMatchProducerService implements MessageProducerService {

	@Value("${lolsearcher.kafka.topics.success_match.name}")
	private String TOPIC_NAME;

	private final KafkaTemplate<String, Match> successMatchKafkaTemplate;

	private final SummonerService summonerService;


	@Override
	public void send(Object successMatch, String summonerId, String beforeLastMatchId) {

		validateDataType(successMatch);

		Match validSuccessMatch = (Match) successMatch;
		ProducerRecord<String, Match> record = createRecord(validSuccessMatch);

		ListenableFuture<SendResult<String, Match>> future = successMatchKafkaTemplate.send(record);
		future.addCallback(callback(summonerId, beforeLastMatchId));
	}

	@Override
	public void sendBatch(List<Object> data, String summonerId, String lastMatchId) {

	}

	@Override
	public void validateDataType(Object data) {

		if(!(data instanceof Match)){
			log.error("입력 받은 successMatch 데이터 타입이 Match 클래스가 아닙니다.");
			throw new ClassCastException();
		}
	}

	private ProducerRecord<String, Match> createRecord(Match match){
		return new ProducerRecord<>(TOPIC_NAME, match);
	}

	private KafkaSendCallback<String, Match> callback(String summonerId, String beforeLastMatchId) {
		return new KafkaSendCallback<>(){
			@Override
			public void onSuccess(SendResult<String, Match> result) {
				log.info("success match : {} 카프카 토픽에 저장 성공!!",
						result.getProducerRecord().value().getMatchId());
			}

			@SuppressWarnings("NullableProblems")
			@Override
			public void onFailure(KafkaProducerException ex) {
				log.error("success match : {} 카프카 토픽에 저장 실패...",
						((Match)ex.getFailedProducerRecord().value()).getMatchId());

				summonerService.rollbackLastMatchId(summonerId, beforeLastMatchId);
			}
		};
	}
}
