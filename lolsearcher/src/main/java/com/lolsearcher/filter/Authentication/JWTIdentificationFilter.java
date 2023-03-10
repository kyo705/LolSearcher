package com.lolsearcher.filter.Authentication;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.constant.LolSearcherConstants;
import com.lolsearcher.model.request.user.identification.RequestIdentificationDto;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

import static com.lolsearcher.constant.LolSearcherConstants.CERTIFICATION_NUMBER;

/**
 *  JWT 방식으로 본인 인증을 위한 필터.
 */
public class JWTIdentificationFilter extends IdentificationFilter {


    protected final ObjectMapper objectMapper = new ObjectMapper();

    public JWTIdentificationFilter() {
        super();
    }

    public JWTIdentificationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected Object obtainUserInfo(HttpServletRequest request) {

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(authorizationHeader == null || !authorizationHeader.startsWith(LolSearcherConstants.JWT_PREFIX)){
            throw new AuthenticationServiceException("Proper Authorization Header is not exist");
        }
        return request.getHeader(HttpHeaders.AUTHORIZATION)
                .replaceAll(LolSearcherConstants.JWT_PREFIX, "");
    }

    @Override
    protected Integer obtainCertificationNum(HttpServletRequest request) {

        String token = request.getHeader(HttpHeaders.AUTHORIZATION).replaceAll(LolSearcherConstants.JWT_PREFIX, "");

        return JWT.decode(token).getClaim(CERTIFICATION_NUMBER).asInt();
    }

    @Override
    protected Integer obtainRequestCertificationNum(HttpServletRequest request) {

        try {
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader br = request.getReader();
            String str;

            while((str = br.readLine()) != null){
                stringBuilder.append(str);
            }
            RequestIdentificationDto requestDto = objectMapper.readValue(stringBuilder.toString(), RequestIdentificationDto.class);

            return requestDto.getCertificationNumber();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
