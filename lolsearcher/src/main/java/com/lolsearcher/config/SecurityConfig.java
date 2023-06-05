package com.lolsearcher.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.ban.LoginBanFilter;
import com.lolsearcher.ban.SearchBanFilter;
import com.lolsearcher.config.ErrorResponseEntityConfig.ErrorResponseBody;
import com.lolsearcher.errors.handler.filter.CustomForbiddenEntryPoint;
import com.lolsearcher.errors.handler.filter.ExceptionHandlingFilter;
import com.lolsearcher.errors.handler.filter.LolsearcherDeniedHandler;
import com.lolsearcher.filter.HttpHeaderFilter;
import com.lolsearcher.login.LolSearcherAuthenticationFailureHandler;
import com.lolsearcher.login.LolSearcherAuthenticationSuccessHandler;
import com.lolsearcher.login.LolSearcherLoginConfigurer;
import com.lolsearcher.login.LolSearcherOauthUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.filter.ForwardedHeaderFilter;

import java.util.List;
import java.util.Map;

import static com.lolsearcher.login.LoginConstant.LOGIN_URI;
import static com.lolsearcher.user.identification.IdentificationConstant.IDENTIFICATION_URI;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final CacheManager cacheManager;
	private final ObjectMapper objectMapper;
	private final Map<String,ResponseEntity<ErrorResponseBody>> responseEntities;

	private final List<CorsFilter> corsFilters;
	private final LolSearcherOauthUserService oauthUserService;

	private final LolSearcherAuthenticationSuccessHandler lolSearcherAuthenticationSuccessHandler;
	private final LolSearcherAuthenticationFailureHandler lolSearcherAuthenticationFailureHandler;

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

		http.x509();

		addCustomAuthenticationFilter(http);
		addServletFilter(http);

		http
		.authorizeRequests()
				.antMatchers(HttpMethod.GET, "/user").permitAll()
				.antMatchers(HttpMethod.POST, "/user").permitAll()
				.antMatchers(IDENTIFICATION_URI).permitAll()
				.antMatchers("/user/**").access("hasRole('ROLE_USER')")
				.anyRequest().permitAll()
				.and()
			.sessionManagement()
				.maximumSessions(5)
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

		LolSearcherLoginConfigurer<HttpSecurity> lolSearcherLoginConfigurer = new LolSearcherLoginConfigurer<>(objectMapper);

		http.apply(lolSearcherLoginConfigurer)
				.loginProcessingUrl(LOGIN_URI)
				.successHandler(lolSearcherAuthenticationSuccessHandler)
				.failureHandler(lolSearcherAuthenticationFailureHandler);
	}

	private void addServletFilter(HttpSecurity http){

		corsFilters.forEach(http::addFilter);

		SearchBanFilter searchBanFilter = new SearchBanFilter(cacheManager, responseEntities, objectMapper);
		LoginBanFilter loginBanFilter = new LoginBanFilter(cacheManager, responseEntities, objectMapper);

		http.addFilterBefore(new HttpHeaderFilter(), HeaderWriterFilter.class)
				.addFilterBefore(new ForwardedHeaderFilter(), HeaderWriterFilter.class)
				.addFilterBefore(new ExceptionHandlingFilter(objectMapper), HttpHeaderFilter.class)
				.addFilterBefore(searchBanFilter, UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(loginBanFilter, UsernamePasswordAuthenticationFilter.class);
	}
}
