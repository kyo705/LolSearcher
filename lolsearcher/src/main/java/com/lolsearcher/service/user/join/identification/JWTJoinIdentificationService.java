package com.lolsearcher.service.user.join.identification;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.exception.exception.join.InvalidTokenException;
import com.lolsearcher.exception.exception.join.RandomNumDifferenceException;
import com.lolsearcher.model.entity.user.LolSearcherUser;
import com.lolsearcher.model.request.user.JoinAuthentication;
import com.lolsearcher.model.request.user.JwtJoinAuthentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.lolsearcher.constant.LolSearcherConstants.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class JWTJoinIdentificationService implements JoinIdentificationService {

    @Value("${lolsearcher.jwt.secret}")
    private String JWT_SECRET_KEY = "secret";

    private final ObjectMapper objectMapper;

    @Override
    public LolSearcherUser authenticate(JoinAuthentication authentication) {

        if(!(authentication instanceof JwtJoinAuthentication)){
            log.error("authentication 는 JWTJoinAuthentication 객체가 아닙니다.");
            throw new ClassCastException();
        }
        String token =  (String) authentication.getUserInfo();

        //토큰 복호화 시도
        DecodedJWT jwt = JWT.require(Algorithm.HMAC256(JWT_SECRET_KEY)).build().verify(token);

        String subject = jwt.getSubject();
        if(!subject.equals(JOIN_IDENTIFICATION_SIGNATURE)){
            log.error("토큰이 회원 가입 인증용 토큰이 아닙니다.");
            throw new InvalidTokenException();
        }

        int realRandomNum = jwt.getClaim(RANDOM_NUMBER).asInt();
        int requestRandomNum = authentication.getRandomNum();

        if(realRandomNum != requestRandomNum){
            log.info("사용자 요청 인증번호가 실제 인증번호와 다릅니다.");
            throw new RandomNumDifferenceException();
        }
        String userInfoJson = jwt.getClaim(USER_INFO).asString();

        try {
            return objectMapper.readValue(userInfoJson, LolSearcherUser.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
