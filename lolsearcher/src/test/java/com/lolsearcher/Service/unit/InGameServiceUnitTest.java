package com.lolsearcher.Service.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.lolsearcher.domain.Dto.ingame.InGameDto;
import com.lolsearcher.domain.Dto.summoner.SummonerDto;
import com.lolsearcher.domain.entity.ingame.InGame;
import com.lolsearcher.domain.entity.summoner.Summoner;
import com.lolsearcher.repository.SummonerRepository.SummonerRepository;
import com.lolsearcher.repository.ingamerepository.IngameRepository;
import com.lolsearcher.restapi.RiotRestAPI;
import com.lolsearcher.service.InGameService;

@ExtendWith(MockitoExtension.class)
public class InGameServiceUnitTest {

	InGameService inGameService;
	@Mock 
	SummonerRepository summonerRepository;
	@Mock 
	RiotRestAPI riotRestApi;
	@Mock
	IngameRepository inGameRepository;
	
	@BeforeEach
	void upset() {
		inGameService = new InGameService( riotRestApi, inGameRepository, summonerRepository);
	}
	
	
	//----------------------getInGame() 메소드 Test Case------------------------------------
	
	@Test
	void getInGameCase1() {
		//test Case 1 : 최근 인게임 정보 확인한 시간이 현재 시각으로부터 2분 이하일 때
		//				최근 저장된 데이터가 하나 있을 경우
		
		//given
		SummonerDto summonerDto = new SummonerDto();
		summonerDto.setSummonerid("Id1");
		summonerDto.setName("푸켓푸켓");
		
		Summoner summoner = new Summoner();
		summoner.setId("Id1");
		summoner.setName("푸켓푸켓");
		summoner.setLastInGameSearchTimeStamp(System.currentTimeMillis());
		
		when(summonerRepository.findSummonerById(summonerDto.getSummonerid()))
		.thenReturn(summoner);
		
		
		InGame ingame1 = new InGame();
		ingame1.setGameId(1);
		List<InGame> ingames = new ArrayList<>();
		ingames.add(ingame1);
		
		when(inGameRepository.getIngame(summoner.getId()))
		.thenReturn(ingames);
		
		//when
		InGameDto ingameDto = inGameService.getInGame(summonerDto);
		
		//then
		assertThat(ingameDto.getGameId()).isEqualTo(ingame1.getGameId());
		
		verify(riotRestApi, times(0)).getSummonerById(anyString()); //REST API 통신 일어나면 안됌
		verify(inGameRepository, times(1)).getIngame(anyString());  //인게임 정보 DB에 조회 1번 수행해야함
	}
	
	@Test
	void getInGameCase2() {
		//test Case 2 : 최근 인게임 정보 확인한 시간이 현재 시각으로부터 2분 이하일 때
		//				최근 저장된 데이터가 둘 이상 있을 경우
		
		//given
		SummonerDto summonerDto = new SummonerDto();
		summonerDto.setSummonerid("Id1");
		summonerDto.setName("푸켓푸켓");
		
		Summoner summoner = new Summoner();
		summoner.setId("Id1");
		summoner.setName("푸켓푸켓");
		summoner.setLastInGameSearchTimeStamp(System.currentTimeMillis());
		
		when(summonerRepository.findSummonerById(summonerDto.getSummonerid()))
		.thenReturn(summoner);
		
		
		InGame ingame2 = new InGame();
		ingame2.setGameId(2);
		InGame ingame1 = new InGame();
		ingame1.setGameId(1);
		List<InGame> ingames = new ArrayList<>();
		ingames.add(ingame2);
		ingames.add(ingame1);
		
		when(inGameRepository.getIngame(summoner.getId()))
		.thenReturn(ingames);
		
		//when
		InGameDto ingameDto = inGameService.getInGame(summonerDto);
		
		//then
		assertThat(ingameDto.getGameId()).isEqualTo(ingame2.getGameId()); //최신 데이터 가져옴(리스트의 첫번째 값)
		
		verify(riotRestApi, times(0)).getSummonerById(anyString()); //REST API 통신 일어나면 안됌
		verify(inGameRepository, times(1)).getIngame(anyString());  //인게임 정보 DB에 조회 1번 수행해야함
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
		
		when(summonerRepository.findSummonerById(summonerDto.getSummonerid()))
		.thenReturn(summoner);
		
		
		List<InGame> ingames = new ArrayList<>();
		
		when(inGameRepository.getIngame(summoner.getId()))
		.thenReturn(ingames);
		
		//when
		InGameDto ingameDto = inGameService.getInGame(summonerDto);
		
		//then
		assertThat(ingameDto).isEqualTo(null);  //DB로부터 조회된 데이터 없음
		
		verify(riotRestApi, times(0)).getSummonerById(anyString()); //REST API 통신 일어나면 안됌
		verify(inGameRepository, times(1)).getIngame(anyString());  //인게임 정보 DB에 조회 1번 수행해야함
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
		verify(inGameRepository, times(0)).getIngame(anyString());  //인게임 정보 DB에 조회하면 안됌
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
		verify(inGameRepository, times(0)).getIngame(anyString());  //인게임 정보 DB에 조회하면 안됌
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
		
		when(inGameRepository.getIngame(summonerId))
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
		
		when(inGameRepository.getIngame(summonerId))
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
		
		when(inGameRepository.getIngame(summonerId))
		.thenReturn(inGames);
		
		//Mock 객체 리턴 값 2
		doThrow(new DataIntegrityViolationException("이미 존재하지 않는 엔티티입니다."))
		.when(inGameRepository)
		.deleteIngame(ingame1);
		
		//when
		inGameService.removeDirtyInGame(summonerId, currentGameId);
				
		//then
		verify(inGameRepository, times(2)).deleteIngame(any()); //이미 끝난 게임 정보들은 삭제함
		verify(inGameRepository, times(0)).deleteIngame(ingame3); //현재 실행 중인 게임은 삭제되지 않음
	}
}
