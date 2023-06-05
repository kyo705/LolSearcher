package com.lolsearcher.search.summoner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.config.EmbeddedRedisConfig;
import com.lolsearcher.errors.exception.summoner.NotExistedSummonerInDBException;
import com.lolsearcher.errors.exception.summoner.NotExistedSummonerInGameServerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.Map;

import static com.lolsearcher.config.ErrorResponseEntityConfig.*;
import static com.lolsearcher.search.summoner.SummonerConstant.FIND_BY_NAME_URI;
import static com.lolsearcher.search.summoner.SummonerConstant.SUMMONER_NAME_REGEX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({EmbeddedRedisConfig.class})
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class SummonerControllerTest {

	@Autowired private ObjectMapper objectMapper;
	@Autowired private WebApplicationContext context;
	@Autowired private Map<String, ResponseEntity<ErrorResponseBody>> errorResponseEntities;
	@MockBean private SummonerService summonerService;
	private MockMvc mockMvc;
	
	@BeforeEach
	public void beforeEach() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
				.addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
				.build();
	}
	
	@DisplayName("정상적인 요청시 200 상태 코드를 리턴한다.")
	@MethodSource(value = "com.lolsearcher.search.summoner.SummonerSetup#correctSummonerName")
	@ParameterizedTest
	public void getSummonerWithCorrectName(String name) throws Exception {
		//given
		SummonerDto summoner = SummonerDto.builder()
						.summonerId("summonerId1")
						.name(name.replaceAll(SUMMONER_NAME_REGEX, ""))
						.build();

		given(summonerService.findByName(any())).willReturn(summoner);

		//when && then
		mockMvc.perform(get(FIND_BY_NAME_URI, name)
						.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().is(HttpStatus.OK.value()))
				.andExpect(response -> {
					String responseBody = response.getResponse().getContentAsString();
					SummonerDto responseSummoner = objectMapper.readValue(responseBody, SummonerDto.class);

					assertThat(responseSummoner.getName()).isEqualTo(summoner.getName());
					assertThat(responseSummoner.getSummonerId()).isEqualTo(summoner.getSummonerId());
				});
	}

	@DisplayName("특수 문자가 포함된 닉네임이 컨트롤러로 전달되면 컨트롤러는 특수 문자가 제거된 닉네임의 JSON 데이터를 반환한다.")
	@MethodSource(value = "com.lolsearcher.search.summoner.SummonerSetup#specialCharacterSummonerName")
	@ParameterizedTest
	public void getSummonerWithSpecialCharacterName(String name) throws Exception {
		//given
		SummonerDto summoner = SummonerDto.builder()
				.summonerId("summonerId1")
				.name(name.replaceAll(SUMMONER_NAME_REGEX, ""))
				.build();

		given(summonerService.findByName(any())).willReturn(summoner);

		//when && then
		mockMvc.perform(get(FIND_BY_NAME_URI, name)
						.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().is(HttpStatus.OK.value()))
				.andExpect(response -> {
					String responseBody = response.getResponse().getContentAsString();
					SummonerDto responseSummoner = objectMapper.readValue(responseBody, SummonerDto.class);

					assertThat(responseSummoner.getName()).isNotEqualTo(name);            //초기 파라미터 값 비교
					assertThat(responseSummoner.getName()).isEqualTo(summoner.getName()); //필터링된 파라미터로 값 비교
					assertThat(responseSummoner.getSummonerId()).isEqualTo(summoner.getSummonerId());
				});
	}

	@DisplayName("유효하지 않는 파라미터 요청시 400 상태코드를 리턴한다.")
	@MethodSource(value = "com.lolsearcher.search.summoner.SummonerSetup#incorrectSummonerName")
	@ParameterizedTest
	public void getSummonerWithIncorrectRequest(String name) throws Exception {
		//given

		//when && then
		mockMvc.perform(get(FIND_BY_NAME_URI, name)
						.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
				.andExpect(result -> {
					ErrorResponseBody body = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponseBody.class);
					ErrorResponseBody expected = errorResponseEntities.get(BAD_REQUEST_ENTITY_NAME).getBody();

					assertThat(expected).isNotNull();
					assertThat(body.getErrorStatusCode()).isEqualTo(expected.getErrorStatusCode());
					assertThat(body.getErrorMessage()).isEqualTo(expected.getErrorMessage());
				});
	}

	@DisplayName("외부 서버 응답 시간 초과가 날 경우 504 응답코드를 리턴한다.")
	@MethodSource("com.lolsearcher.search.summoner.SummonerSetup#timeoutError")
	@ParameterizedTest
	public void getSummonerWithTimeoutError(Exception exception) throws Exception {
		//given
		String name = "닉네임";
		given(summonerService.findByName(name)).willThrow(exception);

		//when && then
		mockMvc.perform(get(FIND_BY_NAME_URI, name)
						.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().is(HttpStatus.GATEWAY_TIMEOUT.value()))
				.andExpect(result -> {
					ErrorResponseBody body = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponseBody.class);
					ErrorResponseBody expected = errorResponseEntities.get(TIME_OUT_ENTITY_NAME).getBody();

					assertThat(expected).isNotNull();
					assertThat(body.getErrorStatusCode()).isEqualTo(expected.getErrorStatusCode());
					assertThat(body.getErrorMessage()).isEqualTo(expected.getErrorMessage());
				});
	}

	@DisplayName("외부 서버로부터 에러가 발생할 경우 502 상태코드를 리턴한다.")
	@MethodSource("com.lolsearcher.search.summoner.SummonerSetup#externalServerError")
	@ParameterizedTest
	public void getSummonerWithExternalServerError(Exception exception) throws Exception {
		//given
		String name = "닉네임";
		given(summonerService.findByName(name)).willThrow(exception);

		//when && then
		mockMvc.perform(get(FIND_BY_NAME_URI, name)
						.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().is(HttpStatus.BAD_GATEWAY.value()))
				.andExpect(result -> {
					ErrorResponseBody body = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponseBody.class);
					ErrorResponseBody expected = errorResponseEntities.get(BAD_GATEWAY_ENTITY_NAME).getBody();

					assertThat(expected).isNotNull();
					assertThat(body.getErrorStatusCode()).isEqualTo(expected.getErrorStatusCode());
					assertThat(body.getErrorMessage()).isEqualTo(expected.getErrorMessage());
				});
	}

	@DisplayName("존재하지 않는 소환사 닉네임일 경우 404 상태코드를 리턴한다.")
	@Test
	public void getSummonerWithNotExistedSummonerInGameServerException() throws Exception {
		//given
		String name = "닉네임";

		given(summonerService.findByName(name))
				.willThrow(new NotExistedSummonerInGameServerException(name));

		//when && then
		mockMvc.perform(get(FIND_BY_NAME_URI, name)
						.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().is(HttpStatus.NOT_FOUND.value()))
				.andExpect(result -> {
					ErrorResponseBody body = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponseBody.class);
					ErrorResponseBody expected = errorResponseEntities.get(NOT_FOUND_ENTITY_NAME).getBody();

					assertThat(expected).isNotNull();
					assertThat(body.getErrorStatusCode()).isEqualTo(expected.getErrorStatusCode());
					assertThat(body.getErrorMessage()).isEqualTo(expected.getErrorMessage());
				});
	}

	@DisplayName("DB에 데이터가 없을 경우 갱신서버로 리다이렉트 요청한다.")
	@Test
	public void getSummonerWithNotExistedSummonerInDBException() throws Exception {
		//given
		String name = "닉네임";

		given(summonerService.findByName(name))
				.willThrow(new NotExistedSummonerInDBException(name));

		//when && then
		mockMvc.perform(get(FIND_BY_NAME_URI, name)
						.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().is(HttpStatus.TEMPORARY_REDIRECT.value()))
				.andExpect(result -> {
					ErrorResponseBody body = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponseBody.class);
					ErrorResponseBody expected = errorResponseEntities.get(TEMPORARY_REDIRECT_ENTITY_NAME).getBody();

					assertThat(expected).isNotNull();
					assertThat(body.getErrorStatusCode()).isEqualTo(expected.getErrorStatusCode());
					assertThat(body.getErrorMessage()).isEqualTo(expected.getErrorMessage());
				});
	}
}
