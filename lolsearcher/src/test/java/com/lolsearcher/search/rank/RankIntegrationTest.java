package com.lolsearcher.search.rank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.errors.ErrorResponseBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
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

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
public class RankIntegrationTest {

    protected static final String SEASON_ID_PARAM_KEY = "seasonId";

    @Autowired private WebApplicationContext context;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private Map<String, ResponseEntity<ErrorResponseBody>> errorResponseEntities;
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();

    }

    @DisplayName("RankAll : 유효한 파라미터로 요청시 200 상태 코드를 리턴한다.")
    @MethodSource("com.lolsearcher.search.rank.RankSetup#validRanksParam")
    @ParameterizedTest
    public void testFindRanksWithValidParam(String summonerId, int seasonId) throws Exception {

        //when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .get(FIND_RANKS_URI, summonerId)
                        .param(SEASON_ID_PARAM_KEY, Integer.toString(seasonId))
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(result -> {
                    Map body = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
                    body.values().forEach(obj -> {
                        RankDto rank = objectMapper.convertValue(obj, RankDto.class);

                        assertThat(rank.getSummonerId()).isEqualTo(summonerId);
                        assertThat(rank.getSeasonId()).isEqualTo(seasonId);
                    });
                });
    }

    @DisplayName("RankAll : 잘못된 파라미터로 요청시 400 상태 코드를 리턴한다.")
    @MethodSource("com.lolsearcher.search.rank.RankSetup#invalidRanksParam")
    @ParameterizedTest
    public void testFindRanksWithInvalidParam(String summonerId, int seasonId) throws Exception {

        //when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .get(FIND_RANKS_URI, summonerId)
                        .param(SEASON_ID_PARAM_KEY, Integer.toString(seasonId))
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    ErrorResponseBody body = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponseBody.class);
                    ErrorResponseBody expected = errorResponseEntities.get(BAD_REQUEST_ENTITY_NAME).getBody();

                    assertThat(expected).isNotNull();
                    assertThat(body.getErrorStatusCode()).isEqualTo(expected.getErrorStatusCode());
                    assertThat(body.getErrorMessage()).isEqualTo(expected.getErrorMessage());
                });
    }

    @DisplayName("RankAll : 존재하지 않는 소환사 id로 요청시 404 상태 코드를 리턴한다.")
    @Test
    public void testFindRanksWithNotExistingSummoner() throws Exception {

        //given
        String summonerId = "NOT_EXIST";

        //when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .get(FIND_RANKS_URI, summonerId)
                        .param(SEASON_ID_PARAM_KEY, Integer.toString(CURRENT_SEASON_ID))
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.NOT_FOUND.value()))
                .andExpect(result -> {
                    ErrorResponseBody body = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponseBody.class);
                    ErrorResponseBody expected = errorResponseEntities.get(NOT_FOUND_ENTITY_NAME).getBody();

                    assertThat(expected).isNotNull();
                    assertThat(body.getErrorStatusCode()).isEqualTo(expected.getErrorStatusCode());
                    assertThat(body.getErrorMessage()).isEqualTo(expected.getErrorMessage());
                });
    }

    @DisplayName("RankAll : DB에 잘못된 값이 들어있는 경우 500 상태 코드를 리턴한다.")
    @MethodSource("com.lolsearcher.search.rank.RankSetup#invalidRanksInDB")
    @ParameterizedTest
    public void testFindRanksWithInvalidDataInDB(String summonerId, int seasonId) throws Exception {

        //when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .get(FIND_RANKS_URI, summonerId)
                        .param(SEASON_ID_PARAM_KEY, Integer.toString(seasonId))
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


    // --------------------------------- find Rank By Id -------------------------------------


    @DisplayName("RankById : 유효한 파라미터로 요청시 200 상태 코드를 리턴한다.")
    @MethodSource("com.lolsearcher.search.rank.RankSetup#validRankByIdParam")
    @ParameterizedTest
    public void testFindRankWithValidParam(String summonerId, int seasonId, String rankId) throws Exception {

        //when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .get(FIND_RANK_BY_ID_URI, summonerId, rankId)
                        .param(SEASON_ID_PARAM_KEY, Integer.toString(seasonId))
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(result -> {
                    Map body = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);

                    assertThat(body.size()).isLessThanOrEqualTo(1);

                    body.values().forEach(obj -> {
                        RankDto rank = objectMapper.convertValue(obj, RankDto.class);

                        assertThat(rank.getSummonerId()).isEqualTo(summonerId);
                        assertThat(rank.getSeasonId()).isEqualTo(seasonId);
                    });
                });
    }

    @DisplayName("RankById : 잘못된 파라미터로 요청시 400 상태 코드를 리턴한다.")
    @MethodSource("com.lolsearcher.search.rank.RankSetup#invalidRankByIdParam")
    @ParameterizedTest
    public void testFindRanksWithInvalidParam(String summonerId, int seasonId, String rankId) throws Exception {

        //when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .get(FIND_RANK_BY_ID_URI, summonerId, rankId)
                        .param(SEASON_ID_PARAM_KEY, Integer.toString(seasonId))
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    ErrorResponseBody body = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponseBody.class);
                    ErrorResponseBody expected = errorResponseEntities.get(BAD_REQUEST_ENTITY_NAME).getBody();

                    assertThat(expected).isNotNull();
                    assertThat(body.getErrorStatusCode()).isEqualTo(expected.getErrorStatusCode());
                    assertThat(body.getErrorMessage()).isEqualTo(expected.getErrorMessage());
                });
    }

    @DisplayName("RankById : 존재하지 않는 소환사 id로 요청시 404 상태 코드를 리턴한다.")
    @Test
    public void testFindRankByIdWithNotExistingSummoner() throws Exception {

        //given
        String summonerId = "NOT_EXIST";
        String rankId = RANKED_SOLO_5x5.name();

        //when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .get(FIND_RANK_BY_ID_URI, summonerId, rankId)
                        .param(SEASON_ID_PARAM_KEY, Integer.toString(CURRENT_SEASON_ID))
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.NOT_FOUND.value()))
                .andExpect(result -> {
                    ErrorResponseBody body = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponseBody.class);
                    ErrorResponseBody expected = errorResponseEntities.get(NOT_FOUND_ENTITY_NAME).getBody();

                    assertThat(expected).isNotNull();
                    assertThat(body.getErrorStatusCode()).isEqualTo(expected.getErrorStatusCode());
                    assertThat(body.getErrorMessage()).isEqualTo(expected.getErrorMessage());
                });
    }

    @DisplayName("RankById : DB에 잘못된 값이 들어있는 경우 500 상태 코드를 리턴한다.")
    @MethodSource("com.lolsearcher.search.rank.RankSetup#invalidRanksInDB")
    @ParameterizedTest
    public void testFindRankByIdWithInvalidDataInDB(String summonerId, int seasonId) throws Exception {

        //given
        String rankId = RANKED_SOLO_5x5.name();

        //when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .get(FIND_RANK_BY_ID_URI, summonerId, rankId)
                        .param(SEASON_ID_PARAM_KEY, Integer.toString(seasonId))
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
}
