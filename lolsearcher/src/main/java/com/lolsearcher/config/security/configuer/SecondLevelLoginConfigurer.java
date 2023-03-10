package com.lolsearcher.config.security.configuer;

import com.lolsearcher.filter.Authentication.login.SecondLevelLoginJWTIdentificationFilter;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class SecondLevelLoginConfigurer <H extends HttpSecurityBuilder<H>> extends
        AbstractAuthenticationFilterConfigurer<H, SecondLevelLoginConfigurer<H>, SecondLevelLoginJWTIdentificationFilter> {

    public SecondLevelLoginConfigurer(){
        super(new SecondLevelLoginJWTIdentificationFilter(), "/identification/login");
    }

    @Override
    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        return new AntPathRequestMatcher(loginProcessingUrl, "POST");
    }
}
