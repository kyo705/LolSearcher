package com.lolsearcher.service;

import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaProducerException;
import org.springframework.kafka.core.KafkaSendCallback;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import com.lolsearcher.domain.Dto.ingame.InGameDto;
import com.lolsearcher.domain.entity.ingame.InGame;
import com.lolsearcher.domain.entity.summoner.match.Match;

@Service
public class ThreadService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final ExecutorService executorService;
	private final InGameService inGameService;
	private final KafkaTemplate<String, Match> MatchesKafkaTemplate;
	private final KafkaTemplate<String, String> failMatchIdsKafkaTemplate;
	
	public ThreadService(
			ExecutorService executorService,
			InGameService inGameService,
			KafkaTemplate<String, Match> MatchesKafkaTemplate,
			KafkaTemplate<String, String> failMatchIdsKafkaTemplate) {
		
		this.executorService = executorService;
		this.inGameService = inGameService;
		this.MatchesKafkaTemplate = MatchesKafkaTemplate;
		this.failMatchIdsKafkaTemplate = failMatchIdsKafkaTemplate;
	}
	
	@PreDestroy
	private void preDestroyThreadPool() {
		logger.info("{} shut down", this.executorService.toString());
		executorService.shutdown();
	}

	public void runSavingMatches(List<Match> matches) {
		Runnable saveMatchesToDB = makingRunnableToSaveMatches(matches);
		executorService.submit(saveMatchesToDB);
	}
	
	public void runRemainingMatches(List<String> failMatchIds) {
		Runnable saveRemainingMatches = makingRunnableToSaveRemainingMatches(failMatchIds);
		executorService.submit(saveRemainingMatches);
	}

	public void runSavingInGame(InGameDto inGameDto) {
		executorService.submit(()->{
			InGame inGame = new InGame(inGameDto);
			inGameService.saveNewInGame(inGame);
		});
	}
	
	public void runRemovingDirtyInGame(String summonerId) {
		runRemovingDirtyInGame(summonerId, -1);
	}

	public void runRemovingDirtyInGame(String summonerId, long gameId) {
		executorService.submit(()->{
			inGameService.removeDirtyInGame(summonerId, gameId);
		});
	}
	
	
	private Runnable makingRunnableToSaveMatches(List<Match> matches) {
		Runnable saveMatchesToDB = new Runnable() {
			@Override
			public void run() {
				//카프카로 Matches 보내는 로직 + 저장 컨슈머 읽으라고 rest api 보내기
				String topic_name = "matches";
				int n = matches.size();
				int[] count = new int[1];
				
				for(Match match : matches) {
					ListenableFuture<SendResult<String, Match>> future = MatchesKafkaTemplate.send(topic_name, match);
					
					future.addCallback(new KafkaSendCallback<String, Match>() {

						@Override
						public void onSuccess(SendResult<String, Match> result) {
							count[0]++;
						}

						@Override
						public void onFailure(Throwable ex) {
							count[0]++;
						}

						@Override
						public void onFailure(KafkaProducerException ex) {
						}
					});
				}
				
				while(true) {
					if(n==count[0]) {
						break;
					}else {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				
				//threadService2.saveMatches(matches);
			}
		};
		
		return saveMatchesToDB;
	}
	
	
	private Runnable makingRunnableToSaveRemainingMatches(List<String> matchIds) {
		
		Runnable savingFailMatchIds = new Runnable() {
			
			@Override
			public void run() {
				//카프카로 실패한 matchids 보내는 로직 +  컨슈머 읽으라고 rest api 요청 보내기
				String topic_name = "fail_match_ids";
				int n = matchIds.size();
				int[] count = new int[1];
				
				for(String matchId : matchIds) {
					ListenableFuture<SendResult<String, String>> future = failMatchIdsKafkaTemplate.send(topic_name, matchId);
					
					future.addCallback(new KafkaSendCallback<String, String>() {

						@Override
						public void onSuccess(SendResult<String, String> result) {
							count[0]++;
						}

						@Override
						public void onFailure(Throwable ex) {
							count[0]++;
						}

						@Override
						public void onFailure(KafkaProducerException ex) {
						}
					});
				}
				
				while(true) {
					if(n==count[0]) {
						break;
					}else {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				
			}
		};
		return savingFailMatchIds;
		
		/*Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					System.out.println(Thread.currentThread().getName() + " 스레드 2분 정지");
					Thread.sleep(1000*60*2 + 2000);
					System.out.println(Thread.currentThread().getName() + " 스레드 다시 시작");
				} catch (InterruptedException e2) {
					System.out.println("인터럽트 에러 발생");
				}
				
				//트랜잭션 시작
				
				
				List<Match> matches = new ArrayList<>();
				
				for(int i=start_index; i<matchIds.size();) {
					String matchId = matchIds.get(i);
					
					try {
						Match match = riotApi.getOneMatchByBlocking(matchId);
						matches.add(match);
						i++;
					}catch(WebClientResponseException e1) {
						if(e1.getStatusCode().value()==429) {
							for(Match match : matches) {
								try {
									summonerRepository.saveMatch(match);
								}catch(DataIntegrityViolationException e) {
									//중복 데이터 삽입 시 에러 발생 -> 무시하고 다음 데이터 저장하면 됌 
									System.out.println(e.getLocalizedMessage());
								}
							}
							
							
							try {
								System.out.println(Thread.currentThread().getName() + " 스레드 2분 정지");
								Thread.sleep(1000*60*2+2000);
								System.out.println(Thread.currentThread().getName() + " 스레드 다시 시작");
								matches.clear();
							} catch (InterruptedException e2) {
								e2.printStackTrace();
							}
						}
					}catch(Exception e2) {
						break;
					}
				}
				
				for(Match match : matches) {
					try {
						summonerRepository.saveMatch(match);
					}catch(DataIntegrityViolationException e) {
						//중복 데이터 삽입 시 에러 발생 -> 무시하고 다음 데이터 저장하면 됌 
						System.out.println(e.getLocalizedMessage());
					}
				}
				
				
				//트랜잭션 종료
				
				
			}
		};
		
		return runnable;*/
	}

}
