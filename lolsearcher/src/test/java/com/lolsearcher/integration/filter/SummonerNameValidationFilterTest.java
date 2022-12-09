package com.lolsearcher.integration.filter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
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

import com.lolsearcher.controller.SummonerController;
import com.lolsearcher.filter.parameter.SummonerNameValidationFilter;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class SummonerNameValidationFilterTest {
	private final String KEY = "name";
	private final String SUCCESS_URI = "/success";
	
	private MockMvc mockMvc;
	@Autowired
	private WebApplicationContext context;
	@MockBean
	private SummonerController summonerController;
	
	@BeforeEach
	public void beforeEach() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context)
	            .addFilters(new SummonerNameValidationFilter())
	            .build();
		
		when(summonerController.getSummonerData(any(), anyString()))
		.thenReturn(new ModelAndView(SUCCESS_URI));
	}
	
	@DisplayName("Request의 파라미터 값이 적절한 경우 다음 필터로 이동")
	@ParameterizedTest
	@CsvSource({"푸켓푸켓,푸켓푸켓", "푸켓!푸켓,푸켓푸켓", "!푸켓@푸$켓,푸켓푸켓"})
	void getRequestWithProperParameter(String unfilteredName, String filteredName) throws Exception{
		mockMvc.perform(post("/summoner").param(KEY, unfilteredName))
		.andExpect(status().isOk())
		.andExpect(request().attribute(KEY, filteredName));
	}
	
	@DisplayName("Request의 파라미터 길이가 설정 값을 초과한 경우 실패 페이지로 이동")
	@ParameterizedTest
	@ValueSource(strings = {"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"})
	void getRequestWithExceedLengthParameter(String value) throws Exception{
		mockMvc.perform(post("/summoner").param(KEY, value))
		.andExpect(status().is3xxRedirection());
	}
	
	@DisplayName("Request의 파라미터 값이 없을 경우 실패 페이지로 이동")
	@Test
	void getRequestWithNoParameter() throws Exception{
		mockMvc.perform(post("/summoner"))
		.andExpect(status().is3xxRedirection());
	}
	
	@DisplayName("Request의 파라미터 값이 null이거나 empty인 경우 실패 페이지로 이동")
	@NullAndEmptySource
	@ParameterizedTest
	void getRequestWithNullOrEmptyParameter(String value) throws Exception{
		mockMvc.perform(post("/summoner").param(KEY, value))
		.andExpect(status().is3xxRedirection());
	}
}
