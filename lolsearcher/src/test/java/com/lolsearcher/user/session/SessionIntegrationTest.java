package com.lolsearcher.user.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.errors.ErrorResponseBody;
import com.lolsearcher.user.ResponseSuccessDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.List;
import java.util.Map;

import static com.lolsearcher.errors.ErrorConstant.BAD_REQUEST_ENTITY_NAME;
import static com.lolsearcher.user.session.SessionConstant.USER_SESSION_URI;
import static com.lolsearcher.user.session.SessionSetup.setupSessions;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
public class SessionIntegrationTest {

    private static final String username = "user@naver.com";

    @Autowired private ObjectMapper objectMapper;
    @Autowired private Map<String, ResponseEntity<ErrorResponseBody>> errorResponseEntities;
    @Autowired private RedisConnectionFactory redisConnectionFactory;
    @Autowired private WebApplicationContext context;
    private MockMvc mockMvc;
    private RedisTemplate<String, Object> redisTemplate;

    @BeforeEach
    public void setup(){

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();

        redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.afterPropertiesSet();
    }

    @WithUserDetails(username)
    @DisplayName("find All : 정상적인 요청시 200 상태코드를 리턴한다.")
    @MethodSource("com.lolsearcher.user.session.SessionSetup#sessionIds")
    @ParameterizedTest
    public void testFindAllWithValidParam(List<String> sessionIds) throws Exception {

        //given
        Long userId = 1L;
        setupSessions(sessionIds, username, redisTemplate);

        //when & then
        mockMvc.perform(
                MockMvcRequestBuilders.get(USER_SESSION_URI, userId)
                        .contentType(APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(result -> {
                    List response = objectMapper.readValue(result.getResponse().getContentAsString(), List.class);

                    assertThat(response.size()).isEqualTo(sessionIds.size());
                    for(Object obj : response) {
                        String sessionId = objectMapper.convertValue(obj, String.class);
                        assertThat(sessionIds).contains(sessionId);
                    }
                });
    }

    @WithUserDetails(username)
    @DisplayName("find All : 세션 정보와 pathvariable 값이 다를 경우 400 상태코드를 리턴한다.")
    @MethodSource("com.lolsearcher.user.session.SessionSetup#sessionIds")
    @ParameterizedTest
    public void testFindAllWithInvalidPathVariable(List<String> sessionIds) throws Exception {

        //given
        Long userId = 2L;  // Session Data의 userId 는 1L 임
        setupSessions(sessionIds, username, redisTemplate);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.get(USER_SESSION_URI, userId)
                                .contentType(APPLICATION_JSON)
                                .with(csrf())
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    ErrorResponseBody response = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponseBody.class);
                    ErrorResponseBody expected = errorResponseEntities.get(BAD_REQUEST_ENTITY_NAME).getBody();

                    assertThat(expected).isNotNull();
                    assertThat(response.getErrorStatusCode()).isEqualTo(expected.getErrorStatusCode());
                    assertThat(response.getErrorMessage()).isEqualTo(expected.getErrorMessage());
                });
    }

    @WithUserDetails(username)
    @DisplayName("delete : 정상적인 요청시 success : true 를 리턴한다.")
    @MethodSource("com.lolsearcher.user.session.SessionSetup#sessionIds")
    @ParameterizedTest
    public void testDeleteSessionWithValidParam(List<String> sessionIds) throws Exception {

        //given
        Long userId = 1L;
        setupSessions(sessionIds, username, redisTemplate);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.delete(USER_SESSION_URI, userId)
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(new SessionDeleteRequest(sessionIds.get(1))))
                                .with(csrf())
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(result -> {
                    ResponseSuccessDto response = objectMapper.readValue(result.getResponse().getContentAsString(), ResponseSuccessDto.class);

                    assertThat(response.getSuccess()).isTrue();
                });
    }

    @WithUserDetails(username)
    @DisplayName("delete : 존재하지 않는 세션 id를 파라미터로 전달 시 success : false를 리턴한다.")
    @MethodSource("com.lolsearcher.user.session.SessionSetup#sessionIds")
    @ParameterizedTest
    public void testDeleteSessionWithInvalidParam(List<String> sessionIds) throws Exception {

        //given
        Long userId = 1L;
        setupSessions(sessionIds, username, redisTemplate);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.delete(USER_SESSION_URI, userId)
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(new SessionDeleteRequest("non-session-id")))
                                .with(csrf())
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(result -> {
                    ResponseSuccessDto response = objectMapper.readValue(result.getResponse().getContentAsString(), ResponseSuccessDto.class);

                    assertThat(response.getSuccess()).isFalse();
                });
    }

    @WithUserDetails(username)
    @DisplayName("delete : 세션 정보와 pathvariable 값이 다를 경우 400 상태코드를 리턴한다.")
    @MethodSource("com.lolsearcher.user.session.SessionSetup#sessionIds")
    @ParameterizedTest
    public void testDeleteSessionWithInvalidPathVariable(List<String> sessionIds) throws Exception {

        //given
        Long userId = 2L;  // Session Data의 userId 는 1L 임
        setupSessions(sessionIds, username, redisTemplate);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.delete(USER_SESSION_URI, userId)
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(new SessionDeleteRequest(sessionIds.get(1))))
                                .with(csrf())
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    ErrorResponseBody response = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponseBody.class);
                    ErrorResponseBody expected = errorResponseEntities.get(BAD_REQUEST_ENTITY_NAME).getBody();

                    assertThat(expected).isNotNull();
                    assertThat(response.getErrorStatusCode()).isEqualTo(expected.getErrorStatusCode());
                    assertThat(response.getErrorMessage()).isEqualTo(expected.getErrorMessage());
                });
    }
}
