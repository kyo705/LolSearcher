package com.lolsearcher.unit.service.user.join;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.constant.LolSearcherConstants;
import com.lolsearcher.model.entity.user.LolSearcherUser;
import com.lolsearcher.model.request.user.JoinAuthentication;
import com.lolsearcher.model.request.user.JwtJoinAuthentication;

import java.util.Date;

import static com.lolsearcher.constant.LolSearcherConstants.JOIN_IDENTIFICATION_SIGNATURE;
import static com.lolsearcher.constant.LolSearcherConstants.JWT_EXPIRED_TIME;

public class JWTJoinIdentificationServiceTestSetup {

    public static ObjectMapper objectMapper = new ObjectMapper();

    protected static JoinAuthentication getInvalidAuthentication() {

        return new JoinAuthentication() {
            @Override
            public Object getUserInfo() {
                return "커스텀 유저";
            }

            @Override
            public int getRandomNum() {
                return 0;
            }
        };
    }

    public static JoinAuthentication getAuthenticationWithInvalidToken(String secret) throws JsonProcessingException {

        LolSearcherUser user = LolSearcherUser.builder()
                .email("email@gmail.com")
                .password("password")
                .username("닉네임")
                .role("역할")
                .build();

        String userJson = objectMapper.writeValueAsString(user);
        int randomNum = 1111;

        String token = JWT.create().withSubject("인증용 토큰이 아님")
                .withClaim(LolSearcherConstants.USER_INFO, userJson)
                .withClaim(LolSearcherConstants.RANDOM_NUMBER, randomNum)
                .sign(Algorithm.HMAC256(secret));

        return new JwtJoinAuthentication(token, randomNum);
    }

    public static JoinAuthentication getAuthenticationWithDifferentRandomNum(String secret) throws JsonProcessingException {

        LolSearcherUser user = LolSearcherUser.builder()
                .email("email@gmail.com")
                .password("password")
                .username("닉네임")
                .role("역할")
                .build();
        String userJson = objectMapper.writeValueAsString(user);
        int randomNum = 1111;

        String token = JWT.create().withSubject(JOIN_IDENTIFICATION_SIGNATURE)
                .withClaim(LolSearcherConstants.USER_INFO, userJson)
                .withClaim(LolSearcherConstants.RANDOM_NUMBER, randomNum)
                .sign(Algorithm.HMAC256(secret));

        return new JwtJoinAuthentication(token, 1234);
    }

    public static JoinAuthentication getValidAuthentication(String secret) throws JsonProcessingException {

        LolSearcherUser user = LolSearcherUser.builder()
                .email("email@gmail.com")
                .password("password")
                .username("닉네임")
                .role("역할")
                .build();

        String userJson = objectMapper.writeValueAsString(user);
        int randomNum = 1111;

        String token = JWT.create()
                .withSubject(JOIN_IDENTIFICATION_SIGNATURE)
                .withExpiresAt(new Date(System.currentTimeMillis() + JWT_EXPIRED_TIME))
                .withClaim(LolSearcherConstants.USER_INFO, userJson)
                .withClaim(LolSearcherConstants.RANDOM_NUMBER, randomNum)
                .sign(Algorithm.HMAC256(secret));

        return new JwtJoinAuthentication(token, randomNum);
    }
}
