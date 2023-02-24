package com.lolsearcher.unit.service.user.join;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.exception.exception.join.InvalidTokenException;
import com.lolsearcher.exception.exception.join.RandomNumDifferenceException;
import com.lolsearcher.model.entity.user.LolSearcherUser;
import com.lolsearcher.model.request.user.JoinAuthentication;
import com.lolsearcher.service.user.join.identification.JWTJoinIdentificationService;
import com.lolsearcher.service.user.join.identification.JoinIdentificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.lolsearcher.constant.LolSearcherConstants.USER_INFO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class JWTJoinIdentificationServiceUnitTest {

    private String SECRET;
    private ObjectMapper objectMapper;
    private JoinIdentificationService joinIdentificationService;

    @BeforeEach
    void setup(){
        SECRET = "secret";
        objectMapper = new ObjectMapper();
        joinIdentificationService = new JWTJoinIdentificationService(objectMapper);
    }

    @DisplayName("파라미터 객체가 JwtJoinAuthentication 객체가 아닌 경우 예외가 발생한다")
    @Test
    public void authenticateWithInvalidParam(){

        //given
        JoinAuthentication authentication = JWTJoinIdentificationServiceTestSetup.getInvalidAuthentication();

        //when & then
        assertThrows(ClassCastException.class, ()->joinIdentificationService.authenticate(authentication));
    }

    @DisplayName("파라미터 객체에 회원가입 인증용 토큰이 없는 경우 예외가 발생한다")
    @Test
    public void authenticateWithInvalidToken() throws JsonProcessingException {

        //given
        JoinAuthentication authentication = JWTJoinIdentificationServiceTestSetup
                .getAuthenticationWithInvalidToken(SECRET);

        //when & then
        assertThrows(InvalidTokenException.class, ()->joinIdentificationService.authenticate(authentication));
    }

    @DisplayName("회원가입 인증번호가 틀린 경우 예외가 발생한다")
    @Test
    public void authenticateWithDifferentRandomNum() throws JsonProcessingException {

        //given
        JoinAuthentication authentication = JWTJoinIdentificationServiceTestSetup
                .getAuthenticationWithDifferentRandomNum(SECRET);

        //when & then
        assertThrows(RandomNumDifferenceException.class, ()->joinIdentificationService.authenticate(authentication));
    }


    @DisplayName("회원가입 인증이 성공한 경우 유저 데이터를 리턴한다")
    @Test
    public void authenticateWithSuccess() throws JsonProcessingException {

        //given
        JoinAuthentication authentication = JWTJoinIdentificationServiceTestSetup.getValidAuthentication(SECRET);

        //when
        LolSearcherUser user = joinIdentificationService.authenticate(authentication);

        //then
        String token = (String) authentication.getUserInfo();
        String requestUserJson = JWT.require(Algorithm.HMAC256(SECRET)).build().verify(token).getClaim(USER_INFO).asString();
        LolSearcherUser requestUser = objectMapper.readValue(requestUserJson, LolSearcherUser.class);

        assertThat(user.getEmail()).isEqualTo(requestUser.getEmail());
        assertThat(user.getPassword()).isEqualTo(requestUser.getPassword());
        assertThat(user.getUsername()).isEqualTo(requestUser.getUsername());
        assertThat(user.getRole()).isEqualTo(requestUser.getRole());
    }
}
