package com.lolsearcher.unit.service.ingame;

import com.lolsearcher.api.riotgames.RiotRestAPI;
import com.lolsearcher.exception.ingame.NoInGameException;
import com.lolsearcher.model.dto.ingame.InGameDto;
import com.lolsearcher.service.ingame.InGameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class InGameServiceUnitTest {

	InGameService inGameService;
	@Mock
    RiotRestAPI riotRestApi;
	
	@BeforeEach
	void upset() {
		inGameService = new InGameService(riotRestApi);
	}

	@DisplayName("인게임 데이터가 없을 경우 예외가 발생한다.")
	@Test
	void getInGameWithNoData(){
		//given
		String summonerId = "summonerId";
		given(riotRestApi.getInGameBySummonerId(summonerId))
				.willReturn(null);

		//when & then
		NoInGameException e = assertThrows(NoInGameException.class, ()->{
			inGameService.getInGame(summonerId);
		});
		assertThat(e.getExpectedSize()).isEqualTo(1);
		assertThat(e.getActualSize()).isEqualTo(0);
	}

	@DisplayName("API 요청으로 인게임 데이터가 응답될 경우 해당 데이터를 리턴한다.")
	@Test
	void getInGameWithSuccess(){
		//given
		String summonerId = "summonerId";

		InGameDto inGameDto = new InGameDto();
		inGameDto.setGameId(1);
		inGameDto.setGameMode("classic");
		inGameDto.setGameLength(5000);

		given(riotRestApi.getInGameBySummonerId(summonerId))
				.willReturn(inGameDto);

		//when
		InGameDto result = inGameService.getInGame(summonerId);

		//then
		assertThat(result).isEqualTo(inGameDto);
	}

	@DisplayName("REST API 요청이 실패하면 예외가 발생한다.")
	@Test
	void getInGameWithAnyOtherException(){
		//given
		String summonerId = "summonerId";
		given(riotRestApi.getInGameBySummonerId(summonerId))
				.willThrow(
						new WebClientResponseException(
								HttpStatus.TOO_MANY_REQUESTS.value(),
								HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase(),
								null, null, null
						)
				);

		//when & then
		WebClientResponseException e = assertThrows(WebClientResponseException.class, ()->{
			inGameService.getInGame(summonerId);
		});
		assertThat(e.getStatusCode().value()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS.value());
	}
}
