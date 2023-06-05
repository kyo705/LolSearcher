package com.lolsearcher.search.champion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.config.EmbeddedRedisConfig;
import com.lolsearcher.config.ErrorResponseEntityConfig.ErrorResponseBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

import java.util.ArrayList;
import java.util.Map;

import static com.lolsearcher.config.ErrorResponseEntityConfig.BAD_REQUEST_ENTITY_NAME;
import static com.lolsearcher.search.match.MatchConstant.*;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Import({EmbeddedRedisConfig.class})
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class ChampionControllerTest {

    private static final String POSITION_PARAM_KEY = "position";
    private static final String GAME_VERSION_PARAM_KEY = "gameVersion";
    private static final String CHAMPIONS_URI = "/stats/champions";
    private static final String CHAMP_ITEM_STATS_URI = "/stats/champion/{championId}/item";
    private static final String CHAMP_ENEMY_STATS_URI = "/stats/champion/{championId}/enemy";

    @Autowired private Map<String, ResponseEntity<ErrorResponseBody>> errorResponseEntities;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private WebApplicationContext context;
    @Autowired private CacheManager cacheManager;
    @MockBean private ChampionService championService;
    private MockMvc mockMvc;

    @BeforeEach
    public void setup(){

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();

        requireNonNull(cacheManager.getCache(CHAMPION_ID_LIST)).clear();
        requireNonNull(cacheManager.getCache(GAME_VERSION_LIST)).clear();
    }


    //-------------------------------------- CHAMPIONS TEST ----------------------------------------

    @DisplayName("Champions : 정상적인 요청 시 200 응답을 리턴한다. (1)")
    @MethodSource("com.lolsearcher.search.champion.ChampionSetup#validChampionsParam")
    @ParameterizedTest
    public void findByChampionIdWithValidParam(String champion, String gameVersion) throws Exception {

        //given
        requireNonNull(cacheManager.getCache(GAME_VERSION_LIST)).put(gameVersion, "true");

        given(championService.findAllByPosition(any())).willReturn(new ArrayList<>());

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(CHAMPIONS_URI)
                                .param(POSITION_PARAM_KEY, champion)
                                .param(GAME_VERSION_PARAM_KEY, gameVersion)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(result -> verify(championService, times(1)).findAllByPosition(any()));

    }

    @DisplayName("Champions :  정상적인 요청 시 200 응답을 리턴한다. (2)")
    @Test
    public void findByChampionIdWithValidParam2() throws Exception {

        //given
        requireNonNull(cacheManager.getCache(GAME_VERSION_LIST)).put(CURRENT_GAME_VERSION, "true");

        given(championService.findAllByPosition(any())).willReturn(new ArrayList<>());

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(CHAMPIONS_URI)
                                .param(GAME_VERSION_PARAM_KEY, CURRENT_GAME_VERSION)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()));
    }

    @DisplayName("Champions :  정상적인 요청 시 200 응답을 리턴한다. (3)")
    @Test
    public void findByChampionIdWithValidParam3() throws Exception {

        //given
        requireNonNull(cacheManager.getCache(GAME_VERSION_LIST)).put(CURRENT_GAME_VERSION, "true");

        given(championService.findAllByPosition(any())).willReturn(new ArrayList<>());

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(CHAMPIONS_URI)
                                .param(POSITION_PARAM_KEY, "MIDDLE")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()));
    }

    @DisplayName("Champions :  정상적인 요청 시 200 응답을 리턴한다. (4)")
    @Test
    public void findByChampionIdWithValidParam4() throws Exception {

        //given
        requireNonNull(cacheManager.getCache(GAME_VERSION_LIST)).put(CURRENT_GAME_VERSION, "true");

        given(championService.findAllByPosition(any())).willReturn(new ArrayList<>());

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(CHAMPIONS_URI)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()));
    }


    @DisplayName("Champions :  잘못된 파라미터로 요청 시 400 응답을 리턴한다.")
    @MethodSource("com.lolsearcher.search.champion.ChampionSetup#invalidChampionsParam")
    @ParameterizedTest
    public void findByChampionIdWithInvalidParam(String position, String gameVersion) throws Exception {

        //given
        requireNonNull(cacheManager.getCache(GAME_VERSION_LIST)).put(CURRENT_GAME_VERSION, "true");

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(CHAMPIONS_URI)
                                .param(POSITION_PARAM_KEY, position)
                                .param(GAME_VERSION_PARAM_KEY, gameVersion)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
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



    //-------------------------------------- CHAMP ITEM STATS TEST ----------------------------------------

    @DisplayName("Item : 정상적인 요청 시 200 응답을 리턴한다.")
    @Test
    public void getChampItemStatsWithValidParam() throws Exception {

        //given
        int championId = 1;
        String gameVersion = CURRENT_GAME_VERSION;

        requireNonNull(cacheManager.getCache(CHAMPION_ID_LIST)).put(championId, "talon");
        requireNonNull(cacheManager.getCache(GAME_VERSION_LIST)).put(gameVersion, "true");

        given(championService.findItemStats(any())).willReturn(new ArrayList<>());

        //when & then
        mockMvc.perform(
                MockMvcRequestBuilders
                        .get(CHAMP_ITEM_STATS_URI, championId)
                        .param(GAME_VERSION_PARAM_KEY, gameVersion)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(result -> verify(championService, times(1)).findItemStats(any()));
    }

    @DisplayName("Item : 정상적인 요청 시 200 응답을 리턴한다.")
    @Test
    public void getChampItemStatsWithValidParam2() throws Exception {

        //given
        int championId = 1;

        requireNonNull(cacheManager.getCache(CHAMPION_ID_LIST)).put(championId, "talon");
        requireNonNull(cacheManager.getCache(GAME_VERSION_LIST)).put(CURRENT_GAME_VERSION, "true");

        given(championService.findItemStats(any())).willReturn(new ArrayList<>());

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(CHAMP_ITEM_STATS_URI, championId)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(result -> verify(championService, times(1)).findItemStats(any()));
    }

    @DisplayName("Item : 잘못된 championId로 요청 시 400 응답을 리턴한다.")
    @MethodSource("com.lolsearcher.search.champion.ChampionSetup#invalidChampionDetailsParam")
    @ParameterizedTest
    public void getChampItemStatsWithInvalidParam(int championId, String gameVersion) throws Exception {

        //given
        requireNonNull(cacheManager.getCache(CHAMPION_ID_LIST)).put(1, "talon");
        requireNonNull(cacheManager.getCache(GAME_VERSION_LIST)).put(CURRENT_GAME_VERSION, "true");

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(CHAMP_ITEM_STATS_URI, championId)
                                .param(GAME_VERSION_PARAM_KEY, gameVersion)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(response -> {
                    ErrorResponseBody body = objectMapper.readValue(response.getResponse().getContentAsString(), ErrorResponseBody.class);
                    ErrorResponseBody expected = errorResponseEntities.get(BAD_REQUEST_ENTITY_NAME).getBody();

                    assertThat(expected).isNotNull();
                    assertThat(body.getErrorStatusCode()).isEqualTo(expected.getErrorStatusCode());
                    assertThat(body.getErrorMessage()).isEqualTo(expected.getErrorMessage());
                });;
    }

    //-------------------------------------- CHAMP ENEMY STATS TEST ----------------------------------------

    @DisplayName("Enemy : 정상적인 요청 시 200 응답을 리턴한다.")
    @Test
    public void getChampEnemyStatsWithValidParam() throws Exception {

        //given
        int championId = 1;
        String gameVersion = CURRENT_GAME_VERSION;

        requireNonNull(cacheManager.getCache(CHAMPION_ID_LIST)).put(championId, "talon");
        requireNonNull(cacheManager.getCache(GAME_VERSION_LIST)).put(gameVersion, "true");

        given(championService.findEnemyStats(any())).willReturn(new ArrayList<>());

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(CHAMP_ENEMY_STATS_URI, championId)
                                .param(GAME_VERSION_PARAM_KEY, gameVersion)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(result -> verify(championService, times(1)).findEnemyStats(any()));
    }

    @DisplayName("Enemy : 정상적인 요청 시 200 응답을 리턴한다.")
    @Test
    public void getChampEnemyStatsWithValidParam2() throws Exception {

        //given
        int championId = 1;

        requireNonNull(cacheManager.getCache(CHAMPION_ID_LIST)).put(championId, "talon");
        requireNonNull(cacheManager.getCache(GAME_VERSION_LIST)).put(CURRENT_GAME_VERSION, "true");

        given(championService.findEnemyStats(any())).willReturn(new ArrayList<>());

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(CHAMP_ENEMY_STATS_URI, championId)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(result -> verify(championService, times(1)).findEnemyStats(any()));
    }

    @DisplayName("Enemy : 잘못된 championId로 요청 시 400 응답을 리턴한다.")
    @MethodSource("com.lolsearcher.search.champion.ChampionSetup#invalidChampionDetailsParam")
    @ParameterizedTest
    public void getChampEnemyStatsWithInvalidParam(int championId, String gameVersion) throws Exception {

        //given
        requireNonNull(cacheManager.getCache(CHAMPION_ID_LIST)).put(1, "talon");
        requireNonNull(cacheManager.getCache(GAME_VERSION_LIST)).put(CURRENT_GAME_VERSION, "true");

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(CHAMP_ENEMY_STATS_URI, championId)
                                .param(GAME_VERSION_PARAM_KEY, gameVersion)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(response -> {
                    ErrorResponseBody body = objectMapper.readValue(response.getResponse().getContentAsString(), ErrorResponseBody.class);
                    ErrorResponseBody expected = errorResponseEntities.get(BAD_REQUEST_ENTITY_NAME).getBody();

                    assertThat(expected).isNotNull();
                    assertThat(body.getErrorStatusCode()).isEqualTo(expected.getErrorStatusCode());
                    assertThat(body.getErrorMessage()).isEqualTo(expected.getErrorMessage());
                });
    }

}
