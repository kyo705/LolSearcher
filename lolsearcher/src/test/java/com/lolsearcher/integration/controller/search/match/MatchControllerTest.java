package com.lolsearcher.integration.controller.search.match;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.constant.BeanNameConstants;
import com.lolsearcher.model.request.search.match.RequestMatchDto;
import com.lolsearcher.model.response.error.ErrorResponseBody;
import com.lolsearcher.model.response.front.search.match.MatchDto;
import com.lolsearcher.service.search.match.MatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
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
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class MatchControllerTest {

    private static final String MATCH_URI = "/summoner/match";

    @Autowired private ObjectMapper objectMapper;
    @Autowired private Map<String, ResponseEntity<ErrorResponseBody>> errorResponseEntities;
    @Autowired private WebApplicationContext context;
    @MockBean private MatchService matchService;
    private MockMvc mockMvc;

    @BeforeEach
    public void setup(){

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @DisplayName("정상적인 요청이 들어오면 유저의 match 데이터를 리턴한다.")
    @Test
    public void getMatchDtoWithSuccess() throws Exception {

        //given
        RequestMatchDto request = MatchControllerTestSetup.getValidRequest();
        String requestBody = objectMapper.writeValueAsString(request);

        List<MatchDto> result = MatchControllerTestSetup.getMatchDtoList(request);
        BDDMockito.given(matchService.getMatchesInDB(request)).willReturn(result);

        //when
        mockMvc.perform(MockMvcRequestBuilders
                        .post(MATCH_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(response -> {
                    String responseBody = response.getResponse().getContentAsString();
                    List<MatchDto> responseMatches = objectMapper.readValue(responseBody, List.class);

                    assertThat(responseMatches.size()).isEqualTo(result.size());
                });
    }

    @DisplayName("유효하지 않은 요청이 들어오면 400 에러를 리턴한다.")
    @ParameterizedTest
    @MethodSource("com.lolsearcher.integration.controller.search.match.MatchControllerTestSetup#getInvalidRequest")
    public void getMatchDtoWithInvalidParameter(RequestMatchDto request) throws Exception {

        //given
        String requestBody = objectMapper.writeValueAsString(request);

        //when
        mockMvc.perform(MockMvcRequestBuilders
                        .post(MATCH_URI)
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

    @DisplayName("DB 관련 에러가 발생하면 502 에러를 리턴한다.")
    @Test
    public void getMatchDtoWithJPAException() throws Exception {

        //given
        RequestMatchDto request = MatchControllerTestSetup.getValidRequest();
        String requestBody = objectMapper.writeValueAsString(request);

        BDDMockito.given(matchService.getMatchesInDB(request)).willThrow(QueryTimeoutException.class);

        //when
        mockMvc.perform(MockMvcRequestBuilders
                        .post(MATCH_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_GATEWAY.value()))
                .andExpect(response -> {
                    String responseBody = response.getResponse().getContentAsString();
                    ErrorResponseBody errorResponseBody = objectMapper.readValue(responseBody, ErrorResponseBody.class);

                    ErrorResponseBody badGatewayResponseBody =
                            errorResponseEntities.get(BeanNameConstants.BAD_GATEWAY_ENTITY_NAME).getBody();

                    assertThat(errorResponseBody.getErrorStatusCode()).isEqualTo(badGatewayResponseBody.getErrorStatusCode());
                    assertThat(errorResponseBody.getErrorMessage()).isEqualTo(badGatewayResponseBody.getErrorMessage());
                });
    }
}
