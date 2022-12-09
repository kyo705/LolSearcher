package com.lolsearcher.unit.service.ingame;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;

import com.lolsearcher.api.riotgames.RiotRestAPI;
import com.lolsearcher.exception.ingame.MoreInGameException;
import com.lolsearcher.exception.ingame.NoInGameException;
import com.lolsearcher.exception.summoner.MoreSummonerException;
import com.lolsearcher.exception.summoner.NoSummonerException;
import com.lolsearcher.model.dto.ingame.InGameDto;
import com.lolsearcher.model.entity.ingame.InGame;
import com.lolsearcher.model.entity.summoner.Summoner;
import com.lolsearcher.repository.ingame.InGameRepository;
import com.lolsearcher.repository.summoner.SummonerRepository;
import com.lolsearcher.service.ingame.InGameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class InGameServiceUnitTest {

	InGameService inGameService;
	@Mock
    SummonerRepository summonerRepository;
	@Mock
    RiotRestAPI riotRestApi;
	@Mock
	InGameRepository inGameRepository;
	
	@BeforeEach
	void upset() {
		inGameService = new InGameService(riotRestApi, inGameRepository, summonerRepository);
	}

	// -----------------getOldInGame 메소트 테스트-------------------------------
	@DisplayName("DB에 인게임 데이터가 없을 경우 예외가 발생한다.")
	@Test
	void getOldInGameWithNoData(){
		//given
		String summonerId = "summonerId";
		given(inGameRepository.getInGamesBySummonerId(summonerId))
				.willReturn(List.of());

		//when & then
		NoInGameException e = assertThrows(NoInGameException.class, ()->{
			inGameService.getOldInGame(summonerId);
		});
		assertThat(e.getExpectedSize()).isEqualTo(1);
		assertThat(e.getActualSize()).isEqualTo(0);
	}

	@DisplayName("DB에 인게임 데이터가 둘 이상 존재할 경우 예외가 발생한다.")
	@ParameterizedTest
	@MethodSource("com.lolsearcher.unit.service.ingame.InGameServiceTestUpSet#getOldInGameForMoreDataParam")
	void getOldInGameWithMoreData(List<InGame> inGames){
		//given
		String summonerId = "summonerId";
		given(inGameRepository.getInGamesBySummonerId(summonerId))
				.willReturn(inGames);

		//when & then
		MoreInGameException e = assertThrows(MoreInGameException.class, ()->{
			inGameService.getOldInGame(summonerId);
		});
		assertThat(e.getExpectedSize()).isEqualTo(1);
		assertThat(e.getActualSize()).isEqualTo(inGames.size());
	}

	@DisplayName("DB에 인게임 데이터가 하나 존재할 경우 해당 데이터를 리턴한다.")
	@Test
	void getOldInGameWithSuccess(){
		//given
		String summonerId = "summonerId";
		InGame inGame = new InGame();
		inGame.setGameId(1);

		given(inGameRepository.getInGamesBySummonerId(summonerId))
				.willReturn(List.of(inGame));

		//when
		InGameDto result = inGameService.getOldInGame(summonerId);

		//then
		assertThat(result.getGameId()).isEqualTo(inGame.getGameId());
	}

	// -----------------getRenewInGame 메소트 테스트-------------------------------

	@DisplayName("게임 서버로부터 api 요청이 실패한 경우 예외가 발생한다.")
	@Test
	void getRenewInGameWithWebClientResponseException(){
		//given
		String summonerId = "summonerId";
		given(riotRestApi.getInGameBySummonerId(summonerId))
				.willThrow(new WebClientResponseException(
						HttpStatus.NOT_FOUND.value(),
						HttpStatus.NOT_FOUND.getReasonPhrase(),
						null,
						null,
						null
				));

		//when & then
		WebClientResponseException e = assertThrows(WebClientResponseException.class, ()->{
			inGameService.getRenewInGame(summonerId);
		});
		assertThat(e.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@DisplayName("주어진 파라미터에 적합한 Summoner가 없을 경우 예외가 발생한다.")
	@Test
	void getRenewInGameWithNoSummoner(){
		//given
		String summonerId = "summonerId";
		InGame inGame = new InGame();
		inGame.setGameId(1);

		given(riotRestApi.getInGameBySummonerId(summonerId)).willReturn(inGame);
		given(summonerRepository.findSummonerById(summonerId)).willThrow(NoResultException.class);

		//when & then
		NoSummonerException e = assertThrows(NoSummonerException.class, ()->{
			inGameService.getRenewInGame(summonerId);
		});
		assertThat(e.getActualSize()).isEqualTo(0);
		assertThat(e.getExpectedSize()).isEqualTo(1);
	}

	@DisplayName("주어진 파라미터에 적합한 Summoner가 둘 이상인 경우 예외가 발생한다.")
	@Test
	void getRenewInGameWithMoreSummoner(){
		//given
		String summonerId = "summonerId";
		InGame inGame = new InGame();
		inGame.setGameId(1);

		given(riotRestApi.getInGameBySummonerId(summonerId)).willReturn(inGame);
		given(summonerRepository.findSummonerById(summonerId)).willThrow(NonUniqueResultException.class);

		//when & then
		MoreSummonerException e = assertThrows(MoreSummonerException.class, ()->{
			inGameService.getRenewInGame(summonerId);
		});
		assertThat(e.getExpectedSize()).isEqualTo(1);
	}

	@DisplayName("주어진 파라미터에 적합한 Summoner가 둘 이상인 경우 예외가 발생한다.")
	@Test
	void getRenewInGameWithSuccess(){
		//given
		String summonerId = "summonerId";
		long beforeTimeStamp = 5000L;

		InGame inGame = new InGame();
		inGame.setGameId(1);

		Summoner summoner = new Summoner();
		summoner.setSummonerId(summonerId);
		summoner.setLastInGameSearchTimeStamp(beforeTimeStamp);

		given(riotRestApi.getInGameBySummonerId(summonerId)).willReturn(inGame);
		given(summonerRepository.findSummonerById(summonerId)).willReturn(summoner);

		//when
		InGameDto inGameDto = inGameService.getRenewInGame(summonerId);

		//then
		assertThat(inGameDto.getGameId()).isEqualTo(inGame.getGameId());
		assertThat(summoner.getLastInGameSearchTimeStamp()).isNotEqualTo(beforeTimeStamp);
		assertThat(summoner.getLastInGameSearchTimeStamp()).isGreaterThan(beforeTimeStamp);
	}

	// -----------------removeDirtyInGame 메소트 테스트-------------------------------

	@DisplayName("DB에 존재하는 이전 인게임 데이터들을 삭제한다.")
	@MethodSource("com.lolsearcher.unit.service.ingame.InGameServiceTestUpSet#removeDirtyInGameParam")
	@ParameterizedTest
	void removeDirtyInGame(
			String summonerId, long inGameId, List<InGame> inGames, List<InGame> removedInGames
	){
		//given
		int deleteCount = inGames.size();
		for(InGame inGame : inGames){
			if(inGame.getGameId() == inGameId){
				deleteCount--;
			}
		}

		given(inGameRepository.getInGamesBySummonerId(summonerId)).willReturn(inGames);
		for(InGame removedInGame : removedInGames){
			willThrow(RuntimeException.class).given(inGameRepository).deleteInGame(removedInGame);
		}

		//when
		inGameService.removeDirtyInGame(summonerId,inGameId);
		//then
		verify(inGameRepository, times(deleteCount)).deleteInGame(any());
	}
}
