package com.lolsearcher.integration.controller.search.mostchamp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.constant.BeanNameConstants;
import com.lolsearcher.model.request.search.mostchamp.RequestMostChampDto;
import com.lolsearcher.model.response.error.ErrorResponseBody;
import com.lolsearcher.model.response.front.search.mostchamp.ResponseMostChampDto;
import com.lolsearcher.service.search.mostchamp.MostChampService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
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
import static org.mockito.BDDMockito.given;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class MostChampControllerTest {

    private static final String MOST_CHAMP_URI = "/summoner/most-champ";

    @Autowired private WebApplicationContext context;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private Map<String, ResponseEntity<ErrorResponseBody>> errorResponseEntities;
    @MockBean private MostChampService mostChampService;
    private MockMvc mockMvc;

    @BeforeEach
    public void setup(){

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @DisplayName("정상적인 요청이 들어올 경우 유저 모스트 챔피언 통계 데이터를 제공한다.")
    @Test
    public void getMostChampWithSuccess() throws Exception {

        //given
        RequestMostChampDto request = MostChampControllerTestSetup.getValidRequest();
        String requestBody = objectMapper.writeValueAsString(request);

        List<ResponseMostChampDto> result = MostChampControllerTestSetup.getMostChamps();
        given(mostChampService.getMostChamps(request)).willReturn(result);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .post(MOST_CHAMP_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(response -> {
                    String responseBody = response.getResponse().getContentAsString();
                    List<ResponseMostChampDto> responseMostChamps = objectMapper.readValue(responseBody, List.class);

                    assertThat(responseMostChamps.size()).isEqualTo(result.size());
                });
    }

    @DisplayName("유효하지 않는 파라미터 요청이 들어올 경우 400 에러를 리턴한다.")
    @ParameterizedTest
    @MethodSource("com.lolsearcher.integration.controller.search.mostchamp.MostChampControllerTestSetup#getInvalidRequest")
    public void getMostChampWithInvalidParameter(RequestMostChampDto request) throws Exception {

        //given
        String requestBody = objectMapper.writeValueAsString(request);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .post(MOST_CHAMP_URI)
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

    @DisplayName("DB 관련 예외가 발생할 경우 502 에러를 리턴한다.")
    @Test
    public void getMostChampWithJPAException() throws Exception {

        //given
        RequestMostChampDto request = MostChampControllerTestSetup.getValidRequest();
        String requestBody = objectMapper.writeValueAsString(request);

        given(mostChampService.getMostChamps(request)).willThrow(QueryTimeoutException.class);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .post(MOST_CHAMP_URI)
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
