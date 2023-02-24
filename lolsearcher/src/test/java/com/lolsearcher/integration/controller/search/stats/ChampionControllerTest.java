package com.lolsearcher.integration.controller.search.stats;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.constant.BeanNameConstants;
import com.lolsearcher.exception.exception.common.NoExistDataException;
import com.lolsearcher.model.request.search.championstats.RequestChampDetailStatsDto;
import com.lolsearcher.model.request.search.championstats.RequestChampPositionStatsDto;
import com.lolsearcher.model.response.error.ErrorResponseBody;
import com.lolsearcher.model.response.front.search.championstats.ChampPositionStatsDto;
import com.lolsearcher.model.response.front.search.championstats.TotalChampStatDto;
import com.lolsearcher.service.search.stats.ChampionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class ChampionControllerTest {

    private static final String CHAMP_STATS_URI = "/stats/champion";
    private static final String CHAMP_STATS_DETAIL_URI = "/stats/champion/detail";

    @Autowired private ObjectMapper objectMapper;
    @Autowired private WebApplicationContext context;
    @Autowired private Map<String, ResponseEntity<ErrorResponseBody>> errorResponseEntities;
    @MockBean private ChampionService championService;
    @MockBean private CacheManager cacheManager;
    @MockBean private Cache cache;
    private MockMvc mockMvc;

    @BeforeEach
    public void setup(){

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }


    /* CHAMPION CONTROLLER TEST */

    @DisplayName("정상적인 요청이 들어오면 포지션별 챔피언 통계 데이터를 리턴한다.")
    @Test
    public void getChampStatsWithSuccess() throws Exception {

        //given
        RequestChampPositionStatsDto request = ChampionControllerTestSetup.getValidChampPositionRequest();
        String requestBody = objectMapper.writeValueAsString(request);

        List<ChampPositionStatsDto> result = ChampionControllerTestSetup.getChampPositionStats(request);
        given(championService.getAllChampPositionStats(any(RequestChampPositionStatsDto.class))).willReturn(result);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .post(CHAMP_STATS_URI)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(response -> {
                   String responseBody = response.getResponse().getContentAsString();
                   List<ChampPositionStatsDto> responseChampPositionStats = objectMapper.readValue(responseBody, List.class);

                   assertThat(responseChampPositionStats.size()).isEqualTo(result.size());
                });
    }

    @DisplayName("유효하지 않는 파라미터로 요청이 들어오면 400 에러를 리턴한다.")
    @ParameterizedTest
    @MethodSource("com.lolsearcher.integration.controller.search.stats.ChampionControllerTestSetup#getInvalidChampPositionRequest")
    public void getChampStatsWithInvalidParameter(RequestChampPositionStatsDto request) throws Exception {

        //given
        String requestBody = objectMapper.writeValueAsString(request);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .post(CHAMP_STATS_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
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

    @DisplayName("DB에 데이터가 없는 경우 500 에러를 리턴한다.")
    @Test
    public void getChampStatsWithNoDataInDB() throws Exception {

        //given
        RequestChampPositionStatsDto request = ChampionControllerTestSetup.getValidChampPositionRequest();
        String requestBody = objectMapper.writeValueAsString(request);

        given(championService.getAllChampPositionStats(request)).willThrow(NoExistDataException.class);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .post(CHAMP_STATS_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(response -> {
                    String responseBody = response.getResponse().getContentAsString();
                    ErrorResponseBody errorResponseBody = objectMapper.readValue(responseBody, ErrorResponseBody.class);

                    ErrorResponseBody internalServerErrorResponseBody =
                            errorResponseEntities.get(BeanNameConstants.INTERNAL_SERVER_ERROR_ENTITY_NAME).getBody();

                    assertThat(errorResponseBody.getErrorStatusCode()).isEqualTo(internalServerErrorResponseBody.getErrorStatusCode());
                    assertThat(errorResponseBody.getErrorMessage()).isEqualTo(internalServerErrorResponseBody.getErrorMessage());
                });
    }


    /* CHAMPION DETAIL CONTROLLER TEST */

    @DisplayName("정상적인 요청이 들어오면 포지션별 챔피언 통계 데이터를 리턴한다.")
    @Test
    public void getChampDetailStatsWithSuccess() throws Exception {

        //given
        RequestChampDetailStatsDto request = ChampionControllerTestSetup.getValidChampDetailRequest();
        String requestBody = objectMapper.writeValueAsString(request);

        given(cacheManager.getCache(any())).willReturn(cache);
        given(cache.get(any())).willReturn(()->"exist");

        TotalChampStatDto result = ChampionControllerTestSetup.getChampDetailStats(request);
        given(championService.getChampDetailStats(request)).willReturn(result);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .post(CHAMP_STATS_DETAIL_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()));
    }

    @DisplayName("유효하지 않는 파라미터로 요청이 들어오면 400 에러를 리턴한다.")
    @ParameterizedTest
    @MethodSource("com.lolsearcher.integration.controller.search.stats.ChampionControllerTestSetup#getInvalidChampDetailRequest")
    public void getChampDetailStatsWithInvalidParameter(RequestChampDetailStatsDto request) throws Exception {

        //given
        String requestBody = objectMapper.writeValueAsString(request);

        given(cacheManager.getCache(any())).willReturn(cache);
        given(cache.get(1, String.class)).willReturn("exist");
        given(cache.get(-1)).willReturn(null);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .post(CHAMP_STATS_DETAIL_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
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

    @DisplayName("DB에 데이터가 없는 경우 500 에러를 리턴한다.")
    @Test
    public void getChampDetailStatsWithNoDataInDB() throws Exception {

        //given
        RequestChampDetailStatsDto request = ChampionControllerTestSetup.getValidChampDetailRequest();
        String requestBody = objectMapper.writeValueAsString(request);

        given(cacheManager.getCache(any())).willReturn(cache);
        given(cache.get(any())).willReturn(() -> "exist");
        given(championService.getChampDetailStats(request)).willThrow(NoExistDataException.class);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .post(CHAMP_STATS_DETAIL_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(response -> {
                    String responseBody = response.getResponse().getContentAsString();
                    ErrorResponseBody errorResponseBody = objectMapper.readValue(responseBody, ErrorResponseBody.class);

                    ErrorResponseBody internalServerErrorResponseBody =
                            errorResponseEntities.get(BeanNameConstants.INTERNAL_SERVER_ERROR_ENTITY_NAME).getBody();

                    assertThat(errorResponseBody.getErrorStatusCode()).isEqualTo(internalServerErrorResponseBody.getErrorStatusCode());
                    assertThat(errorResponseBody.getErrorMessage()).isEqualTo(internalServerErrorResponseBody.getErrorMessage());
                });
    }
}
