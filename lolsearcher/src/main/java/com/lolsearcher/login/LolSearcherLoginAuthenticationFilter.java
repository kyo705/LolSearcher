package com.lolsearcher.login;

import com.fasterxml.jackson.databind.ObjectMapper;
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

public class LolSearcherLoginAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper;

    public LolSearcherLoginAuthenticationFilter(ObjectMapper objectMapper) {
        super();
        this.objectMapper = objectMapper;
    }

    public LolSearcherLoginAuthenticationFilter(ObjectMapper objectMapper, AuthenticationManager authenticationManager) {
        super(authenticationManager);
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        LoginRequest loginRequest = getLoginRequest(request); /* requestBody로 부터 데이터 파싱 */

        String username = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);

        setDetails(request, authRequest);

        return getAuthenticationManager().authenticate(authRequest);
    }

    private LoginRequest getLoginRequest(HttpServletRequest request){

        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader br = request.getReader();
            String str;

            while((str = br.readLine()) != null){
                stringBuilder.append(str);
            }
            return objectMapper.readValue(stringBuilder.toString(), LoginRequest.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
