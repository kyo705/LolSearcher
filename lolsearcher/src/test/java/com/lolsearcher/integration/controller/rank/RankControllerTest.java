package com.lolsearcher.integration.controller.rank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.model.response.common.ErrorResponseBody;
import com.lolsearcher.service.summoner.SummonerService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.Map;

public class RankControllerTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private Map<String, ResponseEntity<ErrorResponseBody>> errorResponseEntities;
    @MockBean
    private SummonerService summonerService;

    @BeforeEach
    public void beforeEach() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
                .build();
    }


}
