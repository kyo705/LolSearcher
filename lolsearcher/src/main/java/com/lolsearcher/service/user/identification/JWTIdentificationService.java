package com.lolsearcher.service.user.identification;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.exception.exception.user.identification.JWTIdentificationException;
import com.lolsearcher.model.request.user.identification.IdentificationToken;
import com.lolsearcher.service.notification.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

import static com.lolsearcher.constant.LolSearcherConstants.*;
import static com.lolsearcher.constant.RedisCacheNameConstants.SUCCEEDED_IDENTIFICATION_TOKEN;

@Slf4j
@Service
public class JWTIdentificationService extends AbstractIdentificationService {

    private final ObjectMapper objectMapper;
    private final CacheManager cacheManager;

    @Value("${lolsearcher.jwt.secret}")
    private String JWT_SECRET_KEY;

    public JWTIdentificationService(NotificationService notificationService, ObjectMapper objectMapper, CacheManager cacheManager) {
        super(notificationService);
        this.objectMapper = objectMapper;
        this.cacheManager = cacheManager;
    }

    @Override
    protected Object saveIdentificationTemporarily(Object userInfo, int certificationNumber) {

        try {
            String userJson = objectMapper.writeValueAsString(userInfo);

            return JWT.create()
                    .withSubject(JWT_IDENTIFICATION_SUBJECT)
                    .withExpiresAt(new Date(System.currentTimeMillis() + JWT_EXPIRED_TIME))
                    .withClaim(USER_INFO, userJson)
                    .withClaim(CERTIFICATION_NUMBER, certificationNumber)
                    .sign(Algorithm.HMAC256(JWT_SECRET_KEY));

        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public Authentication authenticate(Authentication authentication) {

        //타입 검사
        if(!(authentication instanceof IdentificationToken)){
            throw new JWTIdentificationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "파라미터 타입이 JWTIdentificationToken이 아닙니다.");
        }
        String jwt = (String) authentication.getPrincipal();
        Integer certificationNum = ((IdentificationToken) authentication).getDetails();
        Integer requestCertificationNum = ((IdentificationToken) authentication).getCredentials();

        try{
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(JWT_SECRET_KEY)).build().verify(jwt);

            if(!decodedJWT.getSubject().equals(JWT_IDENTIFICATION_SUBJECT) ||
                    !Objects.equals(decodedJWT.getClaim(CERTIFICATION_NUMBER).asInt(), certificationNum)){
                throw new JWTVerificationException("본인 인증을 위한 jwt 토큰이 아님");
            }
        } catch (JWTVerificationException e) {
            throw new JWTIdentificationException(HttpStatus.BAD_REQUEST.value(), "JWT가 유효하지 않습니다.");
        }
        if(!Objects.equals(certificationNum, requestCertificationNum)){
            throw new JWTIdentificationException(HttpStatus.BAD_REQUEST.value(), "인증 번호가 다릅니다.");
        }

        Cache cache = cacheManager.getCache(SUCCEEDED_IDENTIFICATION_TOKEN);
        if(cache == null){
            log.error("인증 성공한 토큰 보관용 캐시가 존재하지 않음");
            throw new JWTIdentificationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 내 문제가 발생");
        }
        if(cache.get(jwt) == null){
            throw new JWTIdentificationException(HttpStatus.CONFLICT.value(), "이미 인증된 토큰입니다.");
        }
        cache.put(jwt, "Succeed");

        return authentication;
    }
}
