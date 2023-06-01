package com.lolsearcher.search.rank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.errors.ErrorResponseBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.Map;

import static com.lolsearcher.errors.ErrorConstant.*;
import static com.lolsearcher.search.rank.RankConstant.*;
import static com.lolsearcher.search.rank.RankTypeState.RANKED_SOLO_5x5;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class RankControllerTest {

    private static final String SEASON_ID_PARAM_NAME = "seasonId";

    @Autowired private ObjectMapper objectMapper;
    @Autowired private WebApplicationContext context;
    @Autowired private Map<String, ResponseEntity<ErrorResponseBody>> errorResponseEntities;
    @MockBean private RankService rankService;
    private MockMvc mockMvc;

    @BeforeEach
    public void beforeEach() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
                .build();
    }

    @DisplayName("Find All : 정상적인 요청시 200 상태 코드를 리턴한다.")
    @MethodSource(value = "com.lolsearcher.search.rank.RankSetup#correctParamWithFindAll")
    @ParameterizedTest
    public void testFindAllWithValidParam(String summonerId, Integer seasonId) throws Exception {

        //given
        given(rankService.findAllById(any())).willReturn(Map.of());

        //when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .get(FIND_RANKS_URI, summonerId)
                        .param(SEASON_ID_PARAM_NAME, seasonId == null ? null : Integer.toString(seasonId))
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(response -> verify(rankService, times(1)).findAllById(any()));
    }

    @DisplayName("Find All : 정상적인 요청시 200 상태 코드를 리턴한다.")
    @ValueSource(strings = {"summonerId", "1234"})
    @ParameterizedTest
    public void testFindAllWithValidParam(String summonerId) throws Exception {

        //given
        given(rankService.findAllById(any())).willReturn(Map.of());

        //when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .get(FIND_RANKS_URI, summonerId)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(response -> verify(rankService, times(1)).findAllById(any()));
    }

    @DisplayName("Find All : 유효하지않은 파라미터로 요청시 400에러를 리턴한다.")
    @MethodSource(value = "com.lolsearcher.search.rank.RankSetup#invalidRanksParam")
    @ParameterizedTest
    public void testFindAllWithInvalidParam(String summonerId, Integer seasonId) throws Exception {

        //when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .get(FIND_RANKS_URI, summonerId)
                        .param(SEASON_ID_PARAM_NAME, Integer.toString(seasonId))
                )
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    ErrorResponseBody body = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponseBody.class);
                    ErrorResponseBody expected = errorResponseEntities.get(BAD_REQUEST_ENTITY_NAME).getBody();

                    assertThat(expected).isNotNull();
                    assertThat(body.getErrorStatusCode()).isEqualTo(expected.getErrorStatusCode());
                    assertThat(body.getErrorMessage()).isEqualTo(expected.getErrorMessage());
                });
    }

    @DisplayName("Find All : DB의 rank 데이터가 비정상일경우 500 상태코드를 리턴한다.")
    @MethodSource("com.lolsearcher.search.rank.RankSetup#incorrectPersistenceDataWithFindAll")
    @ParameterizedTest
    public void getRankDtoWithIncorrectSummonerRankSizeException(Exception exception) throws Exception {

        //given
        String summonerId = "summonerId";

        given(rankService.findAllById(any())).willThrow(exception);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .get(FIND_RANKS_URI, summonerId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param(SEASON_ID_PARAM_NAME, Integer.toString(CURRENT_SEASON_ID))
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(response -> {
                    ErrorResponseBody body = objectMapper.readValue(response.getResponse().getContentAsString(), ErrorResponseBody.class);
                    ErrorResponseBody expected = errorResponseEntities.get(INTERNAL_SERVER_ERROR_ENTITY_NAME).getBody();

                    assertThat(expected).isNotNull();
                    assertThat(body.getErrorStatusCode()).isEqualTo(expected.getErrorStatusCode());
                    assertThat(body.getErrorMessage()).isEqualTo(expected.getErrorMessage());
                });
    }

    @DisplayName("Find All : 외부 서버 time out이 발생할 경우 504 상태 코드를 리턴한다.")
    @MethodSource("com.lolsearcher.search.rank.RankSetup#timeoutErrorWithFindAll")
    @ParameterizedTest
    public void testFindAllWithTimeoutException(Exception exception) throws Exception {

        //given
        String summonerId = "summonerId";

        given(rankService.findAllById(any())).willThrow(exception);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .get(FIND_RANKS_URI, summonerId)
                        .param(SEASON_ID_PARAM_NAME, Integer.toString(CURRENT_SEASON_ID))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.GATEWAY_TIMEOUT.value()))
                .andExpect(response -> {
                    ErrorResponseBody body = objectMapper.readValue(response.getResponse().getContentAsString(), ErrorResponseBody.class);
                    ErrorResponseBody expected = errorResponseEntities.get(TIME_OUT_ENTITY_NAME).getBody();

                    assertThat(expected).isNotNull();
                    assertThat(body.getErrorStatusCode()).isEqualTo(expected.getErrorStatusCode());
                    assertThat(body.getErrorMessage()).isEqualTo(expected.getErrorMessage());
                });
    }


    // ----------------------------- find by id test -------------------------------------

    @DisplayName("FindById : 정상적인 요청시 200 상태 코드를 리턴한다.")
    @MethodSource(value = "com.lolsearcher.search.rank.RankSetup#correctParamWithFindAll")
    @ParameterizedTest
    public void testFindByIdWithValidParam(String summonerId, Integer seasonId, String rankId) throws Exception {

        //given
        given(rankService.findOneById(any())).willReturn(Map.of());

        //when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .get(FIND_RANK_BY_ID_URI, summonerId, rankId)
                        .param(SEASON_ID_PARAM_NAME, Integer.toString(seasonId))
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(response -> verify(rankService, times(1)).findOneById(any()));
    }

    @DisplayName("FindById : 정상적인 요청시 200 상태 코드를 리턴한다.")
    @MethodSource(value = "com.lolsearcher.search.rank.RankSetup#validRankByIdParam")
    @ParameterizedTest
    public void testFindByIdlWithValidParam(String summonerId, Integer seasonId, String rankType) throws Exception {

        //given
        given(rankService.findOneById(any())).willReturn(null);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .get(FIND_RANK_BY_ID_URI, summonerId, rankType)
                        .param(SEASON_ID_PARAM_NAME, Integer.toString(seasonId))
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(response -> verify(rankService, times(1)).findOneById(any()));
    }

    @DisplayName("FindById : 유효하지않은 파라미터로 요청시 400에러를 리턴한다.")
    @MethodSource(value = "com.lolsearcher.search.rank.RankSetup#invalidRankByIdParam")
    @ParameterizedTest
    public void testFindByIdWithInvalidParam(String summonerId, Integer seasonId,  String rankType) throws Exception {

        //when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .get(FIND_RANK_BY_ID_URI, summonerId, rankType)
                        .param(SEASON_ID_PARAM_NAME, Integer.toString(seasonId))
                )
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    ErrorResponseBody body = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponseBody.class);
                    ErrorResponseBody expected = errorResponseEntities.get(BAD_REQUEST_ENTITY_NAME).getBody();

                    assertThat(expected).isNotNull();
                    assertThat(body.getErrorStatusCode()).isEqualTo(expected.getErrorStatusCode());
                    assertThat(body.getErrorMessage()).isEqualTo(expected.getErrorMessage());
                });
    }

    @DisplayName("FindById : DB의 rank 데이터가 비정상일경우 500 상태코드를 리턴한다.")
    @MethodSource("com.lolsearcher.search.rank.RankSetup#incorrectPersistenceDataWithFindAll")
    @ParameterizedTest
    public void testFindByIdWithIncorrectSummonerRankSizeException(Exception exception) throws Exception {

        //given
        String summonerId = "summonerId";

        given(rankService.findOneById(any())).willThrow(exception);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .get(FIND_RANK_BY_ID_URI, summonerId, RANKED_SOLO_5x5.name())
                        .param(SEASON_ID_PARAM_NAME, Integer.toString(CURRENT_SEASON_ID))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(result -> {
                    ErrorResponseBody body = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponseBody.class);
                    ErrorResponseBody expected = errorResponseEntities.get(INTERNAL_SERVER_ERROR_ENTITY_NAME).getBody();

                    assertThat(expected).isNotNull();
                    assertThat(body.getErrorStatusCode()).isEqualTo(expected.getErrorStatusCode());
                    assertThat(body.getErrorMessage()).isEqualTo(expected.getErrorMessage());
                });
    }

    @DisplayName("FindById : 외부 서버 time out이 발생할 경우 504 상태 코드를 리턴한다.")
    @MethodSource("com.lolsearcher.search.rank.RankSetup#timeoutErrorWithFindAll")
    @ParameterizedTest
    public void testFindByIdWithTimeOutException(Exception exception) throws Exception {

        //given
        String summonerId = "summonerId";

        given(rankService.findOneById(any())).willThrow(exception);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .get(FIND_RANK_BY_ID_URI, summonerId, RANKED_SOLO_5x5.name())
                        .param(SEASON_ID_PARAM_NAME, Integer.toString(CURRENT_SEASON_ID))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.GATEWAY_TIMEOUT.value()))
                .andExpect(result -> {
                    ErrorResponseBody body = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponseBody.class);
                    ErrorResponseBody expected = errorResponseEntities.get(TIME_OUT_ENTITY_NAME).getBody();

                    assertThat(expected).isNotNull();
                    assertThat(body.getErrorStatusCode()).isEqualTo(expected.getErrorStatusCode());
                    assertThat(body.getErrorMessage()).isEqualTo(expected.getErrorMessage());
                });
    }

}
