package com.lolsearcher.filter.integration;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import com.lolsearcher.controller.ChampionController;
import com.lolsearcher.filter.parameter.PositionValidationFilter;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class PositionValidationFliterTest {
	private final String KEY = "position";
	private final String SUCCESS_URI = "success";
	
	private MockMvc mockMvc;
	@Autowired
	private WebApplicationContext context;
	@MockBean
	private ChampionController championController;
	
	@BeforeEach
	public void beforeEach() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context)
	            .addFilters(new PositionValidationFilter())
	            .build();
		
		when(championController.championDetail(anyString()))
		.thenReturn(new ModelAndView(SUCCESS_URI));
	}
	
	@DisplayName("Request의 파라미터 값이 적절한 경우 다음 필터로 이동")
	@NullSource
	@ParameterizedTest
	@ValueSource(strings = {"TOP", "JUNGLE", "MIDDLE", "BOTTOM", "UTILITY"})
	void getRequestWithProperParameter(String value) throws Exception{
		mockMvc.perform(post("/champions").param(KEY, value))
		.andExpect(status().isOk());
	}
	
	@DisplayName("Request의 파라미터 값이 없을 경우 다음 필터로 이동")
	@Test
	void getRequestWithNoParameter() throws Exception{
		mockMvc.perform(post("/champions"))
		.andExpect(status().isOk());
	}
	
	@DisplayName("Request의 파라미터 값이 적절하지 않을 경우 실패 페이지로 이동")
	@ParameterizedTest
	@ValueSource(strings = {"", " ", "탑","TOP ","top"})
	void getRequestWithImproperParameter(String value) throws Exception{
		mockMvc.perform(post("/champions").param(KEY, value))
		.andExpect(status().is3xxRedirection());
	}
}
