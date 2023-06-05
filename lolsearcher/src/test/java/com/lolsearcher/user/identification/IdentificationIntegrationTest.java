package com.lolsearcher.user.identification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.config.EmbeddedRedisConfig;
import com.lolsearcher.user.ResponseSuccessDto;
import com.lolsearcher.user.Role;
import com.lolsearcher.user.UserRepository;
import com.lolsearcher.utils.RandomCodeUtils;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import static com.lolsearcher.config.ErrorResponseEntityConfig.*;
import static com.lolsearcher.user.identification.IdentificationConstant.IDENTIFICATION_NUMBER_SIZE;
import static com.lolsearcher.user.identification.IdentificationConstant.IDENTIFICATION_URI;
import static com.lolsearcher.user.identification.RedisIdentificationRepository.getKey;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@Import({EmbeddedRedisConfig.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
public class IdentificationIntegrationTest {

    private static final int verificationServerPort = 15553;
    private static final String CODE_PARAM_KEY = "code";

    private static MockWebServer mockWebServer;

    @Autowired private ObjectMapper objectMapper;
    @Autowired private Map<String, ResponseEntity<ErrorResponseBody>> errorResponseEntities;
    @Autowired private UserRepository userRepository;
    @Autowired private StringRedisTemplate redisTemplate;
    @Autowired private WebApplicationContext context;
    private MockMvc mockMvc;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start(verificationServerPort);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    public void setup(){

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();

        Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().flushAll();
    }

    @Transactional
    @DisplayName("create : 유효한 파라미터로 요청 시 캐시에 인증 번호가 저장된다.")
    @MethodSource("com.lolsearcher.user.identification.IdentificationSetup#validParam")
    @ParameterizedTest
    public void testCreateWithValidParam(Long userId, String requestBody) throws Exception {

        // given
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(HttpStatus.OK.value())
                        .addHeader("Content-Type", "application/json")
                        .setBody(objectMapper.writeValueAsString(new ResponseSuccessDto(true, "Identification code is sent by user")))
        );

        //before
        String IdentificationCode = redisTemplate.opsForValue().get(getKey(userId));
        assertThat(IdentificationCode).isNull();

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post(IDENTIFICATION_URI, userId)
                                .content(requestBody)
                                .contentType(APPLICATION_JSON)
                                .with(csrf())
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()));
        //after
        IdentificationCode = redisTemplate.opsForValue().get(getKey(userId));
        assertThat(IdentificationCode).isNotNull();
    }

    @Transactional
    @DisplayName("create : 잘못된 파라미터 요청 시 400 상태 코드를 리턴한다.")
    @MethodSource("com.lolsearcher.user.identification.IdentificationSetup#invalidParam")
    @ParameterizedTest
    public void testCreateWithInvalidParam(Long userId, String requestBody) throws Exception {

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post(IDENTIFICATION_URI, userId)
                                .content(requestBody)
                                .contentType(APPLICATION_JSON)
                                .with(csrf())
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

    @Transactional
    @DisplayName("create : 알림 서버에서 에러가 발생한 경우 502 상태코드를 리턴한다.")
    @MethodSource("com.lolsearcher.user.identification.IdentificationSetup#validParam")
    @ParameterizedTest
    public void testCreateWithExternalServerError(Long userId, String requestBody) throws Exception {

        // given
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .addHeader("Content-Type", "application/json")
        );

        //before
        String IdentificationCode = redisTemplate.opsForValue().get(getKey(userId));
        assertThat(IdentificationCode).isNull();

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post(IDENTIFICATION_URI, userId)
                                .content(requestBody)
                                .contentType(APPLICATION_JSON)
                                .with(csrf())
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_GATEWAY.value()))
                .andExpect(result -> {
                    ErrorResponseBody body = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponseBody.class);
                    ErrorResponseBody expected = errorResponseEntities.get(BAD_GATEWAY_ENTITY_NAME).getBody();

                    assertThat(expected).isNotNull();
                    assertThat(body.getErrorStatusCode()).isEqualTo(expected.getErrorStatusCode());
                    assertThat(body.getErrorMessage()).isEqualTo(expected.getErrorMessage());
                });

        //after
        IdentificationCode = redisTemplate.opsForValue().get(getKey(userId));
        assertThat(IdentificationCode).isNull();
    }

    @Transactional
    @WithAnonymousUser
    @DisplayName("identify : 유효한 파라미터로 요청 시 헤당 유저의 Role을 User로 변경한다.")
    @Test
    public void testIdentifyWithValidParam() throws Exception {

        // given
        Long userId = 2L; /* data.sql 참조 */
        String identificationCode = RandomCodeUtils.create(IDENTIFICATION_NUMBER_SIZE);
        redisTemplate.opsForValue().append(getKey(userId), identificationCode);

        assertThat(userRepository.findById(userId).isPresent()).isTrue();
        assertThat(userRepository.findById(userId).get().getRole()).isEqualTo(Role.TEMPORARY);
        assertThat(redisTemplate.opsForValue().get(getKey(userId))).isNotNull();

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.get(IDENTIFICATION_URI, userId)
                                .param(CODE_PARAM_KEY, identificationCode)
                                .contentType(APPLICATION_JSON)
                                .with(csrf())
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()));

        assertThat(userRepository.findById(userId).get().getRole()).isEqualTo(Role.USER);
        assertThat(redisTemplate.opsForValue().get(getKey(userId))).isNull();
    }

    @Transactional
    @DisplayName("identify : userId에 해당하는 인증 번호가 발급되지 않은 경우 400 상태코드를 리턴한다.")
    @Test
    public void testIdentifyWithNotExistingCode() throws Exception {

        // given
        Long userId = 2L; /* data.sql 참조 */
        String identificationCode = RandomCodeUtils.create(IDENTIFICATION_NUMBER_SIZE);

        assertThat(redisTemplate.opsForValue().get(getKey(userId))).isNull();

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.get(IDENTIFICATION_URI, userId)
                                .param(CODE_PARAM_KEY, identificationCode)
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

        assertThat(redisTemplate.opsForValue().get(getKey(userId))).isNull();
    }

    @Transactional
    @DisplayName("identify : 요청 인증번호가 실제 인증 번호와 다른 경우 400 상태코드를 리턴한다.")
    @Test
    public void testIdentifyWithBadRequestCode() throws Exception {

        // given
        Long userId = 2L; /* data.sql 참조 */
        String identificationCode = "123456";
        String differentCode = "654321";
        redisTemplate.opsForValue().append(getKey(userId), identificationCode);

        assertThat(redisTemplate.opsForValue().get(getKey(userId))).isNotNull();

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.get(IDENTIFICATION_URI, userId)
                                .param(CODE_PARAM_KEY, differentCode)
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

        assertThat(redisTemplate.opsForValue().get(getKey(userId))).isNotNull();
    }

    @Transactional
    @DisplayName("identify : 허용되지 않은 값으로 요청한 경우 400 상태코드를 리턴한다.")
    @Test
    public void testIdentifyWithInvalidParam() throws Exception {

        // given
        Long userId = 2L; /* data.sql 참조 */
        String identificationCode = RandomCodeUtils.create(IDENTIFICATION_NUMBER_SIZE);
        redisTemplate.opsForValue().append(getKey(userId), identificationCode);

        assertThat(redisTemplate.opsForValue().get(getKey(userId))).isNotNull();

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.get(IDENTIFICATION_URI, userId)
                                .param(CODE_PARAM_KEY, "INVALID_CODE")
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

        assertThat(redisTemplate.opsForValue().get(getKey(userId))).isNotNull();
    }

    @Transactional
    @DisplayName("identify : 요청 user id 가 실제 존재하지 않는 유저일 경우 500 상태 코드를 리턴한다.")
    @Test
    public void testIdentifyWithNotExistingUserId() throws Exception {

        // given
        Long userId = 3L; /* not existing userId */
        String identificationCode = RandomCodeUtils.create(IDENTIFICATION_NUMBER_SIZE);
        redisTemplate.opsForValue().append(getKey(userId), identificationCode);

        assertThat(redisTemplate.opsForValue().get(getKey(userId))).isNotNull();

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.get(IDENTIFICATION_URI, userId)
                                .param(CODE_PARAM_KEY, identificationCode)
                                .contentType(APPLICATION_JSON)
                                .with(csrf())
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(result -> {
                    ErrorResponseBody response = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponseBody.class);
                    ErrorResponseBody expected = errorResponseEntities.get(INTERNAL_SERVER_ERROR_ENTITY_NAME).getBody();

                    assertThat(expected).isNotNull();
                    assertThat(response.getErrorStatusCode()).isEqualTo(expected.getErrorStatusCode());
                    assertThat(response.getErrorMessage()).isEqualTo(expected.getErrorMessage());
                });

        assertThat(redisTemplate.opsForValue().get(getKey(userId))).isNull(); // exception handler 에서 invalid data 제거
    }

    @Transactional
    @DisplayName("identify : 요청 user의 ROLE 값이 TEMPORARY가 아닐 경우 500 상태코드를 리턴한다.")
    @Test
    public void testIdentifyWithNotInvalidUser() throws Exception {

        // given
        Long userId = 1L; /* ROLE != TEMPORARY userId */
        String identificationCode = RandomCodeUtils.create(IDENTIFICATION_NUMBER_SIZE);
        redisTemplate.opsForValue().append(getKey(userId), identificationCode);

        assertThat(redisTemplate.opsForValue().get(getKey(userId))).isNotNull();

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.get(IDENTIFICATION_URI, userId)
                                .param(CODE_PARAM_KEY, identificationCode)
                                .contentType(APPLICATION_JSON)
                                .with(csrf())
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(result -> {
                    ErrorResponseBody response = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponseBody.class);
                    ErrorResponseBody expected = errorResponseEntities.get(INTERNAL_SERVER_ERROR_ENTITY_NAME).getBody();

                    assertThat(expected).isNotNull();
                    assertThat(response.getErrorStatusCode()).isEqualTo(expected.getErrorStatusCode());
                    assertThat(response.getErrorMessage()).isEqualTo(expected.getErrorMessage());
                });

        assertThat(redisTemplate.opsForValue().get(getKey(userId))).isNull(); // exception handler 에서 invalid data 제거
    }
}
