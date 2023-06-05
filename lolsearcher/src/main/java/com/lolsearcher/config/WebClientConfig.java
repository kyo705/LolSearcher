package com.lolsearcher.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
@Configuration
public class WebClientConfig {

    @Value("${lolsearcher.webclient.reactive-server-url}")
    private String REACTIVE_LOL_SEARCHER_SERVER_URI;
    @Value("${lolsearcher.webclient.notification-server-url}")
    private String NOTIFICATION_SERVER_URI;

    private final WebClient.Builder webclientBuilder;

    @Qualifier("reactiveLolSearcherWebClient")
    @Bean
    public WebClient reactiveLolSearcherWebClient() {
        return webclientBuilder
                .baseUrl(REACTIVE_LOL_SEARCHER_SERVER_URI)
                .build();
    }

    @Qualifier("notificationWebClient")
    @Bean
    public WebClient notificationWebClient() {
        return webclientBuilder
                .baseUrl(NOTIFICATION_SERVER_URI)
                .build();
    }
}
