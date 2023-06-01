package com.lolsearcher.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.errors.ErrorResponseBody;
import com.lolsearcher.user.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

import java.util.Map;

import static com.lolsearcher.ban.BanConstant.LOGIN_BAN;
import static com.lolsearcher.ban.BanConstant.LOGIN_BAN_COUNT;
import static com.lolsearcher.errors.ErrorConstant.FORBIDDEN_ENTITY_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
public class LoginIntegrationTest {

    private static final String LOGIN_URI = "/login";

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private Map<String, ResponseEntity<ErrorResponseBody>> errorResponseEntities;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private CacheManager cacheManager;
    private MockMvc mockMvc;

    @BeforeEach
    public void setup(){

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @DisplayName("올바른 유저 로그인 정보를 전달하면 200 상태코드를 리턴한다.")
    @MethodSource("com.lolsearcher.login.LoginSetup#getValidUsernamePassword")
    @ParameterizedTest
    public void testLoginWithValidRequest(LoginRequest request) throws Exception {

        //when & then
        mockMvc.perform(
                MockMvcRequestBuilders.post(LOGIN_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(result -> {
                    UserDto userDto = objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);
                    assertThat(userDto.getEmail()).isEqualTo(request.getEmail());
                });
    }

    @DisplayName("잘못된 유저 로그인 정보를 전달하면 400 상태코드를 리턴한다.")
    @MethodSource("com.lolsearcher.login.LoginSetup#getInvalidUsernamePassword")
    @ParameterizedTest
    public void testLoginWithInvalidRequest(LoginRequest request) throws Exception {

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post(LOGIN_URI)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    ErrorResponseBody body = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponseBody.class);
                    assertThat(body.getErrorStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                });
    }

    @DisplayName("로그인 요청 횟수 초과시 403 상태코드를 리턴한다.")
    @Test
    public void testLoginWithTooManyRequest() throws Exception {

        //given
        LoginRequest request = LoginRequest.builder()
                .email("user@naver.com")
                .password("123456789")
                .build();

        String ip = "192.168.0.2";
        cacheManager.getCache(LOGIN_BAN).put(ip, LOGIN_BAN_COUNT);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post(LOGIN_URI)
                                .with(req -> {
                                    req.setRemoteAddr(ip);
                                    return req;
                                })
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.FORBIDDEN.value()))
                .andExpect(result -> {
                    ErrorResponseBody expected = errorResponseEntities.get(FORBIDDEN_ENTITY_NAME).getBody();

                    ErrorResponseBody body = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponseBody.class);
                    assertThat(body.getErrorStatusCode()).isEqualTo(expected.getErrorStatusCode());
                    assertThat(body.getErrorMessage()).isEqualTo(expected.getErrorMessage());
                });
    }


}
