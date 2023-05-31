package com.lolsearcher.config.security.configuer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.login.LolSearcherLoginAuthenticationFilter;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class FirstLevelLoginConfigurer<H extends HttpSecurityBuilder<H>> extends
        AbstractAuthenticationFilterConfigurer<H, FirstLevelLoginConfigurer<H>, LolSearcherLoginAuthenticationFilter> {

    public FirstLevelLoginConfigurer(ObjectMapper objectMapper) {
        super(new LolSearcherLoginAuthenticationFilter(objectMapper), null);
    }

    @Override
    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        return new AntPathRequestMatcher(loginProcessingUrl, "POST");
    }
}
