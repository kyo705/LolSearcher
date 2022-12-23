package com.lolsearcher.service.producer;

import java.util.List;

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

import com.lolsearcher.model.entity.match.Match;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Slf4j
@RequiredArgsConstructor
@Service
public class SuccessMatchProducerService implements ProducerService<Match> {

	@Value("${lolsearcher.kafka.topics.success_match.name}")
	private String TOPIC_NAME;

	private final KafkaTemplate<String, Match> successMatchKafkaTemplate;


	@Transactional(transactionManager = "successMatchKafkaTransactionManager")
	@Override
	public void send(Match successMatch) {

		ProducerRecord<String, Match> record = createRecord(successMatch);

		ListenableFuture<SendResult<String, Match>> future = successMatchKafkaTemplate.send(record);

		future.addCallback(callback());
	}



	@Transactional(transactionManager = "successMatchKafkaTransactionManager")
	@Override
	public void sendBatch(List<Match> successMatches) {

		for(Match successMatch : successMatches){
			ProducerRecord<String, Match> record = createRecord(successMatch);

			ListenableFuture<SendResult<String, Match>> future = successMatchKafkaTemplate.send(record);

			future.addCallback(callback());
		}
	}

	private ProducerRecord<String, Match> createRecord(Match match){
		return new ProducerRecord<>(TOPIC_NAME, match);
	}

	private ListenableFutureCallback<? super SendResult<String, Match>> callback() {
		return new KafkaSendCallback<>(){

			@Override
			public void onSuccess(SendResult<String, Match> result) {
				log.info("success match : {} 카프카 토픽에 저장 성공!!",
						result.getProducerRecord().value().getMatchId()
				);
			}

			@Override
			public void onFailure(KafkaProducerException ex) {
				log.error("success match : {} 카프카 토픽에 저장 실패...",
						((Match)ex.getFailedProducerRecord().value()).getMatchId()
				);
			}
		};
	}
}
