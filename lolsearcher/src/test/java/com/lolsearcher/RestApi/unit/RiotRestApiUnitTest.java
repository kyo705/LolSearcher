package com.lolsearcher.RestApi.unit;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import okhttp3.mockwebserver.MockWebServer;

public class RiotRestApiUnitTest {

	public static MockWebServer mockBackEnd;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }
    
    @BeforeEach
    void initialize() {
        String baseUrl = String.format("http://localhost:%s", 
          mockBackEnd.getPort());
        
    }
}
