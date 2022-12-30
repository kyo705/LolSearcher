package com.lolsearcher.config;

import okhttp3.mockwebserver.MockWebServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import javax.annotation.PreDestroy;
import java.io.IOException;

@TestConfiguration
public class MockWebServerConfig {

    @Value("${lolsearcher.mock-server.port}")
    private int port;

    @Bean
    public MockWebServer mockWebServer() throws IOException {
        MockWebServer mockWebServer = new MockWebServer();

        mockWebServer.start(port);

        return mockWebServer;
    }

    @PreDestroy
    public void shutdown() throws IOException {
        mockWebServer().shutdown();
    }
}
