package com.lolsearcher.filter.join;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.exception.exception.common.RequestBodyLengthException;
import com.lolsearcher.exception.exception.join.InvalidTokenException;
import com.lolsearcher.exception.exception.join.RandomNumDifferenceException;
import com.lolsearcher.model.entity.user.LolSearcherUser;
import com.lolsearcher.model.request.user.JwtJoinAuthentication;
import com.lolsearcher.model.request.user.RequestJoinIdentificationDto;
import com.lolsearcher.model.response.error.ErrorResponseBody;
import com.lolsearcher.model.response.front.user.ResponseJoinDto;
import com.lolsearcher.service.user.join.JoinService;
import com.lolsearcher.service.user.join.identification.JWTJoinIdentificationService;
import com.lolsearcher.service.user.join.identification.JoinIdentificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import static com.lolsearcher.constant.BeanNameConstants.*;
import static com.lolsearcher.constant.LolSearcherConstants.JWT_PREFIX;
import static com.lolsearcher.constant.LolSearcherConstants.MAX_BODY_LENGTH;

@Slf4j
@RequiredArgsConstructor
public class JWTUserJoinAuthenticationFilter extends UserJoinAuthenticationFilter {

    private final JoinIdentificationService joinIdentificationService;
    private final JoinService joinService;
    private final Map<String, ResponseEntity<ErrorResponseBody>> responseEntities;
    private final ObjectMapper objectMapper;

    @Override
    protected LolSearcherUser attemptAuthentication(ServletRequest request, ServletResponse response) {

        HttpServletRequest req = (HttpServletRequest) request;
        String token = req.getHeader(HttpHeaders.AUTHORIZATION);

        if(!token.startsWith(JWT_PREFIX)){
            log.error("적절한 JWT 토큰 값이 없습니다.");
            throw new InvalidTokenException(); // 403 권한 에러 발생
        }
        token = token.replaceAll(JWT_PREFIX, "");
        int requestRandomNum = obtainRandomNum(req);

        if(!(joinIdentificationService instanceof JWTJoinIdentificationService)){
            log.error("joinIdentificationService가 JWTJoinIdentificationService 인스턴스가 아닙니다.");
            throw new ClassCastException(); //500 서버 에러 발생
        }
        return joinIdentificationService.authenticate(new JwtJoinAuthentication(token, requestRandomNum));
    }

    @Override
    protected void successIdentification(ServletRequest request, ServletResponse response, LolSearcherUser user) {

        log.info("이메일 본인 인증이 성공했습니다.");
        joinService.joinUser(user);

        HttpServletResponse resp = (HttpServletResponse) response;
        resp.setStatus(HttpStatus.OK.value());
        resp.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ResponseJoinDto responseJoinDto = new ResponseJoinDto("회원가입 성공!!");
        try {
            String body = objectMapper.writeValueAsString(responseJoinDto);
            resp.getWriter().write(body);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void failIdentification(ServletRequest request, ServletResponse response, Exception e) {

        log.info("이메일 본인 인증이 실패했습니다.");

        ResponseEntity<ErrorResponseBody> responseEntity;

        if(e instanceof InvalidTokenException){
            responseEntity = responseEntities.get(FORBIDDEN_ENTITY_NAME);
        } else if(e instanceof RandomNumDifferenceException){
            responseEntity = responseEntities.get(BAD_REQUEST_ENTITY_NAME);
        } else if(e instanceof DataIntegrityViolationException) {
            responseEntity = responseEntities.get(CONFLICT_ENTITY_NAME);
        } else{
            responseEntity = responseEntities.get(INTERNAL_SERVER_ERROR_ENTITY_NAME);
        }

        HttpServletResponse resp = (HttpServletResponse) response;
        resp.setStatus(responseEntity.getStatusCodeValue());
        resp.setContentType(MediaType.APPLICATION_JSON_VALUE);

        try {
            String body = objectMapper.writeValueAsString(responseEntity.getBody());
            resp.getWriter().write(body);
        } catch (IOException ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    private int obtainRandomNum(HttpServletRequest req) {

        try {
            InputStream inputStream = req.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder sb = new StringBuilder();
            String tempValue;

            while((tempValue = br.readLine())!=null){
                sb.append(tempValue);
                if(sb.length() > MAX_BODY_LENGTH){
                    throw new RequestBodyLengthException();
                }
            }
            br.close();

            RequestJoinIdentificationDto body = objectMapper.readValue(sb.toString(), RequestJoinIdentificationDto.class);

            return body.getRequestRandomNum();

        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
