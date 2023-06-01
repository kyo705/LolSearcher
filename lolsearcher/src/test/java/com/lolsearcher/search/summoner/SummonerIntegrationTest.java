package com.lolsearcher.search.summoner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.errors.ErrorConstant;
import com.lolsearcher.errors.ErrorResponseBody;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.io.IOException;
import java.util.Map;

import static com.lolsearcher.errors.ErrorConstant.*;
import static com.lolsearcher.search.summoner.SummonerConstant.FIND_BY_NAME_URI;
import static com.lolsearcher.search.summoner.SummonerConstant.SUMMONER_NAME_REGEX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class SummonerIntegrationTest {

    @Value("${lolsearcher.mock-server.port}")
    private static int reactiveServerPort = 15554;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired private WebApplicationContext context;
    @Autowired private Map<String, ResponseEntity<ErrorResponseBody>> errorResponseEntities;
    private MockMvc mockMvc;
    private static MockWebServer mockWebServer;


    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start(reactiveServerPort);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }


    @BeforeEach
    public void beforeEach() throws IOException {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
                .build();
    }

    @DisplayName("정상적인 요청시 200 상태 코드를 리턴한다.")
    @ValueSource(strings = {"name1", "name2", "name#@1", "#$name2$#"})
    @ParameterizedTest
    public void getSummonerWithCorrectName(String name) throws Exception {

        //when && then
        mockMvc.perform(get(FIND_BY_NAME_URI, name)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(response -> {
                    SummonerDto body = objectMapper.readValue(response.getResponse().getContentAsString(), SummonerDto.class);

                    assertThat(body.getName()).isEqualTo(name.replaceAll(SUMMONER_NAME_REGEX, ""));
                });
    }

    @DisplayName("유효하지 않는 파라미터 요청시 400 상태코드를 리턴한다.")
    @ValueSource(strings = {"  ", "123456789012345678901234567890123456789012345678901" /* len : 50 */})
    @ParameterizedTest
    public void getSummonerWithIncorrectRequest(String name) throws Exception {

        //when && then
        mockMvc.perform(get(FIND_BY_NAME_URI, name)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    ErrorResponseBody body = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponseBody.class);
                    ErrorResponseBody expected = errorResponseEntities.get(ErrorConstant.BAD_REQUEST_ENTITY_NAME).getBody();

                    assertThat(expected).isNotNull();
                    assertThat(body.getErrorStatusCode()).isEqualTo(expected.getErrorStatusCode());
                    assertThat(body.getErrorMessage()).isEqualTo(expected.getErrorMessage());
                });
    }

    @DisplayName("DB 데이터가 잘못된 경우 500 상태코드를 리턴한다.")
    @ValueSource(strings = {"name13","name14"})
    @ParameterizedTest
    public void getSummonerWithInvalidDataInDB(String name) throws Exception {

        //when && then
        mockMvc.perform(get(FIND_BY_NAME_URI, name)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(result -> {
                    ErrorResponseBody body = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponseBody.class);
                    ErrorResponseBody expected = errorResponseEntities.get(INTERNAL_SERVER_ERROR_ENTITY_NAME).getBody();

                    assertThat(expected).isNotNull();
                    assertThat(body.getErrorStatusCode()).isEqualTo(expected.getErrorStatusCode());
                    assertThat(body.getErrorMessage()).isEqualTo(expected.getErrorMessage());
                });
    }

    @DisplayName("외부 서버로부터 에러가 발생할 경우 502 상태코드를 리턴한다.")
    @Test
    public void getSummonerWithExternalServerError() throws Exception {

        //given
        String name = "중복된 닉네임";

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .addHeader("Content-Type", "application/json")
        );

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
        String name = "중복된 닉네임";

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(HttpStatus.OK.value())
                        .addHeader("Content-Type", "application/json")
        );

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
        String name = "NOT_EXISTING_NAME";

        //when && then
        mockMvc.perform(get(FIND_BY_NAME_URI, name)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is(HttpStatus.TEMPORARY_REDIRECT.value()))
                .andExpect(header().string(HttpHeaders.LOCATION, SUMMONER_RENEW_REQUEST_URI))
                .andExpect(result -> {
                    ErrorResponseBody body = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponseBody.class);
                    ErrorResponseBody expected = errorResponseEntities.get(TEMPORARY_REDIRECT_ENTITY_NAME).getBody();

                    assertThat(expected).isNotNull();
                    assertThat(body.getErrorStatusCode()).isEqualTo(expected.getErrorStatusCode());
                    assertThat(body.getErrorMessage()).isEqualTo(expected.getErrorMessage());
                });
    }
}
