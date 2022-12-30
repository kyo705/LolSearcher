package com.lolsearcher.integration.cache;

import com.lolsearcher.api.riotgames.RiotRestAPI;
import com.lolsearcher.config.EmbeddedRedisConfig;
import com.lolsearcher.constant.CacheConstants;
import com.lolsearcher.repository.match.MatchRepository;
import com.lolsearcher.service.match.MatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ActiveProfiles("test")
@SpringBootTest(classes = {EmbeddedRedisConfig.class})
public class MatchDataCacheTest {

    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private MatchService matchService;
    @MockBean
    private RiotRestAPI riotRestAPI;
    @MockBean
    private MatchRepository matchRepository;

    @BeforeEach
    public void upSet() {
        cacheManager.getCache(CacheConstants.MATCH_KEY).clear();
    }


    @DisplayName("REST API로 응답된 Match 데이터는 Redis 캐시에 저장된다.")
    @Test
    public void savingDataInCacheTest() {

        //given
        List<String> matchIds = CacheTestUpSet.getMatchIds();

        for(String matchId : matchIds){
            assertThat(cacheManager.getCache(CacheConstants.MATCH_KEY).get(matchId)).isNull();

            given(riotRestAPI.getMatchByNonBlocking(matchId)).willReturn(CacheTestUpSet.getMatchMono(matchId));
        }

        //when
        matchService.getRenewMatches(matchIds);

        //then
        for(String matchId : matchIds){
            assertThat(cacheManager.getCache(CacheConstants.MATCH_KEY).get(matchId)).isNotNull();
        }
    }

    @DisplayName("Redis 캐시에 저장된 데이터는 ttl 이후 삭제된다.")
    @Test
    public void removedDataInCacheTest() throws InterruptedException {

        //given
        List<String> matchIds = CacheTestUpSet.getMatchIds();

        for(String matchId : matchIds){
            assertThat(cacheManager.getCache(CacheConstants.MATCH_KEY).get(matchId)).isNull();

            given(riotRestAPI.getMatchByNonBlocking(matchId)).willReturn(CacheTestUpSet.getMatchMono(matchId));
        }

        matchService.getRenewMatches(matchIds);

        for(String matchId : matchIds){
            assertThat(cacheManager.getCache(CacheConstants.MATCH_KEY).get(matchId)).isNotNull();
        }

        //when
        Thread.sleep(3000);

        //then
        for(String matchId : matchIds){
            assertThat(cacheManager.getCache(CacheConstants.MATCH_KEY).get(matchId)).isNull();
        }
    }
}
