package com.lolsearcher.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class LolSearcherLoginConfigurer<H extends HttpSecurityBuilder<H>> extends
        AbstractAuthenticationFilterConfigurer<H, LolSearcherLoginConfigurer<H>, LolSearcherLoginAuthenticationFilter> {

    public LolSearcherLoginConfigurer(ObjectMapper objectMapper) {
        super(new LolSearcherLoginAuthenticationFilter(objectMapper), null);
    }

    @Override
    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        return new AntPathRequestMatcher(loginProcessingUrl, "POST");
    }
}
