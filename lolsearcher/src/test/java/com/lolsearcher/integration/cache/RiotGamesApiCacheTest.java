package com.lolsearcher.integration.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.api.riotgames.RiotRestAPI;
import com.lolsearcher.config.EmbeddedRedisConfig;
import com.lolsearcher.config.MockWebServerConfig;
import com.lolsearcher.constant.CacheConstants;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;


import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(classes = {EmbeddedRedisConfig.class, MockWebServerConfig.class})
public class RiotGamesApiCacheTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private MockWebServer mockWebServer;
    @Autowired
    private RiotRestAPI riotRestAPI;

    @BeforeEach
    public void upSet() {
        cacheManager.getCache(CacheConstants.IN_GAME_KEY).clear();
    }


    @DisplayName("REST API로 응답된 인게임 데이터는 Redis 캐시에 저장된다.")
    @Test
    public void savingDataInCacheTest() throws JsonProcessingException {

        //given
        String summonerId = "summonerId1";

        assertThat(cacheManager.getCache(CacheConstants.IN_GAME_KEY).get(summonerId)).isNull();

        mockWebServer.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setBody(objectMapper.writeValueAsString(CacheTestUpSet.createInGameDto(summonerId)))
        );

        //when
        riotRestAPI.getInGameBySummonerId(summonerId);

        //then
        assertThat(cacheManager.getCache(CacheConstants.IN_GAME_KEY).get(summonerId)).isNotNull();
    }

    @DisplayName("Redis 캐시에 저장된 데이터는 ttl 이후 삭제된다.")
    @Test
    public void removedDataInCacheTest() throws InterruptedException, JsonProcessingException {

        //given
        String summonerId = "summonerId1";

        assertThat(cacheManager.getCache(CacheConstants.IN_GAME_KEY).get(summonerId)).isNull();

        mockWebServer.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setBody(objectMapper.writeValueAsString(CacheTestUpSet.createInGameDto(summonerId)))
        );

        riotRestAPI.getInGameBySummonerId(summonerId);

        assertThat(cacheManager.getCache(CacheConstants.IN_GAME_KEY).get(summonerId)).isNotNull();

        //when
        Thread.sleep(3000);

        //then
        assertThat(cacheManager.getCache(CacheConstants.IN_GAME_KEY).get(summonerId)).isNull();
    }
}
