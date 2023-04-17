package com.lolsearcher.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.config.security.configuer.FirstLevelLoginConfigurer;
import com.lolsearcher.config.security.configuer.SecondLevelLoginConfigurer;
import com.lolsearcher.exception.handler.filter.springsecurity.authentication.*;
import com.lolsearcher.exception.handler.filter.springsecurity.authorization.CustomForbiddenEntryPoint;
import com.lolsearcher.exception.handler.filter.springsecurity.authorization.LolsearcherDeniedHandler;
import com.lolsearcher.filter.Authentication.join.JoinJWTIdentificationFilter;
import com.lolsearcher.filter.ban.LoginBanFilter;
import com.lolsearcher.filter.ban.SearchBanFilter;
import com.lolsearcher.filter.header.HttpHeaderFilter;
import com.lolsearcher.model.response.error.ErrorResponseBody;
import com.lolsearcher.service.user.login.OauthUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.HeaderWriterFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.session.Session;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;
import org.springframework.web.filter.CorsFilter;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final CacheManager cacheManager;
	private final ObjectMapper objectMapper;
	private final Map<String,ResponseEntity<ErrorResponseBody>> responseEntities;

	private final List<CorsFilter> corsFilters;
	private final OauthUserService oauthUserService;

	private final LoginAuthenticationSuccessHandler loginAuthenticationSuccessHandler;
	private final LoginAuthenticationFailureHandler loginAuthenticationFailureHandler;
	private final SecondLevelLoginAuthenticationFailureHandler secondLevelLoginAuthenticationFailureHandler;

	private final JoinAuthenticationSuccessHandler joinAuthenticationSuccessHandler;
	private final JoinAuthenticationFailureHandler joinAuthenticationFailureHandler;

	private final CustomForbiddenEntryPoint forbiddenEntryPoint;
	private final LolsearcherDeniedHandler lolsearcherDeniedHandler;

	@Bean
	public SpringSessionBackedSessionRegistry<? extends Session> sessionRegistry(RedisIndexedSessionRepository sessionRepository) {

		return new SpringSessionBackedSessionRegistry<>(sessionRepository);
	}

	@Bean
	public HttpSessionEventPublisher httpSessionEventPublisher() {
		return new HttpSessionEventPublisher();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.csrf()
				.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
				.requireCsrfProtectionMatcher(new AntPathRequestMatcher("/user/**"));

		addCustomAuthenticationFilter(http);
		addServletFilter(http);

		http
		.authorizeRequests()
				.antMatchers("/user/**").access("hasRole('ROLE_GET')")
				.antMatchers("/api/**").access("hasRole('ROLE_GET')")
				.anyRequest().permitAll()
				.and()
			.sessionManagement()
				.maximumSessions(1)
				.and()
				.and()
			.exceptionHandling()
				.accessDeniedHandler(lolsearcherDeniedHandler)
				.authenticationEntryPoint(forbiddenEntryPoint)
				.and()
			.oauth2Login()
				.loginPage("/loginForm")
				.userInfoEndpoint()
				.userService(oauthUserService)
		;
	}

	private void addCustomAuthenticationFilter(HttpSecurity http) throws Exception {

		AuthenticationManager authenticationManager = authenticationManager();

		//1차 로그인 필터 등록
		FirstLevelLoginConfigurer<HttpSecurity> firstLevelLoginConfigurer = new FirstLevelLoginConfigurer<>(objectMapper);
		//2차 로그인 필터 등록
		SecondLevelLoginConfigurer<HttpSecurity> secondLevelLoginConfigurer = new SecondLevelLoginConfigurer<>();

		http.apply(firstLevelLoginConfigurer)
				.loginProcessingUrl("/login")
				.successHandler(loginAuthenticationSuccessHandler)
				.failureHandler(loginAuthenticationFailureHandler)
				.and()
			.apply(secondLevelLoginConfigurer)
				.loginProcessingUrl("/identification/login")
				.successHandler(loginAuthenticationSuccessHandler)
				.failureHandler(secondLevelLoginAuthenticationFailureHandler);

		//회원가입 본인 인증 필터 등록
		JoinJWTIdentificationFilter joinFilter = new JoinJWTIdentificationFilter();
		joinFilter.setFilterProcessesUrl("/identification/join");
		joinFilter.setAuthenticationManager(authenticationManager);
		joinFilter.setAuthenticationSuccessHandler(joinAuthenticationSuccessHandler);
		joinFilter.setAuthenticationFailureHandler(joinAuthenticationFailureHandler);

		http.addFilterBefore(joinFilter, UsernamePasswordAuthenticationFilter.class);
	}

	private void addServletFilter(HttpSecurity http){

		corsFilters.forEach(http::addFilter);

		SearchBanFilter searchBanFilter = new SearchBanFilter(cacheManager, responseEntities, objectMapper);
		LoginBanFilter loginBanFilter = new LoginBanFilter(cacheManager, responseEntities, objectMapper);

		http.addFilterBefore(new HttpHeaderFilter(), HeaderWriterFilter.class)
				.addFilterBefore(searchBanFilter, UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(loginBanFilter, UsernamePasswordAuthenticationFilter.class);
	}
}
