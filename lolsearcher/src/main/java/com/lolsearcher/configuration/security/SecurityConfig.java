package com.lolsearcher.configuration.security;

import com.lolsearcher.auth.exceptiontranslationfilter.LolsearcherDeniedHandler;
import com.lolsearcher.auth.usernamepassword.UserLoginFailHandler;
import com.lolsearcher.filter.EncodingFilter;
import com.lolsearcher.filter.LoginBanFilter;
import com.lolsearcher.filter.SearchBanFilter;
import com.lolsearcher.service.login.OauthUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.HeaderWriterFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final OauthUserService oauthUserService;

	private final List<CorsFilter> corsFilters;
	private final CacheManager cacheManager;

	private final UserLoginFailHandler userLoginFailHandler;
	public final LolsearcherDeniedHandler lolsearcherDeniedHandler;

	@Bean
	public HttpSessionEventPublisher httpSessionEventPublisher() {
		return new HttpSessionEventPublisher();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();

		corsFilters.forEach(http::addFilter);

		http.addFilterBefore(new EncodingFilter(), HeaderWriterFilter.class)
				.addFilterBefore(new SearchBanFilter(cacheManager),UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(new LoginBanFilter(cacheManager), UsernamePasswordAuthenticationFilter.class)
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
