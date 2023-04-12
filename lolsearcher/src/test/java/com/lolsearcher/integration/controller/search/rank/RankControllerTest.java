package com.lolsearcher.integration.controller.search.rank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.constant.BeanNameConstants;
import com.lolsearcher.constant.LolSearcherConstants;
import com.lolsearcher.exception.exception.search.rank.IncorrectSummonerRankSizeException;
import com.lolsearcher.exception.exception.search.rank.NonUniqueRankTypeException;
import com.lolsearcher.model.request.search.rank.RequestRankDto;
import com.lolsearcher.model.response.error.ErrorResponseBody;
import com.lolsearcher.model.response.front.search.rank.RankDto;
import com.lolsearcher.service.search.rank.RankService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

import javax.persistence.QueryTimeoutException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class RankControllerTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String RANK_URI = "/summoner/rank";

    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private Map<String, ResponseEntity<ErrorResponseBody>> errorResponseEntities;
    @MockBean
    private RankService rankService;

    @BeforeEach
    public void beforeEach() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
                .build();
    }

    @DisplayName("정상적인 요청이 들어오면 유저의 rank 데이터를 리턴한다.")
    @Test
    public void getRankDtoWithSuccess() throws Exception {

        //given
        RequestRankDto request = new RequestRankDto("summonerId1");
        String requestBody = objectMapper.writeValueAsString(request);

        Map<String, RankDto> result = RankControllerTestSetup.getRankDto(request.getSummonerId());
        BDDMockito.given(rankService.getOldRanks(request)).willReturn(result);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.post(RANK_URI)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(response -> {
                    Map<String, RankDto> responseBody = objectMapper.readValue(response.getResponse().getContentAsString(),
                            Map.class);

                    assertThat(responseBody.size()).isLessThanOrEqualTo(LolSearcherConstants.THE_NUMBER_OF_RANK_TYPE);
                });
    }

    @DisplayName("DB의 유저 rank 데이터가 정상적이지 않다면 예외가 발생한다.(1)")
    @Test
    public void getRankDtoWithIncorrectSummonerRankSizeException() throws Exception {

        //given
        RequestRankDto request = new RequestRankDto("summonerId1");
        String requestBody = objectMapper.writeValueAsString(request);

        BDDMockito.given(rankService.getOldRanks(request)).willThrow(new IncorrectSummonerRankSizeException(3));

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.post(RANK_URI)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(response -> {
                    ErrorResponseBody errorResponse = objectMapper.readValue(response.getResponse().getContentAsString(),
                            ErrorResponseBody.class);

                    ErrorResponseBody internalServerErrorResponseBody =
                            errorResponseEntities.get(BeanNameConstants.INTERNAL_SERVER_ERROR_ENTITY_NAME).getBody();

                    assertThat(errorResponse.getErrorStatusCode()).isEqualTo(internalServerErrorResponseBody.getErrorStatusCode());
                    assertThat(errorResponse.getErrorMessage()).isEqualTo(internalServerErrorResponseBody.getErrorMessage());
                });
    }

    @DisplayName("DB의 유저 rank 데이터가 정상적이지 않다면 예외가 발생한다.(2)")
    @Test
    public void getRankDtoWithNonUniqueRankTypeException() throws Exception {

        //given
        RequestRankDto request = new RequestRankDto("summonerId1");
        String requestBody = objectMapper.writeValueAsString(request);

        BDDMockito.given(rankService.getOldRanks(request)).willThrow(new NonUniqueRankTypeException(LolSearcherConstants.FLEX_RANK));

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.post(RANK_URI)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(response -> {
                    ErrorResponseBody errorResponseBody = objectMapper.readValue(response.getResponse().getContentAsString(),
                            ErrorResponseBody.class);

                    ErrorResponseBody internalServerErrorResponseBody =
                            errorResponseEntities.get(BeanNameConstants.INTERNAL_SERVER_ERROR_ENTITY_NAME).getBody();

                    assertThat(errorResponseBody.getErrorStatusCode()).isEqualTo(internalServerErrorResponseBody.getErrorStatusCode());
                    assertThat(errorResponseBody.getErrorMessage()).isEqualTo(internalServerErrorResponseBody.getErrorMessage());
                });
    }

    @DisplayName("DB 관련 예외가 발생할 경우 502 에러를 리턴한다.")
    @Test
    public void getRankDtoWithJPAException() throws Exception {

        //given
        RequestRankDto request = new RequestRankDto("summonerId1");
        String requestBody = objectMapper.writeValueAsString(request);

        BDDMockito.given(rankService.getOldRanks(request)).willThrow(new QueryTimeoutException());

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.post(RANK_URI)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_GATEWAY.value()))
                .andExpect(response -> {
                    ErrorResponseBody errorResponseBody = objectMapper.readValue(response.getResponse().getContentAsString(),
                            ErrorResponseBody.class);

                    ErrorResponseBody badGatewayResponseBody =
                            errorResponseEntities.get(BeanNameConstants.BAD_GATEWAY_ENTITY_NAME).getBody();

                    assertThat(errorResponseBody.getErrorStatusCode()).isEqualTo(badGatewayResponseBody.getErrorStatusCode());
                    assertThat(errorResponseBody.getErrorMessage()).isEqualTo(badGatewayResponseBody.getErrorMessage());
                });
    }

    @DisplayName("요청 파라미터가 유효하지 않을 경우 400 에러가 발생한다.")
    @NullAndEmptySource
    @ParameterizedTest
    public void getRankDtoWithInvalidParameter(String summonerId) throws Exception {

        //given
        RequestRankDto request = new RequestRankDto(summonerId);
        String requestBody = objectMapper.writeValueAsString(request);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.post(RANK_URI)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(response -> {
                    ErrorResponseBody errorResponseBody = objectMapper.readValue(response.getResponse().getContentAsString(),
                            ErrorResponseBody.class);

                    ErrorResponseBody badRequestResponseBody =
                            errorResponseEntities.get(BeanNameConstants.BAD_REQUEST_ENTITY_NAME).getBody();

                    assertThat(errorResponseBody.getErrorStatusCode()).isEqualTo(badRequestResponseBody.getErrorStatusCode());
                    assertThat(errorResponseBody.getErrorMessage()).isEqualTo(badRequestResponseBody.getErrorMessage());
                });
    }
}
