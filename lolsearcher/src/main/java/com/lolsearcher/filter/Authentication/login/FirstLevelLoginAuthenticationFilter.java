package com.lolsearcher.filter.Authentication.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.constant.enumeration.LoginSecurityPolicyStatus;
import com.lolsearcher.exception.exception.user.login.NotYetSecondLevelLoginException;
import com.lolsearcher.model.request.user.login.RequestUsernamePasswordDto;
import com.lolsearcher.model.response.front.user.LolsearcherUserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

public class FirstLevelLoginAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper;

    public FirstLevelLoginAuthenticationFilter(ObjectMapper objectMapper) {
        super();
        this.objectMapper = objectMapper;
    }

    public FirstLevelLoginAuthenticationFilter(ObjectMapper objectMapper, AuthenticationManager authenticationManager) {
        super(authenticationManager);
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        RequestUsernamePasswordDto requestUsernamePasswordDto = getUsernamePasswordDto(request); /* requestBody로 부터 데이터 파싱 */

        String username = requestUsernamePasswordDto.getEmail();
        String password = requestUsernamePasswordDto.getPassword();

        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);

        setDetails(request, authRequest);
        Authentication authentication = this.getAuthenticationManager().authenticate(authRequest);

        checkSecurityLevel(authentication); /* 2차 로그인 시도 여부 확인 */

        return authentication;
    }

    private void checkSecurityLevel(Authentication authentication) {

        LolsearcherUserDetails details = (LolsearcherUserDetails)authentication.getPrincipal();

        if(details.getSecurityLevel() >= LoginSecurityPolicyStatus.IDENTIFICATION.getLevel()){
            throw new NotYetSecondLevelLoginException(authentication);
        }
    }

    private RequestUsernamePasswordDto getUsernamePasswordDto(HttpServletRequest request){

        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader br = request.getReader();
            String str;

            while((str = br.readLine()) != null){
                stringBuilder.append(str);
            }

            return objectMapper.readValue(stringBuilder.toString(), RequestUsernamePasswordDto.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
