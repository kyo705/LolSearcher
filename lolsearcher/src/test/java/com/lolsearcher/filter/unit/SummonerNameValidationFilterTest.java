package com.lolsearcher.filter.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.lolsearcher.filter.parameter.SummonerNameValidationFilter;

@ExtendWith(MockitoExtension.class)
public class SummonerNameValidationFilterTest {
	private final String KEY = "name";
	private final String FAIL_HANDLER_URI = "/invalid";
	
	SummonerNameValidationFilter summonerNameValidationFilter;
	
	MockHttpServletRequest req;
	@Mock
	MockHttpServletResponse rsp;
	@Mock
	MockFilterChain mockChain;
	
	@BeforeEach
	void setup() {
		summonerNameValidationFilter = new SummonerNameValidationFilter();
		req = new MockHttpServletRequest();
	}
	
	@DisplayName("Request의 파라미터 값이 적절한 경우 다음 필터로 이동")
	@ParameterizedTest
	@CsvSource({"aaaa,aaaa", "kyo705,kyo705", "kyo&705!,kyo705", "kyo 705,kyo 705"})
	void getRequestWithProperParameter(String unfilteredName, String filteredName) throws IOException, ServletException{
		//given
		req.setParameter(KEY, unfilteredName);
		//when
		summonerNameValidationFilter.doFilter(req, rsp, mockChain);
		//then
		assertThat(req.getAttribute(KEY)).isEqualTo(filteredName);
		
		verify(mockChain, times(1)).doFilter(req, rsp);
		verify(rsp, times(0)).sendRedirect(FAIL_HANDLER_URI);
	}
	
	@DisplayName("Request의 파라미터 길이가 설정 값을 초과한 경우 실패 페이지로 이동")
	@ParameterizedTest
	@ValueSource(strings = {"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"})
	void getRequestWithExceedLengthParameter(String value) throws IOException, ServletException{
		//given
		req.setParameter(KEY, value);
		//when
		summonerNameValidationFilter.doFilter(req, rsp, mockChain);
		//then
		verify(mockChain, times(0)).doFilter(req, rsp);
		verify(rsp, times(1)).sendRedirect(FAIL_HANDLER_URI);
	}
	
	@DisplayName("Request의 파라미터 값이 없을 경우 실패 페이지로 이동")
	@Test
	void getRequestWithNoParameter() throws IOException, ServletException{
		//no given
		//when
		summonerNameValidationFilter.doFilter(req, rsp, mockChain);
		//then
		verify(mockChain, times(0)).doFilter(req, rsp);
		verify(rsp, times(1)).sendRedirect(FAIL_HANDLER_URI);
	}
	
	@DisplayName("Request의 파라미터 값이 null이거나 empty인 경우 실패 페이지로 이동")
	@NullAndEmptySource
	@ParameterizedTest
	void getRequestWithNullOrEmptyParameter(String value) throws IOException, ServletException{
		//given
		req.setParameter(KEY, value);
		//when
		summonerNameValidationFilter.doFilter(req, rsp, mockChain);
		//then
		verify(mockChain, times(0)).doFilter(req, rsp);
		verify(rsp, times(1)).sendRedirect(FAIL_HANDLER_URI);
	}
}
