package com.lolsearcher.integration.controller.search.summoner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.constant.BeanNameConstants;
import com.lolsearcher.constant.LolSearcherConstants;
import com.lolsearcher.exception.exception.summoner.NotExistedSummonerInDBException;
import com.lolsearcher.exception.exception.summoner.NotExistedSummonerInGameServerException;
import com.lolsearcher.model.request.search.summoner.RequestSummonerDto;
import com.lolsearcher.model.response.error.ErrorResponseBody;
import com.lolsearcher.model.response.front.search.summoner.SummonerDto;
import com.lolsearcher.service.search.summoner.SummonerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import javax.persistence.QueryTimeoutException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
	
	@DisplayName("유효한 파라미터가 컨트롤러로 전달되면 컨트롤러는 적절한 객체를 JSON 데이터로 반환한다.")
	@Test
	public void getSummonerWithCorrectRequest() throws Exception {
		//given
		RequestSummonerDto request = new RequestSummonerDto("닉#네임");
		String requestBody = objectMapper.writeValueAsString(request);

		SummonerDto summoner = SummonerDto.builder()
						.summonerId("summonerId1")
						.name(request.getSummonerName().replaceAll(LolSearcherConstants.REGEX, ""))
						.build();
		given(summonerService.getSummonerDto(any())).willReturn(summoner);

		//when && then
		mockMvc.perform(post("/summoner")
						.contentType(MediaType.APPLICATION_JSON_VALUE)
						.content(requestBody))
				.andExpect(status().is(HttpStatus.OK.value()))
				.andExpect(response -> {
					String responseBody = response.getResponse().getContentAsString();
					SummonerDto responseSummoner = objectMapper.readValue(responseBody, SummonerDto.class);

					assertThat(responseSummoner.getName()).isEqualTo(summoner.getName());
					assertThat(responseSummoner.getSummonerId()).isEqualTo(summoner.getSummonerId());
				});
	}

	@DisplayName("유효하지 않는 파라미터가 컨트롤러로 전달되면 프론트로 404에러를 전달한다.")
	@NullAndEmptySource
	@ParameterizedTest
	@ValueSource(strings = {"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"})
	public void getSummonerWithInvalidRequest(String name) throws Exception {
		//given
		RequestSummonerDto params = new RequestSummonerDto(name);
		String paramBody = objectMapper.writeValueAsString(params);

		//when && then
		mockMvc.perform(post("/summoner")
						.contentType(MediaType.APPLICATION_JSON_VALUE)
						.content(paramBody))
				.andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
				.andExpect(result -> {
					ErrorResponseBody errorResponse = objectMapper.readValue(result.getResponse().getContentAsString(),
							ErrorResponseBody.class);

					ErrorResponseBody badRequestEntity = errorResponseEntities.get(BeanNameConstants.BAD_REQUEST_ENTITY_NAME).getBody();

					assertThat(errorResponse.getErrorStatusCode()).isEqualTo(badRequestEntity.getErrorStatusCode());
					assertThat(errorResponse.getErrorMessage()).isEqualTo(badRequestEntity.getErrorMessage());
				});
	}

	@DisplayName("DB 관련 에러가 발생하면 프론트로 502에러를 전달한다.")
	@Test
	public void getSummonerWithJPAException() throws Exception {
		//given
		RequestSummonerDto params = new RequestSummonerDto("닉네임");
		String paramBody = objectMapper.writeValueAsString(params);

		given(summonerService.getSummonerDto(params)).willThrow(new QueryTimeoutException());

		//when && then
		mockMvc.perform(post("/summoner")
						.contentType(MediaType.APPLICATION_JSON_VALUE)
						.content(paramBody))
				.andExpect(status().is(HttpStatus.BAD_GATEWAY.value()))
				.andExpect(result -> {
					ErrorResponseBody errorResponse = objectMapper.readValue(result.getResponse().getContentAsString(),
							ErrorResponseBody.class);

					ErrorResponseBody bandRequestEntity = errorResponseEntities
							.get(BeanNameConstants.BAD_GATEWAY_ENTITY_NAME).getBody();

					assertThat(errorResponse.getErrorStatusCode()).isEqualTo(bandRequestEntity.getErrorStatusCode());
					assertThat(errorResponse.getErrorMessage()).isEqualTo(bandRequestEntity.getErrorMessage());
				});
	}

	@DisplayName("WebClientResponseException 에러가 발생하면 프론트로 502에러를 전달한다.")
	@Test
	public void getSummonerWithWebClientResponseException() throws Exception {
		//given
		RequestSummonerDto params = new RequestSummonerDto("닉네임");
		String paramBody = objectMapper.writeValueAsString(params);

		WebClientResponseException webClientResponseException =
				new WebClientResponseException(HttpStatus.BAD_GATEWAY.value(), HttpStatus.BAD_GATEWAY.getReasonPhrase(), null, null, null);

		given(summonerService.getSummonerDto(params)).willThrow(webClientResponseException);

		//when && then
		mockMvc.perform(post("/summoner")
						.contentType(MediaType.APPLICATION_JSON_VALUE)
						.content(paramBody))
				.andExpect(status().is(HttpStatus.BAD_GATEWAY.value()))
				.andExpect(result -> {
					ErrorResponseBody errorResponse = objectMapper.readValue(result.getResponse().getContentAsString(),
							ErrorResponseBody.class);

					ErrorResponseBody bandRequestEntity = errorResponseEntities
							.get(BeanNameConstants.BAD_GATEWAY_ENTITY_NAME).getBody();

					assertThat(errorResponse.getErrorStatusCode()).isEqualTo(bandRequestEntity.getErrorStatusCode());
					assertThat(errorResponse.getErrorMessage()).isEqualTo(bandRequestEntity.getErrorMessage());
				});
	}

	@DisplayName("NotExistedSummonerInGameServerException이 발생하면 프론트로 404에러를 전달한다.")
	@Test
	public void getSummonerWithNotExistedSummonerInGameServerException() throws Exception {
		//given
		RequestSummonerDto params = new RequestSummonerDto("닉네임");
		String paramBody = objectMapper.writeValueAsString(params);

		given(summonerService.getSummonerDto(params))
				.willThrow(new NotExistedSummonerInGameServerException(params.getSummonerName()));

		//when && then
		mockMvc.perform(post("/summoner")
						.contentType(MediaType.APPLICATION_JSON_VALUE)
						.content(paramBody))
				.andExpect(status().is(HttpStatus.NOT_FOUND.value()))
				.andExpect(result -> {
					ErrorResponseBody errorResponse = objectMapper.readValue(result.getResponse().getContentAsString(),
							ErrorResponseBody.class);

					ErrorResponseBody bandRequestEntity = errorResponseEntities
							.get(BeanNameConstants.NOT_FOUND_ENTITY_NAME).getBody();

					assertThat(errorResponse.getErrorStatusCode()).isEqualTo(bandRequestEntity.getErrorStatusCode());
					assertThat(errorResponse.getErrorMessage()).isEqualTo(bandRequestEntity.getErrorMessage());
				});
	}

	@DisplayName("NotExistedSummonerInDBException이 발생하면 프론트로 갱신서버로 리다이렉트 요청한다.")
	@Test
	public void getSummonerWithNotExistedSummonerInDBException() throws Exception {
		//given
		RequestSummonerDto params = new RequestSummonerDto("닉네임");
		String paramBody = objectMapper.writeValueAsString(params);

		given(summonerService.getSummonerDto(params))
				.willThrow(new NotExistedSummonerInDBException(params.getSummonerName()));

		//when && then
		mockMvc.perform(post("/summoner")
						.contentType(MediaType.APPLICATION_JSON_VALUE)
						.content(paramBody))
				.andExpect(status().is(HttpStatus.TEMPORARY_REDIRECT.value()))
				.andExpect(result -> {
					ErrorResponseBody errorResponse = objectMapper.readValue(result.getResponse().getContentAsString(),
							ErrorResponseBody.class);

					ErrorResponseBody temporaryRedirectResponseBody = errorResponseEntities
							.get(BeanNameConstants.TEMPORARY_REDIRECT_ENTITY_NAME).getBody();

					assertThat(errorResponse.getErrorStatusCode()).isEqualTo(temporaryRedirectResponseBody.getErrorStatusCode());
					assertThat(errorResponse.getErrorMessage()).isEqualTo(temporaryRedirectResponseBody.getErrorMessage());
				});
	}
}
