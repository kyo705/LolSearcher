package com.lolsearcher.service.producer;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaProducerException;
import org.springframework.kafka.core.KafkaSendCallback;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import com.lolsearcher.model.entity.match.Match;

@RequiredArgsConstructor
@Service
public class MessageProducingService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final KafkaTemplate<String, Match> MatchesKafkaTemplate;
	private final KafkaTemplate<String, String> failMatchIdsKafkaTemplate;

	public void saveSuccessMatches(List<Match> matches) {
		//카프카로 Matches 보내는 로직 + 저장 컨슈머 읽으라고 rest api 보내기
		String topicName = "matches";
		
		int size = matches.size();
		int[] count = new int[1];
		
		for(Match match : matches) {
			ListenableFuture<SendResult<String, Match>> future = MatchesKafkaTemplate.send(topicName, match);
			
			future.addCallback(new KafkaSendCallback<>() {
				@Override
				public void onSuccess(SendResult<String, Match> result) {
					count[0]++;
				}
				@Override
				public void onFailure(Throwable ex) {
					count[0]++;
				}
				@Override
				public void onFailure(KafkaProducerException ex) {}
			});
		}
		while(size > count[0]) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void saveFailMatchIds(List<String> failMatchIds) {
		String topicName = "fail_match_ids";
		
		int size = failMatchIds.size();
		int[] count = new int[1];
		
		for(String failMatchId : failMatchIds) {
			ListenableFuture<SendResult<String, String>> future = failMatchIdsKafkaTemplate.send(topicName, failMatchId);
			
			future.addCallback(new KafkaSendCallback<>() {
				@Override
				public void onSuccess(SendResult<String, String> result) {
					count[0]++;
				}
				@Override
				public void onFailure(Throwable ex) {
					count[0]++;
				}
				@Override
				public void onFailure(KafkaProducerException ex) {}
			});
		}
		
		while(size > count[0]) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
