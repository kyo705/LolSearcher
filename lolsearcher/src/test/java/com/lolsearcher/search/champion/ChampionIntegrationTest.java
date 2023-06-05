package com.lolsearcher.search.champion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.config.EmbeddedRedisConfig;
import com.lolsearcher.config.ErrorResponseEntityConfig.ErrorResponseBody;
import com.lolsearcher.search.champion.entity.ChampEnemyStats;
import com.lolsearcher.search.champion.entity.ChampItemStats;
import com.lolsearcher.search.champion.entity.ChampPositionStats;
import com.lolsearcher.search.match.MatchConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.List;
import java.util.Map;

import static com.lolsearcher.config.ErrorResponseEntityConfig.BAD_REQUEST_ENTITY_NAME;
import static com.lolsearcher.search.champion.ChampionSetup.*;
import static org.assertj.core.api.Assertions.assertThat;

@Import({EmbeddedRedisConfig.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
public class ChampionIntegrationTest {

    private static final String CHAMPIONS_URI = "/stats/champions";
    private static final String CHAMPION_ITEM_STATS_URI = "/stats/champion/{championId}/item";
    private static final String CHAMPION_ENEMY_STATS_URI = "/stats/champion/{championId}/enemy";
    private static final String POSITION_PARAM_NAME = "position";
    private static final String GAME_VERSION_PARAM_NAME = "gameVersion";

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private Map<String, ResponseEntity<ErrorResponseBody>> errorResponseEntities;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    private MockMvc mockMvc;

    @BeforeEach
    public void setup(){
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();

        cacheManager.getCache(MatchConstant.GAME_VERSION_LIST).clear();
        cacheManager.getCache(MatchConstant.CHAMPION_ID_LIST).clear();
    }

    @DisplayName("Champions : 유효한 파라미터로 요청시 정렬된 값들을 리턴한다.")
    @MethodSource("com.lolsearcher.search.champion.ChampionSetup#validChampionsParam")
    @ParameterizedTest
    @Transactional
    public void testFindChampionsWithValidParam(String position, String gameVersion) throws Exception {

        //given
        setupWithCache(cacheManager);
        setupChampPositionStatsWithRedis(stringRedisTemplate.opsForZSet(), objectMapper);

        //when & then
        mockMvc.perform(
                MockMvcRequestBuilders.get(CHAMPIONS_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param(POSITION_PARAM_NAME, position)
                        .param(GAME_VERSION_PARAM_NAME, gameVersion)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(result -> {
                    Double before = Double.MAX_VALUE;

                    for(Object value : objectMapper.readValue(result.getResponse().getContentAsString(), List.class)) {
                        double current = score(objectMapper.convertValue(value, ChampPositionStats.class));
                        assertThat(current).isLessThanOrEqualTo(before); /* 정렬되었는지 검증 */
                        before = current;
                    }
                });
    }

    @DisplayName("Champions : 잘못된 파라미터로 요청시 400 상태코드를 리턴한다.")
    @MethodSource("com.lolsearcher.search.champion.ChampionSetup#invalidChampionsParam")
    @ParameterizedTest
    @Transactional
    public void testFindChampionsWithInvalidParam(String position, String gameVersion) throws Exception {

        //given
        setupWithCache(cacheManager);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.get(CHAMPIONS_URI)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param(POSITION_PARAM_NAME, position)
                                .param(GAME_VERSION_PARAM_NAME, gameVersion)
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



    // ----------------------------------- Champion item stats ----------------------------------------

    @DisplayName("item : 유효한 파라미터로 요청시 정렬된 값들을 리턴한다.")
    @MethodSource("com.lolsearcher.search.champion.ChampionSetup#validChampionDetailsParam")
    @ParameterizedTest
    @Transactional
    public void testFindItemStatsWithValidParam(int championId, String gameVersion) throws Exception {

        //given
        setupWithCache(cacheManager);
        setupChampItemStatsWithRedis(stringRedisTemplate.opsForZSet(), objectMapper);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.get(CHAMPION_ITEM_STATS_URI, championId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param(GAME_VERSION_PARAM_NAME, gameVersion)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(result -> {
                    Double before = Double.MAX_VALUE;

                    for(Object value : objectMapper.readValue(result.getResponse().getContentAsString(), List.class)) {
                        double current = score(objectMapper.convertValue(value, ChampItemStats.class));
                        assertThat(current).isLessThanOrEqualTo(before); /* 정렬되었는지 검증 */
                        before = current;
                    }
                });
    }

    @DisplayName("item : 잘못된 파라미터로 요청시 400 상태코드를 리턴한다.")
    @MethodSource("com.lolsearcher.search.champion.ChampionSetup#invalidChampionDetailsParam")
    @ParameterizedTest
    @Transactional
    public void testFindItemStatsWithInvalidParam(int championId, String gameVersion) throws Exception {

        //given
        setupWithCache(cacheManager);
        setupChampItemStatsWithRedis(stringRedisTemplate.opsForZSet(), objectMapper);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.get(CHAMPION_ITEM_STATS_URI, championId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param(GAME_VERSION_PARAM_NAME, gameVersion)
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



    // ----------------------------------- Champion enemy stats ----------------------------------------

    @DisplayName("Enemy : 유효한 파라미터로 요청시 정렬된 값들을 리턴한다.")
    @MethodSource("com.lolsearcher.search.champion.ChampionSetup#validChampionDetailsParam")
    @ParameterizedTest
    @Transactional
    public void testFindEnemyStatsWithValidParam(int championId, String gameVersion) throws Exception {

        //given
        setupWithCache(cacheManager);
        setupChampEnemyStatsWithRedis(stringRedisTemplate.opsForZSet(), objectMapper);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.get(CHAMPION_ENEMY_STATS_URI, championId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param(GAME_VERSION_PARAM_NAME, gameVersion)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(result -> {
                    Double before = Double.MAX_VALUE;

                    for(Object value : objectMapper.readValue(result.getResponse().getContentAsString(), List.class)) {
                        double current = score(objectMapper.convertValue(value, ChampEnemyStats.class));
                        assertThat(current).isLessThanOrEqualTo(before); /* 정렬되었는지 검증 */
                        before = current;
                    }
                });
    }

    @DisplayName("Enemy : 잘못된 파라미터로 요청시 400 상태코드를 리턴한다.")
    @MethodSource("com.lolsearcher.search.champion.ChampionSetup#invalidChampionDetailsParam")
    @ParameterizedTest
    @Transactional
    public void testFindEnemyStatsWithInvalidParam(int championId, String gameVersion) throws Exception {

        //given
        setupWithCache(cacheManager);
        setupChampEnemyStatsWithRedis(stringRedisTemplate.opsForZSet(), objectMapper);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.get(CHAMPION_ENEMY_STATS_URI, championId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param(GAME_VERSION_PARAM_NAME, gameVersion)
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
}
