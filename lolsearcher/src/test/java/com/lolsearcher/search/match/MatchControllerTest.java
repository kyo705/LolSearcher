package com.lolsearcher.search.match;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.constant.BeanNameConstants;
import com.lolsearcher.errors.ErrorResponseBody;
import com.lolsearcher.search.match.dto.MatchDto;
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
import org.springframework.dao.EmptyResultDataAccessException;
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
import static com.lolsearcher.search.match.MatchConstant.CHAMPION_ID_LIST;
import static com.lolsearcher.search.match.MatchConstant.QUEUE_ID_LIST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class MatchControllerTest {

    private static final String championId = "championId";
    private static final String queueId = "queueId";
    private static final String count = "count";
    private static final String offset = "offset";
    private static final String MATCH_URI = "/summoner/{summonerId}/match";

    @Autowired private ObjectMapper objectMapper;
    @Autowired private Map<String, ResponseEntity<ErrorResponseBody>> errorResponseEntities;
    @Autowired private WebApplicationContext context;
    @MockBean private MatchService matchService;
    @Autowired private CacheManager cacheManager;
    private MockMvc mockMvc;

    @BeforeEach
    public void setup(){

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();

        cacheManager.getCache(CHAMPION_ID_LIST).clear();
        cacheManager.getCache(QUEUE_ID_LIST).clear();
    }

    @DisplayName("정상적인 요청시 200 상태 코드를 리턴한다.")
    @MethodSource(value = "com.lolsearcher.search.match.MatchSetup#correctParamWithFindMatches")
    @ParameterizedTest
    public void testFindMatchesWithValidParam(MatchRequest request) throws Exception {

        //given
        List<MatchDto> result = List.of();
        given(matchService.findMatches(any())).willReturn(result);

        //when & then
        mockMvc.perform(
                MockMvcRequestBuilders
                        .get(MATCH_URI, request.getSummonerId())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()));
    }

    @DisplayName("정상적인 요청시 200 상태 코드를 리턴한다.(2)")
    @Test
    public void testFindMatchesWithValidParam2() throws Exception {

        //given
        MatchRequest request = MatchRequest.builder()
                .summonerId("summonerId")
                .championId(1)
                .queueId(1)
                .count(20)
                .offset(0)
                .build();

        cacheManager.getCache(CHAMPION_ID_LIST).put(1, "true");
        cacheManager.getCache(QUEUE_ID_LIST).put(1, "true");

        List<MatchDto> result = List.of();
        given(matchService.findMatches(any())).willReturn(result);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(MATCH_URI, request.getSummonerId())
                                .param(championId, Integer.toString(request.getChampionId()))
                                .param(queueId, Integer.toString(request.getQueueId()))
                                .param(count, Integer.toString(request.getCount()))
                                .param(offset, Integer.toString(request.getOffset()))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()));
    }

    @DisplayName("비정상적인 요청시 400 상태 코드를 리턴한다.")
    @MethodSource("com.lolsearcher.search.match.MatchSetup#incorrectParamWithFindMatches")
    @ParameterizedTest
    public void testFindMatchesWithInvalidParam(MatchRequest request) throws Exception {

        //given
        cacheManager.getCache(CHAMPION_ID_LIST).put(1, "true");
        cacheManager.getCache(QUEUE_ID_LIST).put(1, "true");

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(MATCH_URI, request.getSummonerId())
                                .param(championId, Integer.toString(request.getChampionId()))
                                .param(queueId, Integer.toString(request.getQueueId()))
                                .param(count, Integer.toString(request.getCount()))
                                .param(offset, Integer.toString(request.getOffset()))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(response -> {
                    String responseBody = response.getResponse().getContentAsString();
                    ErrorResponseBody errorResponseBody = objectMapper.readValue(responseBody, ErrorResponseBody.class);

                    ErrorResponseBody badRequestResponseBody =
                            errorResponseEntities.get(BeanNameConstants.BAD_REQUEST_ENTITY_NAME).getBody();

                    assertThat(errorResponseBody.getErrorStatusCode()).isEqualTo(badRequestResponseBody.getErrorStatusCode());
                    assertThat(errorResponseBody.getErrorMessage()).isEqualTo(badRequestResponseBody.getErrorMessage());
                });
    }

    @DisplayName("소환사 id가 없는 경우 404 상태 코드를 리턴한다.")
    @Test
    public void testFindMatchesWithInvalidSummonerId() throws Exception {

        //given
        MatchRequest request = new MatchRequest();
        request.setSummonerId("summonerId");

        given(matchService.findMatches(any())).willThrow(EmptyResultDataAccessException.class);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(MATCH_URI, request.getSummonerId())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.NOT_FOUND.value()))
                .andExpect(response -> {
                    String responseBody = response.getResponse().getContentAsString();
                    ErrorResponseBody errorResponseBody = objectMapper.readValue(responseBody, ErrorResponseBody.class);

                    ErrorResponseBody badRequestResponseBody = errorResponseEntities.get(NOT_FOUND_ENTITY_NAME).getBody();

                    assertThat(errorResponseBody.getErrorStatusCode()).isEqualTo(badRequestResponseBody.getErrorStatusCode());
                    assertThat(errorResponseBody.getErrorMessage()).isEqualTo(badRequestResponseBody.getErrorMessage());
                });
    }

    @DisplayName("외부 서버의 에러가 발생할 경우 502 상태 코드를 리턴한다.")
    @MethodSource("com.lolsearcher.search.match.MatchSetup#externalExceptionWithFindMatches")
    @ParameterizedTest
    public void testFindMatchesWithExternalServerError(Exception exception) throws Exception {

        //given
        MatchRequest request = new MatchRequest();
        request.setSummonerId("summonerId");

        given(matchService.findMatches(any())).willThrow(exception);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(MATCH_URI, request.getSummonerId())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_GATEWAY.value()))
                .andExpect(response -> {
                    String responseBody = response.getResponse().getContentAsString();
                    ErrorResponseBody errorResponseBody = objectMapper.readValue(responseBody, ErrorResponseBody.class);

                    ErrorResponseBody badRequestResponseBody = errorResponseEntities.get(BAD_GATEWAY_ENTITY_NAME).getBody();

                    assertThat(errorResponseBody.getErrorStatusCode()).isEqualTo(badRequestResponseBody.getErrorStatusCode());
                    assertThat(errorResponseBody.getErrorMessage()).isEqualTo(badRequestResponseBody.getErrorMessage());
                });
    }

    @DisplayName("외부 서버의 에러가 발생할 경우 502 상태 코드를 리턴한다.")
    @MethodSource("com.lolsearcher.search.match.MatchSetup#timeoutErrorWithFindMatches")
    @ParameterizedTest
    public void testFindMatchesWithTimeoutError(Exception exception) throws Exception {

        //given
        MatchRequest request = new MatchRequest();
        request.setSummonerId("summonerId");

        given(matchService.findMatches(any())).willThrow(exception);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(MATCH_URI, request.getSummonerId())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.GATEWAY_TIMEOUT.value()))
                .andExpect(response -> {
                    String responseBody = response.getResponse().getContentAsString();
                    ErrorResponseBody errorResponseBody = objectMapper.readValue(responseBody, ErrorResponseBody.class);

                    ErrorResponseBody badRequestResponseBody = errorResponseEntities.get(TIME_OUT_ENTITY_NAME).getBody();

                    assertThat(errorResponseBody.getErrorStatusCode()).isEqualTo(badRequestResponseBody.getErrorStatusCode());
                    assertThat(errorResponseBody.getErrorMessage()).isEqualTo(badRequestResponseBody.getErrorMessage());
                });
    }
}
