package com.lolsearcher.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.config.EmbeddedRedisConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.Map;

import static com.lolsearcher.config.ErrorResponseEntityConfig.*;
import static com.lolsearcher.user.Role.USER;
import static com.lolsearcher.user.UserConstant.USER_URI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@Import({EmbeddedRedisConfig.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
public class UserIntegrationTest {

    private static final String USER_ACCESS_URI = "/user/{id}";
    private static final String EMAIL_PARAM_NAME = "email";

    @Autowired private ObjectMapper objectMapper;
    @Autowired private Map<String, ResponseEntity<ErrorResponseBody>> errorResponseEntities;
    @Autowired private WebApplicationContext context;
    @Autowired private UserService userService;
    private MockMvc mockMvc;

    @BeforeEach
    public void setup(){

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @DisplayName("find : 유효한 파라미터로 요청시 200 상태코드를 리턴한다")
    @Test
    public void testFindByEmailWithValidParam() throws Exception {

        //given
        String email = "user@naver.com";

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.get(USER_URI)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param(EMAIL_PARAM_NAME, email)
                                .with(csrf())
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(result -> {
                    UserFindResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), UserFindResponse.class);
                    assertThat(response.isExisted()).isTrue();
                });
    }

    @DisplayName("find : 존재하지 않는 email 요청시 200 상태코드와 false 응답을 리턴한다")
    @Test
    public void testFindByEmailWithNotExistEmail() throws Exception {

        //given
        String email = "notexist@naver.com";

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.get(USER_URI)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param(EMAIL_PARAM_NAME, email)
                                .with(csrf())
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(result -> {
                    UserFindResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), UserFindResponse.class);
                    assertThat(response.isExisted()).isFalse();
                });
    }

    @DisplayName("find : 잘못된 파라미터로 요청시 400 상태코드를 리턴한다")
    @ValueSource(strings = {"user@naver", "user.@naver.com", "u$ser@naver.com"})
    @ParameterizedTest
    public void testFindByEmailWithInvalidParam(String email) throws Exception {


        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.get(USER_URI)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param(EMAIL_PARAM_NAME, email)
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

    @DisplayName("create : 유효한 파라미터로 요청시 200 상태코드를 리턴한다")
    @MethodSource("com.lolsearcher.user.UserSetup#validUserCreatingParam")
    @ParameterizedTest
    @Transactional
    public void testCreatingUserWithValidParam(UserCreateRequest request) throws Exception {


        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post(USER_URI)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .with(csrf())
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(result -> {
                    UserDto response = objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);

                    assertThat(response.getEmail()).isEqualTo(request.getEmail());
                    assertThat(response.getName()).isEqualTo(request.getUsername());
                    assertThat(response.getRole()).isEqualTo(Role.TEMPORARY);
                    assertThat(response.getLoginSecurity()).isEqualTo(LoginSecurityState.NONE);
                });
    }

    @DisplayName("create : 잘못된 파라미터로 요청시 400 상태코드를 리턴한다")
    @MethodSource("com.lolsearcher.user.UserSetup#invalidUserCreatingParam")
    @ParameterizedTest
    @Transactional
    public void testCreatingUserWithInvalidParam(UserCreateRequest request) throws Exception {


        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post(USER_URI)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
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

    @DisplayName("create : 이미 존재하는 유저 메일로 회원가입시 409 상태코드를 리턴한다")
    @MethodSource("com.lolsearcher.user.UserSetup#existingUserCreatingParam")
    @ParameterizedTest
    @Transactional
    public void testCreatingUserWithExistingUser(UserCreateRequest request) throws Exception {

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post(USER_URI)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .with(csrf())
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.CONFLICT.value()))
                .andExpect(result -> {
                    ErrorResponseBody response = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponseBody.class);
                    ErrorResponseBody expected = errorResponseEntities.get(CONFLICT_ENTITY_NAME).getBody();

                    assertThat(expected).isNotNull();
                    assertThat(response.getErrorStatusCode()).isEqualTo(expected.getErrorStatusCode());
                    assertThat(response.getErrorMessage()).isEqualTo(expected.getErrorMessage());
                });
    }


    @WithAnonymousUser
    @DisplayName("update : 접근 가능하지 않는 세션의 경우 403 상태코드를 리턴한다 (1)")
    @Test
    @Transactional
    public void testUpdatingUserWithNoSession1() throws Exception {

        //given
        Long id = 2L;
        UserUpdateRequest request = UserUpdateRequest.builder().role(USER).build();

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.patch(USER_ACCESS_URI, id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .with(csrf())
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.FORBIDDEN.value()))
                .andExpect(result -> {
                    ErrorResponseBody response = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponseBody.class);
                    ErrorResponseBody expected = errorResponseEntities.get(FORBIDDEN_ENTITY_NAME).getBody();

                    assertThat(expected).isNotNull();
                    assertThat(response.getErrorStatusCode()).isEqualTo(expected.getErrorStatusCode());
                    assertThat(response.getErrorMessage()).isEqualTo(expected.getErrorMessage());
                });
    }

    @WithUserDetails("temporary@naver.com")
    @DisplayName("update : 접근 가능하지 않는 세션의 경우 403 상태코드를 리턴한다 (2)")
    @Test
    @Transactional
    public void testUpdatingUserWithNoSession2() throws Exception {

        //given
        Long id = 2L;
        UserUpdateRequest request = UserUpdateRequest.builder().role(USER).build();

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.patch(USER_ACCESS_URI, id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .with(csrf())
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.FORBIDDEN.value()))
                .andExpect(result -> {
                    ErrorResponseBody response = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponseBody.class);
                    ErrorResponseBody expected = errorResponseEntities.get(FORBIDDEN_ENTITY_NAME).getBody();

                    assertThat(expected).isNotNull();
                    assertThat(response.getErrorStatusCode()).isEqualTo(expected.getErrorStatusCode());
                    assertThat(response.getErrorMessage()).isEqualTo(expected.getErrorMessage());
                });
    }

    @WithUserDetails("user@naver.com")
    @DisplayName("update : 정상적인 요청은 200 응답코드를 리턴한다")
    @MethodSource("com.lolsearcher.user.UserSetup#validUpdatingParam")
    @ParameterizedTest
    @Transactional
    public void testUpdatingUserWithValidParam(UserUpdateRequest request) throws Exception {

        //given
        Long id = 1L;
        UserDto before = userService.findById(id);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.patch(USER_ACCESS_URI, id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .with(csrf())
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(result -> {
                    UserDto after = userService.findById(id);

                    if(request.getEmail().isPresent()) {
                        assertThat(after.getEmail()).isEqualTo(request.getEmail().get());
                    }else{
                        assertThat(after.getEmail()).isEqualTo(before.getEmail());
                    }
                    if(request.getName().isPresent()) {
                        assertThat(after.getName()).isEqualTo(request.getName().get());
                    }else{
                        assertThat(after.getName()).isEqualTo(before.getName());
                    }
                    if(request.getRole().isPresent()) {
                        assertThat(after.getRole()).isEqualTo(request.getRole().get());
                    }else{
                        assertThat(after.getRole()).isEqualTo(before.getRole());
                    }
                    if(request.getLoginSecurity().isPresent()) {
                        assertThat(after.getLoginSecurity()).isEqualTo(request.getLoginSecurity().get());
                    }else{
                        assertThat(after.getLoginSecurity()).isEqualTo(before.getLoginSecurity());
                    }
                });
    }



    @WithUserDetails("user@naver.com")
    @DisplayName("update : 잘못된 파라미터 요청은 400 응답코드를 리턴한다")
    @MethodSource("com.lolsearcher.user.UserSetup#invalidUpdatingParam")
    @ParameterizedTest
    @Transactional
    public void testUpdatingUserWithInvalidParam(Map<String, String> body) throws Exception {

        //given
        Long id = 1L;

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.patch(USER_ACCESS_URI, id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(body))
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

    @WithUserDetails("user@naver.com")
    @DisplayName("update : 요청 id 값과 세션 id 값이 불일치할 경우 400 응답코드를 리턴한다")
    @Test
    @Transactional
    public void testUpdatingUserWithInvalidId() throws Exception {

        //given
        Long id = 2L;
        UserUpdateRequest request = UserUpdateRequest.builder().name("변경할 닉네임").build();

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.patch(USER_ACCESS_URI, id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
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

    @WithUserDetails("user@naver.com")
    @DisplayName("delete : 유효한 파라미터 요청은 200 응답코드를 리턴한다")
    @Test
    @Transactional
    public void testDeletingUserWithValidParam() throws Exception {

        //given
        Long id = 1L;

        UserDto before = userService.findById(id);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.delete(USER_ACCESS_URI, id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(result -> {
                    UserDto after = userService.findById(id);

                    assertThat(before).isNotNull();
                    assertThat(after).isNull();
                });
    }

    @WithUserDetails("user@naver.com")
    @DisplayName("delete : 요청 id 값과 세션 id 값이 불일치할 경우 400 응답코드를 리턴한다")
    @Test
    @Transactional
    public void testDeletingUserWithInvalidId() throws Exception {

        //given
        Long id = 2L;

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.delete(USER_ACCESS_URI, id)
                                .contentType(MediaType.APPLICATION_JSON)
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
