package com.lolsearcher.search.mostchamp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.config.EmbeddedRedisConfig;
import com.lolsearcher.config.ErrorResponseEntityConfig.ErrorResponseBody;
import com.lolsearcher.search.match.MatchConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
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

import java.util.List;
import java.util.Map;

import static com.lolsearcher.config.ErrorResponseEntityConfig.BAD_REQUEST_ENTITY_NAME;
import static com.lolsearcher.config.ErrorResponseEntityConfig.INTERNAL_SERVER_ERROR_ENTITY_NAME;
import static com.lolsearcher.search.mostchamp.MostChampConstant.MOST_CHAMPS_URI;
import static com.lolsearcher.search.mostchamp.MostChampConstant.MOST_CHAMP_DEFAULT_COUNT;
import static com.lolsearcher.search.rank.RankConstant.CURRENT_SEASON_ID;
import static org.assertj.core.api.Assertions.assertThat;

@Import({EmbeddedRedisConfig.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
public class MostChampIntegrationTest {

    protected static final String QUEUE_ID_PARAM_KEY = "queueId";
    protected static final String SEASON_ID_PARAM_KEY = "seasonId";
    protected static final String COUNT_PARAM_KEY = "count";
    protected static final int SOLO_RANK_QUEUE_ID = 1;
    protected static final int FLEX_RANK_QUEUE_ID = 100;
    protected static final int CUSTOM_GAME_QUEUE_ID = 101;

    @Autowired private WebApplicationContext context;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private Map<String, ResponseEntity<ErrorResponseBody>> errorResponseEntities;
    @Autowired private CacheManager cacheManager;
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();

        cacheManager.getCache(MatchConstant.QUEUE_ID_LIST).put(SOLO_RANK_QUEUE_ID, "SOLO_RANK");
        cacheManager.getCache(MatchConstant.QUEUE_ID_LIST).put(FLEX_RANK_QUEUE_ID, "FLEX_RANK");
        cacheManager.getCache(MatchConstant.QUEUE_ID_LIST).put(CUSTOM_GAME_QUEUE_ID, "CUSTOM_GAME");
    }

    @DisplayName("유효한 파라미터로 요청시 200 응답을 리턴한다.")
    @ValueSource(strings = {
            "summonerId1",
            "1",
            "12345678901234567890123456789012345678901234567890123456789012"  /* length : 62 */,
            "123456789012345678901234567890123456789012345678901234567890123" /* length : 63 */
    })
    @ParameterizedTest
    public void testFindMostChampsWithValidParam(String summonerId) throws Exception {

        //when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .get(MOST_CHAMPS_URI, summonerId)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(result -> {
                    List body = objectMapper.readValue(result.getResponse().getContentAsString(), List.class);

                    System.out.println(body.size());
                    assertThat(body.size()).isLessThanOrEqualTo(MOST_CHAMP_DEFAULT_COUNT);
                    for(Object obj : body) {
                        MostChampDto mostChamp = objectMapper.convertValue(obj, MostChampDto.class);
                        assertThat(mostChamp.getQueueId()).isNull();
                        assertThat(mostChamp.getSeasonId()).isEqualTo(CURRENT_SEASON_ID);
                        System.out.println(mostChamp);
                    }
                });
    }

    @DisplayName("유효한 파라미터로 요청시 200 응답을 리턴한다.")
    @MethodSource("com.lolsearcher.search.mostchamp.MostChampSetup#validMostChampParam")
    @ParameterizedTest
    public void testFindMostChampsWithValidParam2(int queueId, int seasonId, int count) throws Exception {

        //given
        String summonerId = "summonerId1";

        //when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .get(MOST_CHAMPS_URI, summonerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param(QUEUE_ID_PARAM_KEY, Integer.toString(queueId))
                        .param(SEASON_ID_PARAM_KEY, Integer.toString(seasonId))
                        .param(COUNT_PARAM_KEY, Integer.toString(count))
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(result -> {
                    List body = objectMapper.readValue(result.getResponse().getContentAsString(), List.class);

                    assertThat(body.size()).isLessThanOrEqualTo(count);
                    for(Object obj : body) {
                        MostChampDto mostChamp = objectMapper.convertValue(obj, MostChampDto.class);
                        assertThat(mostChamp.getQueueId()).isEqualTo(queueId);
                        assertThat(mostChamp.getSeasonId()).isEqualTo(seasonId);
                        System.out.println(mostChamp);
                    }
                });
    }


    @DisplayName("잘못된 파라미터로 요청시 400 응답을 리턴한다.")
    @ValueSource(strings = {
            "  ", /* blank */
            "1234567890123456789012345678901234567890123456789012345678901234"  /* length : 64 */,
            "12345678901234567890123456789012345678901234567890123456789012345" /* length : 65 */
    })
    @ParameterizedTest
    public void testFindMostChampsWithInvalidParam1(String summonerId) throws Exception {

        //when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .get(MOST_CHAMPS_URI, summonerId)
                        .contentType(MediaType.APPLICATION_JSON)
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

    @DisplayName("잘못된 파라미터로 요청시 400 응답을 리턴한다.")
    @MethodSource("com.lolsearcher.search.mostchamp.MostChampSetup#invalidMostChampParam")
    @ParameterizedTest
    public void testFindMostChampsWithInvalidParam2(int queueId, int seasonId, int count) throws Exception {

        //given
        String summonerId = "summonerId";

        //when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .get(MOST_CHAMPS_URI, summonerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param(QUEUE_ID_PARAM_KEY, Integer.toString(queueId))
                        .param(SEASON_ID_PARAM_KEY, Integer.toString(seasonId))
                        .param(COUNT_PARAM_KEY, Integer.toString(count))
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

    @DisplayName("DB에 저장된 데이터가 잘못된 경우 500 응답을 리턴한다.")
    @MethodSource("com.lolsearcher.search.mostchamp.MostChampSetup#invalidDataBaseData")
    @ParameterizedTest
    public void testFindMostChampsWithInvalidDataInDB(String summonerId, int queueId) throws Exception {

        //when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .get(MOST_CHAMPS_URI, summonerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param(QUEUE_ID_PARAM_KEY, Integer.toString(queueId))
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
