package com.lolsearcher.filter.Authentication.login;

import com.lolsearcher.filter.Authentication.JWTIdentificationFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SecondLevelLoginJWTIdentificationFilter extends JWTIdentificationFilter {

    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER =
            new AntPathRequestMatcher("/identification/join", "POST");

    public SecondLevelLoginJWTIdentificationFilter() {
        super();
        setRequiresAuthenticationRequestMatcher(DEFAULT_ANT_PATH_REQUEST_MATCHER);
    }

    public SecondLevelLoginJWTIdentificationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
        setRequiresAuthenticationRequestMatcher(DEFAULT_ANT_PATH_REQUEST_MATCHER);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        Authentication realAuthentication = (Authentication) authResult.getPrincipal();

        super.successfulAuthentication(request, response, chain, realAuthentication);
    }
}
