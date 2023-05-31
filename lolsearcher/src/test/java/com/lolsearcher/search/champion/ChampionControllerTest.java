package com.lolsearcher.search.champion;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.ArrayList;

import static com.lolsearcher.search.match.MatchConstant.CHAMPION_ID_LIST;
import static com.lolsearcher.search.match.MatchConstant.CURRENT_GAME_VERSION;
import static java.util.Objects.requireNonNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class ChampionControllerTest {

    private static final String CHAMPIONS_URI = "/stats/champions";
    private static final String CHAMP_ITEM_STATS_URI = "/stats/champion/{championId}/item";
    private static final String CHAMP_ENEMY_STATS_URI = "/stats/champion/{championId}/enemy";

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
    }


    //-------------------------------------- CHAMPIONS TEST ----------------------------------------

    @DisplayName("Champions : 정상적인 요청 시 200 응답을 리턴한다. (1)")
    @MethodSource("com.lolsearcher.search.champion.ChampionSetup#getValidChampionsRequest")
    @ParameterizedTest
    public void findByChampionIdWithValidParam(ChampionsRequest request) throws Exception {

        //given
        given(championService.findAllByPosition(any())).willReturn(new ArrayList<>());

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(CHAMPIONS_URI)
                                .param("positionId", request.getPosition().getName())
                                .param("gameVersion", request.getGameVersion())
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(result -> verify(championService, times(1)).findAllByPosition(any()));

    }


    @DisplayName("Champions :  정상적인 요청 시 200 응답을 리턴한다. (2)")
    @Test
    public void findByChampionIdWithValidParam2() throws Exception {

        //given
        given(championService.findAllByPosition(any())).willReturn(new ArrayList<>());

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(CHAMPIONS_URI)
                                .param("positionId", "INVALID")
                                .param("gameVersion", CURRENT_GAME_VERSION)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()));
    }

    @DisplayName("Champions :  정상적인 요청 시 200 응답을 리턴한다. (3)")
    @Test
    public void findByChampionIdWithValidParam3() throws Exception {

        //given
        given(championService.findAllByPosition(any())).willReturn(new ArrayList<>());

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(CHAMPIONS_URI)
                                .param("gameVersion", CURRENT_GAME_VERSION)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()));
    }

    @DisplayName("Champions :  정상적인 요청 시 200 응답을 리턴한다. (4)")
    @Test
    public void findByChampionIdWithValidParam4() throws Exception {

        //given
        given(championService.findAllByPosition(any())).willReturn(new ArrayList<>());

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(CHAMPIONS_URI)
                                .param("positionId", "INVALID")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()));
    }

    @DisplayName("Champions :  정상적인 요청 시 200 응답을 리턴한다. (5)")
    @Test
    public void findByChampionIdWithValidParam5() throws Exception {

        //given
        given(championService.findAllByPosition(any())).willReturn(new ArrayList<>());

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(CHAMPIONS_URI)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()));
    }



    //-------------------------------------- CHAMP ITEM STATS TEST ----------------------------------------

    @DisplayName("Item : 정상적인 요청 시 200 응답을 리턴한다.")
    @MethodSource("com.lolsearcher.search.champion.ChampionSetup#getValidChampionDetailsRequest")
    @ParameterizedTest
    public void getChampItemStatsWithValidParam(ChampionDetailsRequest request) throws Exception {

        //given
        requireNonNull(cacheManager.getCache(CHAMPION_ID_LIST)).put(request.getChampionId(), "true");

        given(championService.findItemStats(any())).willReturn(new ArrayList<>());

        //when & then
        mockMvc.perform(
                MockMvcRequestBuilders
                        .get(CHAMP_ITEM_STATS_URI, request.getChampionId())
                        .param("gameVersion", request.getGameVersion())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(result -> verify(championService, times(1)).findItemStats(any()));
    }

    @DisplayName("Item : 잘못된 championId로 요청 시 400 응답을 리턴한다.")
    @MethodSource("com.lolsearcher.search.champion.ChampionSetup#getValidChampionDetailsRequest")
    @ParameterizedTest
    public void getChampItemStatsWithInvalidParam(ChampionDetailsRequest request) throws Exception {

        //given

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(CHAMP_ITEM_STATS_URI, request.getChampionId())
                                .param("gameVersion", request.getGameVersion())
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()));
    }

    //-------------------------------------- CHAMP ENEMY STATS TEST ----------------------------------------

    @DisplayName("Enemy : 정상적인 요청 시 200 응답을 리턴한다.")
    @MethodSource("com.lolsearcher.search.champion.ChampionSetup#getValidChampionDetailsRequest")
    @ParameterizedTest
    public void getChampEnemyStatsWithValidParam(ChampionDetailsRequest request) throws Exception {

        //given
        requireNonNull(cacheManager.getCache(CHAMPION_ID_LIST)).put(request.getChampionId(), "true");

        given(championService.findEnemyStats(any())).willReturn(new ArrayList<>());

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(CHAMP_ENEMY_STATS_URI, request.getChampionId())
                                .param("gameVersion", request.getGameVersion())
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(result -> verify(championService, times(1)).findEnemyStats(any()));
    }

    @DisplayName("Enemy : 잘못된 championId로 요청 시 400 응답을 리턴한다.")
    @MethodSource("com.lolsearcher.search.champion.ChampionSetup#getValidChampionDetailsRequest")
    @ParameterizedTest
    public void getChampEnemyStatsWithInvalidParam(ChampionDetailsRequest request) throws Exception {

        //given

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(CHAMP_ENEMY_STATS_URI, request.getChampionId())
                                .param("gameVersion", request.getGameVersion())
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()));
    }

}
