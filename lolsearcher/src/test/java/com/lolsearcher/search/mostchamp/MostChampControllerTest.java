package com.lolsearcher.search.mostchamp;

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
import org.springframework.dao.QueryTimeoutException;
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
import static com.lolsearcher.config.ErrorResponseEntityConfig.TIME_OUT_ENTITY_NAME;
import static com.lolsearcher.search.match.MatchConstant.QUEUE_ID_LIST;
import static com.lolsearcher.search.mostchamp.MostChampConstant.MOST_CHAMPS_URI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Import({EmbeddedRedisConfig.class})
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class MostChampControllerTest {

    private static final String QUEUE_ID_KEY = "queueId";
    private static final String SEASON_ID_KEY = "seasonId";
    private static final String COUNT_KEY = "count";

    @Autowired private WebApplicationContext context;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private Map<String, ResponseEntity<ErrorResponseBody>> errorResponseEntities;
    @Autowired private CacheManager cacheManager;
    @MockBean private MostChampService mostChampService;
    private MockMvc mockMvc;

    @BeforeEach
    public void setup(){

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();

        cacheManager.getCache(QUEUE_ID_LIST).clear();
    }

    @DisplayName("정상적인 파라미터로 요청시 200 상태코드를 리턴한다.")
    @MethodSource("com.lolsearcher.search.mostchamp.MostChampSetup#validRequest")
    @ParameterizedTest
    public void getMostChampWithSuccess(MostChampRequest request) throws Exception {

        //given
        cacheManager.getCache(QUEUE_ID_LIST).put(request.getQueueId(), "true");

        List<MostChampDto> result = List.of();
        given(mostChampService.getMostChamps(request)).willReturn(result);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .get(MOST_CHAMPS_URI, request.getSummonerId())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(response -> verify(mostChampService, times(1)).getMostChamps(any()));
    }

    @DisplayName("유효하지 않은 파라미터로 요청시 400 에러를 리턴한다.")
    @MethodSource("com.lolsearcher.search.mostchamp.MostChampSetup#invalidRequest")
    @ParameterizedTest
    public void getMostChampWithInvalidParam(MostChampRequest request) throws Exception {

        //when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .get(MOST_CHAMPS_URI, request.getSummonerId())
                        .param(QUEUE_ID_KEY, Integer.toString(request.getQueueId()))
                        .param(SEASON_ID_KEY, Integer.toString(request.getSeasonId()))
                        .param(COUNT_KEY, Integer.toString(request.getCount()))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    ErrorResponseBody body = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponseBody.class);
                    ErrorResponseBody expected = errorResponseEntities.get(BAD_REQUEST_ENTITY_NAME).getBody();

                    assertThat(expected).isNotNull();
                    assertThat(body.getErrorStatusCode()).isEqualTo(expected.getErrorStatusCode());
                    assertThat(body.getErrorMessage()).isEqualTo(expected.getErrorMessage());

                    verify(mostChampService, times(0)).getMostChamps(any());
                });
    }

    @DisplayName("외부 응답이 time out 발생할 경우 504 에러를 리턴한다.")
    @Test
    public void getMostChampWithJPAException() throws Exception {

        //given
        cacheManager.getCache(QUEUE_ID_LIST).put(1, "true");

        MostChampRequest request = MostChampRequest.builder().summonerId("summonerId").queueId(1).build();
        String requestBody = objectMapper.writeValueAsString(request);

        given(mostChampService.getMostChamps(any())).willThrow(QueryTimeoutException.class);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .get(MOST_CHAMPS_URI, request.getSummonerId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param(QUEUE_ID_KEY, Integer.toString(request.getQueueId()))
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.GATEWAY_TIMEOUT.value()))
                .andExpect(response -> {
                    String responseBody = response.getResponse().getContentAsString();
                    ErrorResponseBody errorResponseBody = objectMapper.readValue(responseBody, ErrorResponseBody.class);

                    ErrorResponseBody badGatewayResponseBody =
                            errorResponseEntities.get(TIME_OUT_ENTITY_NAME).getBody();

                    assertThat(errorResponseBody.getErrorStatusCode()).isEqualTo(badGatewayResponseBody.getErrorStatusCode());
                    assertThat(errorResponseBody.getErrorMessage()).isEqualTo(badGatewayResponseBody.getErrorMessage());
                });
    }
}
