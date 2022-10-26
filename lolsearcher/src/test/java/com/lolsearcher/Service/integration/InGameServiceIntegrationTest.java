package com.lolsearcher.Service.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.lolsearcher.domain.Dto.ingame.InGameDto;
import com.lolsearcher.domain.Dto.summoner.SummonerDto;
import com.lolsearcher.domain.entity.ingame.BannedChampion;
import com.lolsearcher.domain.entity.ingame.BannedChampionCompKey;
import com.lolsearcher.domain.entity.ingame.CurrentGameParticipant;
import com.lolsearcher.domain.entity.ingame.CurrentParticipantCompKey;
import com.lolsearcher.domain.entity.ingame.InGame;
import com.lolsearcher.domain.entity.summoner.Summoner;
import com.lolsearcher.repository.SummonerRepository.SummonerRepository;
import com.lolsearcher.repository.ingamerepository.IngameRepository;
import com.lolsearcher.restapi.RiotRestAPI;
import com.lolsearcher.service.InGameService;

@ActiveProfiles("test")
@SpringBootTest
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class InGameServiceIntegrationTest {

	@Autowired
	InGameService inGameService;
	@Autowired
	SummonerRepository summonerRepository;
	@Mock
	RiotRestAPI riotRestApi;
	@Autowired
	IngameRepository inGameRepository;
	@Autowired
	EntityManager em;
	@Autowired
	ApplicationContext applicationContext;
	
	@Test
	void getInGameCase1() {
		//test Case 1 : 최근 인게임 정보 확인한 시간이 현재 시각으로부터 2분 이하일 때
		//				최근 저장된 데이터가 하나 있을 경우
		
		//given
		//실행될 메소드 파라미터 값
		SummonerDto summonerDto = new SummonerDto();
		summonerDto.setSummonerid("summoner1");
		summonerDto.setName("푸켓푸켓");
		
		//DB 데이터 셋팅
		Summoner summoner = new Summoner();
		summoner.setId("summoner1");
		summoner.setName("푸켓푸켓");
		summoner.setLastInGameSearchTimeStamp(System.currentTimeMillis());
		summonerRepository.saveSummoner(summoner);
		
		InGame ingame1 = new InGame();
		ingame1.setGameId(1);
		BannedChampion bannedChamp1 = new BannedChampion();
		bannedChamp1.setCk(new BannedChampionCompKey(1, 0));
		bannedChamp1.setIngame(ingame1);
		BannedChampion bannedChamp2 = new BannedChampion();
		bannedChamp2.setCk(new BannedChampionCompKey(1, 1));
		bannedChamp2.setIngame(ingame1);
		BannedChampion bannedChamp3 = new BannedChampion();
		bannedChamp3.setCk(new BannedChampionCompKey(1, 2));
		bannedChamp3.setIngame(ingame1);
		BannedChampion bannedChamp4 = new BannedChampion();
		bannedChamp4.setCk(new BannedChampionCompKey(1, 3));
		bannedChamp4.setIngame(ingame1);
		BannedChampion bannedChamp5 = new BannedChampion();
		bannedChamp5.setCk(new BannedChampionCompKey(1, 4));
		bannedChamp5.setIngame(ingame1);
		BannedChampion bannedChamp6 = new BannedChampion();
		bannedChamp6.setCk(new BannedChampionCompKey(1, 5));
		bannedChamp6.setIngame(ingame1);
		BannedChampion bannedChamp7 = new BannedChampion();
		bannedChamp7.setCk(new BannedChampionCompKey(1, 6));
		bannedChamp7.setIngame(ingame1);
		BannedChampion bannedChamp8 = new BannedChampion();
		bannedChamp8.setCk(new BannedChampionCompKey(1, 7));
		bannedChamp8.setIngame(ingame1);
		BannedChampion bannedChamp9 = new BannedChampion();
		bannedChamp9.setCk(new BannedChampionCompKey(1, 8));
		bannedChamp9.setIngame(ingame1);
		BannedChampion bannedChamp10 = new BannedChampion();
		bannedChamp10.setCk(new BannedChampionCompKey(1, 9));
		bannedChamp10.setIngame(ingame1);
		CurrentGameParticipant curParti1 = new CurrentGameParticipant();
		curParti1.setCk(new CurrentParticipantCompKey(1, "summonerId1"));
		curParti1.setIngame(ingame1);
		CurrentGameParticipant curParti2 = new CurrentGameParticipant();
		curParti2.setCk(new CurrentParticipantCompKey(1, "summonerId2"));
		curParti2.setIngame(ingame1);
		CurrentGameParticipant curParti3 = new CurrentGameParticipant();
		curParti3.setCk(new CurrentParticipantCompKey(1, "summonerId3"));
		curParti3.setIngame(ingame1);
		CurrentGameParticipant curParti4 = new CurrentGameParticipant();
		curParti4.setCk(new CurrentParticipantCompKey(1, "summonerId4"));
		curParti4.setIngame(ingame1);
		CurrentGameParticipant curParti5 = new CurrentGameParticipant();
		curParti5.setCk(new CurrentParticipantCompKey(1, "summonerId5"));
		curParti5.setIngame(ingame1);
		CurrentGameParticipant curParti6 = new CurrentGameParticipant();
		curParti6.setCk(new CurrentParticipantCompKey(1, "summonerId6"));
		curParti6.setIngame(ingame1);
		CurrentGameParticipant curParti7 = new CurrentGameParticipant();
		curParti7.setCk(new CurrentParticipantCompKey(1, "summonerId7"));
		curParti7.setIngame(ingame1);
		CurrentGameParticipant curParti8 = new CurrentGameParticipant();
		curParti8.setCk(new CurrentParticipantCompKey(1, "summonerId8"));
		curParti8.setIngame(ingame1);
		CurrentGameParticipant curParti9 = new CurrentGameParticipant();
		curParti9.setCk(new CurrentParticipantCompKey(1, "summonerId9"));
		curParti9.setIngame(ingame1);
		CurrentGameParticipant curParti10 = new CurrentGameParticipant();
		curParti10.setCk(new CurrentParticipantCompKey(1, "summonerId10"));
		curParti10.setIngame(ingame1);
		inGameRepository.saveIngame(ingame1);
		em.flush();
		em.clear();
		
		//when
		InGameDto ingameDto = inGameService.getInGame(summonerDto); // n+1문제가 발생 안하는지 확인
		em.flush();
		em.clear();
		
		//then
		assertThat(ingameDto.getGameId()).isEqualTo(1); //최신 데이터 가져옴(리스트의 첫번째 값)
		assertThat(ingameDto.getBannedChampions().size()).isEqualTo(10);
		assertThat(ingameDto.getBannedChampions().get(0).getPickTurn()).isEqualTo(0);
		assertThat(ingameDto.getBannedChampions().get(3).getPickTurn()).isEqualTo(3);
		assertThat(ingameDto.getParticipants().size()).isEqualTo(10);
		assertThat(ingameDto.getParticipants().get(1).getSummonerId()).isEqualTo("summoner2");
		assertThat(ingameDto.getParticipants().get(3).getSummonerId()).isEqualTo("summoner4");
	}
	
	@Test
	void getInGameCase2() {
		//test Case 2 : 최근 인게임 정보 확인한 시간이 현재 시각으로부터 2분 이하일 때
		//				최근 저장된 데이터가 둘 이상 있을 경우
		
		//given
		//실행될 메소드 파라미터 값
		SummonerDto summonerDto = new SummonerDto();
		summonerDto.setSummonerid("summoner1");
		summonerDto.setName("푸켓푸켓");
		
		//DB 데이터 셋팅
		Summoner summoner = new Summoner();
		summoner.setId("summoner1");
		summoner.setName("푸켓푸켓");
		summoner.setLastInGameSearchTimeStamp(System.currentTimeMillis());
		summonerRepository.saveSummoner(summoner);
		
		InGame ingame1 = new InGame();
		ingame1.setGameId(1);
		ingame1.setGameStartTime(1000);
		BannedChampion bannedChamp1 = new BannedChampion();
		bannedChamp1.setCk(new BannedChampionCompKey(1, 0));
		bannedChamp1.setIngame(ingame1);
		BannedChampion bannedChamp2 = new BannedChampion();
		bannedChamp2.setCk(new BannedChampionCompKey(1, 1));
		bannedChamp2.setIngame(ingame1);
		BannedChampion bannedChamp3 = new BannedChampion();
		bannedChamp3.setCk(new BannedChampionCompKey(1, 2));
		bannedChamp3.setIngame(ingame1);
		BannedChampion bannedChamp4 = new BannedChampion();
		bannedChamp4.setCk(new BannedChampionCompKey(1, 3));
		bannedChamp4.setIngame(ingame1);
		BannedChampion bannedChamp5 = new BannedChampion();
		bannedChamp5.setCk(new BannedChampionCompKey(1, 4));
		bannedChamp5.setIngame(ingame1);
		BannedChampion bannedChamp6 = new BannedChampion();
		bannedChamp6.setCk(new BannedChampionCompKey(1, 5));
		bannedChamp6.setIngame(ingame1);
		BannedChampion bannedChamp7 = new BannedChampion();
		bannedChamp7.setCk(new BannedChampionCompKey(1, 6));
		bannedChamp7.setIngame(ingame1);
		BannedChampion bannedChamp8 = new BannedChampion();
		bannedChamp8.setCk(new BannedChampionCompKey(1, 7));
		bannedChamp8.setIngame(ingame1);
		BannedChampion bannedChamp9 = new BannedChampion();
		bannedChamp9.setCk(new BannedChampionCompKey(1, 8));
		bannedChamp9.setIngame(ingame1);
		BannedChampion bannedChamp10 = new BannedChampion();
		bannedChamp10.setCk(new BannedChampionCompKey(1, 9));
		bannedChamp10.setIngame(ingame1);
		CurrentGameParticipant curParti1 = new CurrentGameParticipant();
		curParti1.setCk(new CurrentParticipantCompKey(1, "summonerId1"));
		curParti1.setIngame(ingame1);
		CurrentGameParticipant curParti2 = new CurrentGameParticipant();
		curParti2.setCk(new CurrentParticipantCompKey(1, "summonerId2"));
		curParti2.setIngame(ingame1);
		CurrentGameParticipant curParti3 = new CurrentGameParticipant();
		curParti3.setCk(new CurrentParticipantCompKey(1, "summonerId3"));
		curParti3.setIngame(ingame1);
		CurrentGameParticipant curParti4 = new CurrentGameParticipant();
		curParti4.setCk(new CurrentParticipantCompKey(1, "summonerId4"));
		curParti4.setIngame(ingame1);
		CurrentGameParticipant curParti5 = new CurrentGameParticipant();
		curParti5.setCk(new CurrentParticipantCompKey(1, "summonerId5"));
		curParti5.setIngame(ingame1);
		CurrentGameParticipant curParti6 = new CurrentGameParticipant();
		curParti6.setCk(new CurrentParticipantCompKey(1, "summonerId6"));
		curParti6.setIngame(ingame1);
		CurrentGameParticipant curParti7 = new CurrentGameParticipant();
		curParti7.setCk(new CurrentParticipantCompKey(1, "summonerId7"));
		curParti7.setIngame(ingame1);
		CurrentGameParticipant curParti8 = new CurrentGameParticipant();
		curParti8.setCk(new CurrentParticipantCompKey(1, "summonerId8"));
		curParti8.setIngame(ingame1);
		CurrentGameParticipant curParti9 = new CurrentGameParticipant();
		curParti9.setCk(new CurrentParticipantCompKey(1, "summonerId9"));
		curParti9.setIngame(ingame1);
		CurrentGameParticipant curParti10 = new CurrentGameParticipant();
		curParti10.setCk(new CurrentParticipantCompKey(1, "summonerId10"));
		curParti10.setIngame(ingame1);
		inGameRepository.saveIngame(ingame1);
		
		InGame ingame2 = new InGame();
		ingame2.setGameId(3);
		ingame2.setGameStartTime(1700);
		BannedChampion bannedChamp2_1 = new BannedChampion();
		bannedChamp2_1.setCk(new BannedChampionCompKey(3, 0));
		bannedChamp2_1.setIngame(ingame2);
		BannedChampion bannedChamp2_2 = new BannedChampion();
		bannedChamp2_2.setCk(new BannedChampionCompKey(3, 1));
		bannedChamp2_2.setIngame(ingame2);
		BannedChampion bannedChamp2_3 = new BannedChampion();
		bannedChamp2_3.setCk(new BannedChampionCompKey(3, 2));
		bannedChamp2_3.setIngame(ingame2);
		BannedChampion bannedChamp2_4 = new BannedChampion();
		bannedChamp2_4.setCk(new BannedChampionCompKey(3, 3));
		bannedChamp2_4.setIngame(ingame2);
		BannedChampion bannedChamp2_5 = new BannedChampion();
		bannedChamp2_5.setCk(new BannedChampionCompKey(3, 4));
		bannedChamp2_5.setIngame(ingame2);
		BannedChampion bannedChamp2_6 = new BannedChampion();
		bannedChamp2_6.setCk(new BannedChampionCompKey(3, 5));
		bannedChamp2_6.setIngame(ingame2);
		BannedChampion bannedChamp2_7 = new BannedChampion();
		bannedChamp2_7.setCk(new BannedChampionCompKey(3, 6));
		bannedChamp2_7.setIngame(ingame2);
		BannedChampion bannedChamp2_8 = new BannedChampion();
		bannedChamp2_8.setCk(new BannedChampionCompKey(3, 7));
		bannedChamp2_8.setIngame(ingame2);
		BannedChampion bannedChamp2_9 = new BannedChampion();
		bannedChamp2_9.setCk(new BannedChampionCompKey(3, 8));
		bannedChamp2_9.setIngame(ingame2);
		BannedChampion bannedChamp2_10 = new BannedChampion();
		bannedChamp2_10.setCk(new BannedChampionCompKey(3, 9));
		bannedChamp2_10.setIngame(ingame2);
		CurrentGameParticipant curParti2_1 = new CurrentGameParticipant();
		curParti2_1.setCk(new CurrentParticipantCompKey(3, "summonerId1"));
		curParti2_1.setIngame(ingame2);
		CurrentGameParticipant curParti2_2 = new CurrentGameParticipant();
		curParti2_2.setCk(new CurrentParticipantCompKey(3, "summonerId2"));
		curParti2_2.setIngame(ingame2);
		CurrentGameParticipant curParti2_3 = new CurrentGameParticipant();
		curParti2_3.setCk(new CurrentParticipantCompKey(3, "summonerId3"));
		curParti2_3.setIngame(ingame2);
		CurrentGameParticipant curParti2_4 = new CurrentGameParticipant();
		curParti2_4.setCk(new CurrentParticipantCompKey(3, "summonerId4"));
		curParti2_4.setIngame(ingame2);
		CurrentGameParticipant curParti2_5 = new CurrentGameParticipant();
		curParti2_5.setCk(new CurrentParticipantCompKey(3, "summonerId5"));
		curParti2_5.setIngame(ingame2);
		CurrentGameParticipant curParti2_6 = new CurrentGameParticipant();
		curParti2_6.setCk(new CurrentParticipantCompKey(3, "summonerId6"));
		curParti2_6.setIngame(ingame2);
		CurrentGameParticipant curParti2_7 = new CurrentGameParticipant();
		curParti2_7.setCk(new CurrentParticipantCompKey(3, "summonerId7"));
		curParti2_7.setIngame(ingame2);
		CurrentGameParticipant curParti2_8 = new CurrentGameParticipant();
		curParti2_8.setCk(new CurrentParticipantCompKey(3, "summonerId8"));
		curParti2_8.setIngame(ingame2);
		CurrentGameParticipant curParti2_9 = new CurrentGameParticipant();
		curParti2_9.setCk(new CurrentParticipantCompKey(3, "summonerId9"));
		curParti2_9.setIngame(ingame2);
		CurrentGameParticipant curParti2_10 = new CurrentGameParticipant();
		curParti2_10.setCk(new CurrentParticipantCompKey(3, "summonerId10"));
		curParti2_10.setIngame(ingame2);
		inGameRepository.saveIngame(ingame2);
		em.flush();
		em.clear();
		
		//when
		InGameDto ingameDto = inGameService.getInGame(summonerDto); // n+1문제가 발생 안하는지 확인
		em.flush();
		em.clear();
		
		//then
		assertThat(ingameDto.getGameId()).isEqualTo(3); //최신 데이터 가져옴(리스트의 첫번째 값)
		assertThat(ingameDto.getBannedChampions().size()).isEqualTo(10);
		assertThat(ingameDto.getBannedChampions().get(0).getPickTurn()).isEqualTo(0);
		assertThat(ingameDto.getBannedChampions().get(3).getPickTurn()).isEqualTo(3);
		assertThat(ingameDto.getParticipants().size()).isEqualTo(10);
		assertThat(ingameDto.getParticipants().get(1).getSummonerId()).isEqualTo("summoner2");
		assertThat(ingameDto.getParticipants().get(3).getSummonerId()).isEqualTo("summoner4");
	}
	
	@Test
	void getInGameCase3() {
		//test Case 3 : 최근 인게임 정보 확인한 시간이 현재 시각으로부터 2분 이하일 때
		//				최근 저장된 데이터가 없을 경우
		
		//given
		SummonerDto summonerDto = new SummonerDto();
		summonerDto.setSummonerid("Id1");
		summonerDto.setName("푸켓푸켓");
		
		Summoner summoner = new Summoner();
		summoner.setId("Id1");
		summoner.setName("푸켓푸켓");
		summoner.setLastInGameSearchTimeStamp(System.currentTimeMillis());
		summonerRepository.saveSummoner(summoner);
		em.flush();
		em.clear();
		
		//when
		InGameDto ingameDto = inGameService.getInGame(summonerDto);
		em.flush();
		em.clear();
		
		//then
		assertThat(ingameDto).isEqualTo(null);  //DB로부터 조회된 데이터 없음
	}
	
	@Test
	void getInGameCase4() {
		//test Case 4 : 최근 인게임 정보 확인한 시간이 현재 시각으로부터 2분 이상일 때
		//				REST API 로 인게임 데이터를 전달받은 경우 
		
		//given
		//전달받은 파라미터
		SummonerDto summonerDto = new SummonerDto();
		summonerDto.setSummonerid("Id1");
		summonerDto.setName("푸켓푸켓");
		//Mock 객체 리턴 값1
		Summoner summoner = new Summoner();
		summoner.setId("Id1");
		summoner.setName("푸켓푸켓");
		summoner.setLastInGameSearchTimeStamp(System.currentTimeMillis()-3*60*1000);
		
		when(summonerRepository.findSummonerById(summonerDto.getSummonerid()))
		.thenReturn(summoner);
		
		//Mock 객체 리턴 값2
		InGameDto apiInGameDto = new InGameDto();
		apiInGameDto.setGameId(1);
		when(riotRestApi.getInGameBySummonerId(summoner.getId()))
		.thenReturn(apiInGameDto);
		
		//when
		InGameDto ingameDto = inGameService.getInGame(summonerDto);
		
		//then
		assertThat(ingameDto).isEqualTo(apiInGameDto);  //반환받은 객체는 api로부터 받은 객체와 동일해야함
		assertThat(System.currentTimeMillis() - summoner.getLastInGameSearchTimeStamp()< 2*60*1000)
		.isEqualTo(true); //summoner 객체의 최신 인게임조회 타임스탬프 값을 갱신했는지 확인
		
		verify(riotRestApi, times(1)).getInGameBySummonerId(summoner.getId()); //REST API 통신 1회 일어나야함
		verify(inGameRepository, times(0)).getInGamesBySummonerId(anyString());  //인게임 정보 DB에 조회하면 안됌
	}
	
	@Test
	void getInGameCase5() {
		//test Case 5 : 최근 인게임 정보 확인한 시간이 현재 시각으로부터 2분 이상일 때
		//				REST API 로 인게임 데이터를 전달받지 못한 경우 
		
		//given
		//전달받은 파라미터
		SummonerDto summonerDto = new SummonerDto();
		summonerDto.setSummonerid("Id1");
		summonerDto.setName("푸켓푸켓");
		//Mock 객체 리턴 값1
		Summoner summoner = new Summoner();
		summoner.setId("Id1");
		summoner.setName("푸켓푸켓");
		summoner.setLastInGameSearchTimeStamp(System.currentTimeMillis()-3*60*1000);
		
		when(summonerRepository.findSummonerById(summonerDto.getSummonerid()))
		.thenReturn(summoner);
		
		//Mock 객체 리턴 값2
		when(riotRestApi.getInGameBySummonerId(anyString()))
		.thenThrow(new WebClientResponseException(404, "해당 조건에 맞는 정보가 없습니다.", null, null, null));
		
		//when & then
		WebClientResponseException e = assertThrows(WebClientResponseException.class,
				()->inGameService.getInGame(summonerDto));
		assertThat(e.getStatusCode().value()).isEqualTo(404);
		assertThat(e.getStatusText()).isEqualTo("해당 조건에 맞는 정보가 없습니다.");
		
		verify(riotRestApi, times(1)).getInGameBySummonerId(summoner.getId()); //REST API 통신 1회 일어나야함
		verify(inGameRepository, times(0)).getInGamesBySummonerId(anyString());  //인게임 정보 DB에 조회하면 안됌
	}
	
	
	//----------------------removeDirtyInGame() 메소드 Test Case------------------------------------
	
	@Test
	void removeDirtyInGameCase1() {
		//test Case 1 : 현재 진행 중인 게임이 있을 경우
		
		//given
		//전달받은 파라미터
		String summonerId = "summonerId1";
		long currentGameId = 3;
		
		//Mock 객체 리턴 값 1
		InGame ingame3 = new InGame();
		ingame3.setGameId(3);
		InGame ingame2 = new InGame();
		ingame2.setGameId(2);
		InGame ingame1 = new InGame();
		ingame1.setGameId(1);
		
		List<InGame> inGames = new ArrayList<>();
		inGames.add(ingame3);
		inGames.add(ingame2);
		inGames.add(ingame1);
		
		when(inGameRepository.getInGamesBySummonerId(summonerId))
		.thenReturn(inGames);
		
		//when
		inGameService.removeDirtyInGame(summonerId, currentGameId);
		
		//then
		verify(inGameRepository, times(2)).deleteIngame(any()); //이미 끝난 게임 정보들은 삭제함
		verify(inGameRepository, times(0)).deleteIngame(ingame3); //현재 진행 중인 게임 정보는 삭제하면 안됌
	}
	
	@Test
	void removeDirtyInGameCase2() {
		//test Case 2 : 현재 진행 중인 게임이 없을 경우
		
		//given
		//전달받은 파라미터
		String summonerId = "summonerId1";
		long currentGameId = -1;
		
		//Mock 객체 리턴 값 1
		InGame ingame3 = new InGame();
		ingame3.setGameId(3);
		InGame ingame2 = new InGame();
		ingame2.setGameId(2);
		InGame ingame1 = new InGame();
		ingame1.setGameId(1);
		
		List<InGame> inGames = new ArrayList<>();
		inGames.add(ingame3);
		inGames.add(ingame2);
		inGames.add(ingame1);
		
		when(inGameRepository.getInGamesBySummonerId(summonerId))
		.thenReturn(inGames);
		
		//when
		inGameService.removeDirtyInGame(summonerId, currentGameId);
				
		//then
		verify(inGameRepository, times(3)).deleteIngame(any()); //이미 끝난 게임 정보들은 삭제함
	}
	
	@Test
	void removeDirtyInGameCase3() {
		//test Case 3 : 제거해야할 인게임 데이터들을 가져왔으나 멀티 스레드 환경에서 다른 스레드가 기존 데이터를 삭제해
		//				delete 쿼리문이 오류날 경우
		
		//given
		//전달받은 파라미터
		String summonerId = "summonerId1";
		long currentGameId = 2;
		
		//DB 데이터 셋팅
		InGame ingame1 = new InGame();
		ingame1.setGameId(1);
		CurrentGameParticipant curParti1 = new CurrentGameParticipant();
		curParti1.setCk(new CurrentParticipantCompKey(1, "summonerId1"));
		curParti1.setIngame(ingame1);
		InGame ingame2 = new InGame();
		ingame2.setGameId(2);
		CurrentGameParticipant curParti2 = new CurrentGameParticipant();
		curParti2.setCk(new CurrentParticipantCompKey(2, "summonerId1"));
		curParti2.setIngame(ingame2);
		inGameRepository.saveIngame(ingame1);
		inGameRepository.saveIngame(ingame2);
		em.flush();
		em.clear();
		
		
		//when
		Thread thread = new Thread(()->{
			System.out.println("스레드 시작");
			EntityManagerFactory emf =  applicationContext.getBean(EntityManagerFactory.class);
			EntityManager em1 = emf.createEntityManager();
			
			EntityTransaction et = em1.getTransaction();
			et.begin();
			
			InGame ig = em1.find(InGame.class, 1l);
			System.out.println(ig.getGameId());
			
			em1.createQuery("delete from InGame i where i.gameId = :gameId")
			.setParameter("gameId", 1l)
			.executeUpdate();
			
			System.out.println("hi");
			em1.flush();
			
			et.commit();
			
			em1.close();
			System.out.println("스레드 종료");
		});
		thread.start();
		//inGameService.removeDirtyInGame(summonerId, currentGameId);
		//em.flush();
		//em.clear();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		//then
		List<InGame> ingames = inGameRepository.getInGamesBySummonerId(summonerId);
		assertThat(ingames.size()).isEqualTo(1);
		assertThat(ingames.get(0).getGameId()).isEqualTo(2);
	}
}
