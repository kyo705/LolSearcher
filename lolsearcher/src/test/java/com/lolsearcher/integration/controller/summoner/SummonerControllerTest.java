package com.lolsearcher.integration.controller.summoner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.constant.BeanNameConstants;
import com.lolsearcher.constant.LolSearcherConstants;
import com.lolsearcher.model.request.front.RequestSummonerDto;
import com.lolsearcher.model.response.common.ErrorResponseBody;
import com.lolsearcher.model.response.front.summoner.SummonerDto;
import com.lolsearcher.service.summoner.SummonerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
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

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class SummonerControllerTest {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	private MockMvc mockMvc;
	@Autowired
	private WebApplicationContext context;
	@Autowired
	private Map<String, ResponseEntity<ErrorResponseBody>> errorResponseEntities;
	@MockBean
	private SummonerService summonerService;
	
	@BeforeEach
	public void beforeEach() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
				.addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
				.build();
	}
	
	@DisplayName("유효한 파라미터가 컨트롤러로 전달되면 컨트롤러는 적절한 객체를 JSON 데이터로 반환한다.")
	@ParameterizedTest
	@ValueSource(strings = {"푸켓푸켓", "페이커", "gdsgs"})
	public void getSummonerWithCorrectRequest(String name) throws Exception {
		//given
		RequestSummonerDto params = new RequestSummonerDto(name, false);
		String paramBody = objectMapper.writeValueAsString(params);

		SummonerDto summoner = SummonerDto.builder()
						.summonerId("summonerId1")
						.name(name.replaceAll(LolSearcherConstants.REGEX, ""))
						.build();
		given(summonerService.getSummonerDto(params)).willReturn(summoner);

		//when && then
		mockMvc.perform(post("/summoner")
						.contentType(MediaType.APPLICATION_JSON_VALUE)
						.content(paramBody))
		.andExpect(status().is(HttpStatus.OK.value()))
		.andExpect(result -> {
			SummonerDto responseSummoner = objectMapper.readValue(result.getResponse().getContentAsString(),
					SummonerDto.class);

			assertThat(responseSummoner.getName()).isEqualTo(summoner.getName());
			assertThat(responseSummoner.getSummonerId()).isEqualTo(summoner.getSummonerId());
			assertThat(responseSummoner.isRenewed()).isEqualTo(summoner.isRenewed());
		});
	}

	@DisplayName("유효하지 않는 파라미터가 컨트롤러로 전달되면 프론트로 404에러를 전달한다.")
	@ParameterizedTest
	@ValueSource(strings = {"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"})
	public void getSummonerWithInvalidRequest(String name) throws Exception {
		//given
		RequestSummonerDto params = new RequestSummonerDto(name, false);
		String paramBody = objectMapper.writeValueAsString(params);

		ErrorResponseBody bandRequestEntity = errorResponseEntities
				.get(BeanNameConstants.BAD_REQUEST_ENTITY_NAME).getBody();

		//when && then
		mockMvc.perform(post("/summoner")
						.contentType(MediaType.APPLICATION_JSON_VALUE)
						.content(paramBody))
				.andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
				.andExpect(result -> {
					ErrorResponseBody errorResponse = objectMapper.readValue(result.getResponse().getContentAsString(),
							ErrorResponseBody.class);

					assertThat(errorResponse.getErrorStatusCode()).isEqualTo(bandRequestEntity.getErrorStatusCode());
					assertThat(errorResponse.getErrorMessage()).isEqualTo(bandRequestEntity.getErrorMessage());
				});
	}

	@DisplayName("WebClientResponseException 404에러가 발생하면 프론트로 404에러를 전달한다.")
	@ParameterizedTest
	@ValueSource(strings = {"푸켓푸켓"})
	public void getSummonerWith404Error(String name) throws Exception {
		//given
		RequestSummonerDto params = new RequestSummonerDto(name, false);
		String paramBody = objectMapper.writeValueAsString(params);

		ErrorResponseBody bandRequestEntity = errorResponseEntities
				.get(BeanNameConstants.NOT_FOUND_ENTITY_NAME).getBody();

		given(summonerService.getSummonerDto(params))
				.willThrow(new WebClientResponseException(
						HttpStatus.NOT_FOUND.value(),
						HttpStatus.NOT_FOUND.getReasonPhrase(),
						null, null, null));

		//when && then
		mockMvc.perform(post("/summoner")
						.contentType(MediaType.APPLICATION_JSON_VALUE)
						.content(paramBody))
				.andExpect(status().is(HttpStatus.NOT_FOUND.value()))
				.andExpect(result -> {
					ErrorResponseBody errorResponse = objectMapper.readValue(result.getResponse().getContentAsString(),
							ErrorResponseBody.class);

					assertThat(errorResponse.getErrorStatusCode()).isEqualTo(bandRequestEntity.getErrorStatusCode());
					assertThat(errorResponse.getErrorMessage()).isEqualTo(bandRequestEntity.getErrorMessage());
				});
	}

	@DisplayName("WebClientResponseException 400에러가 발생하면 프론트로 404에러를 전달한다.")
	@ParameterizedTest
	@ValueSource(strings = {"푸켓푸켓"})
	public void getSummonerWith400Error(String name) throws Exception {
		//given
		RequestSummonerDto params = new RequestSummonerDto(name, false);
		String paramBody = objectMapper.writeValueAsString(params);

		ErrorResponseBody bandRequestEntity = errorResponseEntities
				.get(BeanNameConstants.NOT_FOUND_ENTITY_NAME).getBody();

		given(summonerService.getSummonerDto(params))
				.willThrow(new WebClientResponseException(
						HttpStatus.BAD_REQUEST.value(),
						HttpStatus.BAD_REQUEST.getReasonPhrase(),
						null, null, null));

		//when && then
		mockMvc.perform(post("/summoner")
						.contentType(MediaType.APPLICATION_JSON_VALUE)
						.content(paramBody))
				.andExpect(status().is(HttpStatus.NOT_FOUND.value()))
				.andExpect(result -> {
					ErrorResponseBody errorResponse = objectMapper.readValue(result.getResponse().getContentAsString(),
							ErrorResponseBody.class);

					assertThat(errorResponse.getErrorStatusCode()).isEqualTo(bandRequestEntity.getErrorStatusCode());
					assertThat(errorResponse.getErrorMessage()).isEqualTo(bandRequestEntity.getErrorMessage());
				});
	}

	@DisplayName("WebClientResponseException 429에러가 발생하면 프론트로 429에러를 전달한다.")
	@ParameterizedTest
	@ValueSource(strings = {"푸켓푸켓"})
	public void getSummonerWith429Error(String name) throws Exception {
		//given
		RequestSummonerDto params = new RequestSummonerDto(name, false);
		String paramBody = objectMapper.writeValueAsString(params);

		ErrorResponseBody bandRequestEntity = errorResponseEntities
				.get(BeanNameConstants.TOO_MANY_REQUESTS_ENTITY_NAME).getBody();

		given(summonerService.getSummonerDto(params))
				.willThrow(new WebClientResponseException(
						HttpStatus.TOO_MANY_REQUESTS.value(),
						HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase(),
						null, null, null));

		//when && then
		mockMvc.perform(post("/summoner")
						.contentType(MediaType.APPLICATION_JSON_VALUE)
						.content(paramBody))
				.andExpect(status().is(HttpStatus.TOO_MANY_REQUESTS.value()))
				.andExpect(result -> {
					ErrorResponseBody errorResponse = objectMapper.readValue(result.getResponse().getContentAsString(),
							ErrorResponseBody.class);

					assertThat(errorResponse.getErrorStatusCode()).isEqualTo(bandRequestEntity.getErrorStatusCode());
					assertThat(errorResponse.getErrorMessage()).isEqualTo(bandRequestEntity.getErrorMessage());
				});
	}
}
