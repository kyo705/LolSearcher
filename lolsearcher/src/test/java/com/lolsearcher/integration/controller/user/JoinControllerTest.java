package com.lolsearcher.integration.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.exception.exception.user.join.ExistedUserException;
import com.lolsearcher.model.request.user.join.RequestUserJoinDto;
import com.lolsearcher.model.response.error.ErrorResponseBody;
import com.lolsearcher.service.user.join.JoinService;
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
import org.springframework.http.HttpHeaders;
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

import java.util.Map;

import static com.lolsearcher.constant.BeanNameConstants.BAD_REQUEST_ENTITY_NAME;
import static com.lolsearcher.constant.BeanNameConstants.CONFLICT_ENTITY_NAME;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class JoinControllerTest {

    private static final String JOIN_URI = "/join";

    @Autowired private ObjectMapper objectMapper;
    @Autowired private WebApplicationContext context;
    @Autowired private Map<String, ResponseEntity<ErrorResponseBody>> errorResponseEntities;
    @MockBean private JoinService joinService;
    private MockMvc mockMvc;

    @BeforeEach
    public void setup(){

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @DisplayName("적절한 파라미터로 요청한 경우 회원 가입이 성공한다.")
    @Test
    public void processJoinWithSuccess() throws Exception {

        //given
        RequestUserJoinDto request = JoinControllerTestSetup.getValidJoinRequest();
        String requestBody = objectMapper.writeValueAsString(request);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .post(JOIN_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.header().exists(HttpHeaders.AUTHORIZATION));

    }

    @DisplayName("잘못된 파라미터로 요청한 경우 400에러를 리턴한다.")
    @ParameterizedTest
    @MethodSource("com.lolsearcher.integration.controller.user.JoinControllerTestSetup#getInvalidJoinRequest")
    public void processJoinWithInvalidParameter(RequestUserJoinDto request) throws Exception {

        //given
        String requestBody = objectMapper.writeValueAsString(request);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .post(JOIN_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(response -> {
                    String responseBody = response.getResponse().getContentAsString();
                    ErrorResponseBody errorResponseBody = objectMapper.readValue(responseBody, ErrorResponseBody.class);

                    ErrorResponseBody badRequestResponseBody = errorResponseEntities.get(BAD_REQUEST_ENTITY_NAME).getBody();

                    assertThat(errorResponseBody.getErrorStatusCode()).isEqualTo(badRequestResponseBody.getErrorStatusCode());
                    assertThat(errorResponseBody.getErrorMessage()).isEqualTo(badRequestResponseBody.getErrorMessage());
                });
    }

    @DisplayName("이미 가입되어있는 회원인 경우 409 에러를 리턴한다.")
    @Test
    public void processJoinWithExistedUserException() throws Exception {

        //given
        RequestUserJoinDto request = JoinControllerTestSetup.getValidJoinRequest();
        String requestBody = objectMapper.writeValueAsString(request);

        BDDMockito.given(joinService.handleJoinProcedure(request)).willThrow(new ExistedUserException(request.getEmail()));

        //when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .post(JOIN_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.CONFLICT.value()))
                .andExpect(response -> {
                    String responseBody = response.getResponse().getContentAsString();
                    ErrorResponseBody errorResponseBody = objectMapper.readValue(responseBody, ErrorResponseBody.class);

                    ErrorResponseBody conflictResponseBody = errorResponseEntities.get(CONFLICT_ENTITY_NAME).getBody();

                    assertThat(errorResponseBody.getErrorStatusCode()).isEqualTo(conflictResponseBody.getErrorStatusCode());
                    assertThat(errorResponseBody.getErrorMessage()).isEqualTo(conflictResponseBody.getErrorMessage());
                });

    }

}
