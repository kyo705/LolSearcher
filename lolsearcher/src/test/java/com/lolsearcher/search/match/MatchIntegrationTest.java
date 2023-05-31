package com.lolsearcher.search.match;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.errors.ErrorResponseBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
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

import static com.lolsearcher.constant.BeanNameConstants.*;
import static com.lolsearcher.search.match.MatchConstant.*;
import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
public class MatchIntegrationTest {

    private static final String championId = "championId";
    private static final String queueId = "queueId";
    private static final String count = "count";
    private static final String offset = "offset";
    private static final String MATCH_URI = "/summoner/{summonerId}/match";

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired private Map<String, ResponseEntity<ErrorResponseBody>> errorResponseEntities;
    @Autowired private WebApplicationContext context;
    @Autowired private CacheManager cacheManager;
    private MockMvc mockMvc;

    @BeforeEach
    public void setup(){

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();

        cacheManager.getCache(CHAMPION_ID_LIST).clear();
        cacheManager.getCache(ITEM_ID_LIST).clear();
        cacheManager.getCache(PERK_ID_LIST).clear();
        cacheManager.getCache(QUEUE_ID_LIST).clear();
    }

    @DisplayName("정상적인 요청은 200 상태코드를 리턴한다.")
    @Test
    public void testWithSuccess() throws Exception {

        //given
        MatchRequest request = MatchRequest.builder()
                .summonerId("summoner1")
                .build();

        Cache cache1 = cacheManager.getCache(CHAMPION_ID_LIST);
        cache1.put(1, "true");cache1.put(2, "true");cache1.put(3, "true");cache1.put(4, "true");cache1.put(5, "true");
        cache1.put(6, "true");cache1.put(7, "true");cache1.put(8, "true");cache1.put(9, "true");cache1.put(10, "true");
        cache1.put(11, "true");cache1.put(12, "true");cache1.put(13, "true");cache1.put(14, "true");cache1.put(15, "true");
        cache1.put(16, "true");cache1.put(17, "true");cache1.put(18, "true");cache1.put(19, "true");cache1.put(20, "true");

        Cache cache2 = cacheManager.getCache(ITEM_ID_LIST);
        cache2.put(0, "true");cache2.put(1, "true");cache2.put(2, "true");cache2.put(3, "true");cache2.put(4, "true");
        cache2.put(5, "true");cache2.put(6, "true");

        Cache cache3 = cacheManager.getCache(PERK_ID_LIST);
        cache3.put(1000, "true");cache3.put(2000, "true");cache3.put(100, "true");cache3.put(500, "true");cache3.put(600, "true");
        cache3.put(1, "true");cache3.put(2, "true");cache3.put(3, "true");cache3.put(4, "true");cache3.put(21, "true");cache3.put(22, "true");

        Cache cache4 = cacheManager.getCache(QUEUE_ID_LIST);
        cache4.put(1, "true");

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(MATCH_URI, request.getSummonerId())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(result -> {
                    List body = objectMapper.readValue(result.getResponse().getContentAsString(), List.class);
                    System.out.println(body);
                });
    }

    @DisplayName("DB의 데이터가 유효하지 않을 경우 500 에러를 리턴한다.")
    @Test
    public void testWithHavingInvalidDBData() throws Exception {

        //given
        MatchRequest request = MatchRequest.builder()
                .summonerId("summoner1")
                .build();

        //when & then
        mockMvc.perform(
                MockMvcRequestBuilders
                        .get(MATCH_URI, request.getSummonerId())
                        .contentType(MediaType.APPLICATION_JSON)
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

    @DisplayName("DB에 존재하지 않는 데이터 요청 시 404 에러를 리턴한다.")
    @Test
    public void testWithBadRequest() throws Exception {

        //given
        MatchRequest request = MatchRequest.builder()
                .summonerId("summoner1")
                .queueId(1)
                .build();

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(MATCH_URI, request.getSummonerId())
                                .param(queueId, Integer.toString(request.getQueueId()))
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

    @DisplayName("DB에 존재하지 않는 데이터 요청 시 404 에러를 리턴한다.")
    @Test
    public void testWithNotFoundedData() throws Exception {

        //given
        MatchRequest request = MatchRequest.builder()
                .summonerId("summoner100")
                .build();

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(MATCH_URI, request.getSummonerId())
                                .contentType(MediaType.APPLICATION_JSON)
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
}
