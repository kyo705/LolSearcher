package com.lolsearcher.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.exception.handler.security.authentication.UserLoginFailHandler;
import com.lolsearcher.exception.handler.security.authorization.LolsearcherDeniedHandler;
import com.lolsearcher.filter.ban.LoginBanFilter;
import com.lolsearcher.filter.ban.SearchBanFilter;
import com.lolsearcher.filter.header.HttpHeaderFilter;
import com.lolsearcher.filter.join.JWTUserJoinAuthenticationFilter;
import com.lolsearcher.filter.join.UserJoinAuthenticationFilter;
import com.lolsearcher.model.response.error.ErrorResponseBody;
import com.lolsearcher.service.user.join.JoinService;
import com.lolsearcher.service.user.join.identification.JoinIdentificationService;
import com.lolsearcher.service.user.login.OauthUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.HeaderWriterFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
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

	private final JoinIdentificationService joinIdentificationService;
	private final JoinService joinService;
	private final OauthUserService oauthUserService;

	private final UserLoginFailHandler userLoginFailHandler;
	private final LolsearcherDeniedHandler lolsearcherDeniedHandler;

	@Bean
	public HttpSessionEventPublisher httpSessionEventPublisher() {
		return new HttpSessionEventPublisher();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();

		corsFilters.forEach(http::addFilter);

		UserJoinAuthenticationFilter jwtUserJoinAuthenticationFilter =
				new JWTUserJoinAuthenticationFilter(joinIdentificationService, joinService, responseEntities, objectMapper);

		SearchBanFilter searchBanFilter = new SearchBanFilter(cacheManager, responseEntities, objectMapper);
		LoginBanFilter loginBanFilter = new LoginBanFilter(cacheManager, responseEntities, objectMapper);

		http.addFilterBefore(new HttpHeaderFilter(), HeaderWriterFilter.class)
				.addFilterAfter(jwtUserJoinAuthenticationFilter, HeaderWriterFilter.class)
				.addFilterBefore(searchBanFilter, UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(loginBanFilter, UsernamePasswordAuthenticationFilter.class)
		.authorizeRequests()
				.antMatchers("/api/**").access("hasRole('ROLE_GET')")
				.anyRequest().permitAll()
				.and()
			.formLogin()
				.loginPage("/loginForm")
				.loginProcessingUrl("/login")
				.defaultSuccessUrl("/")
				.failureHandler(userLoginFailHandler)
				.and()
			.sessionManagement()
				.maximumSessions(1)
				.expiredUrl("/expired")
				.and()
				.and()
			.exceptionHandling()
				.accessDeniedHandler(lolsearcherDeniedHandler)
				.and()
			.oauth2Login()
				.loginPage("/loginForm")
				.userInfoEndpoint()
				.userService(oauthUserService)
		;
	}
}
