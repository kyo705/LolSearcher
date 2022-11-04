package com.lolsearcher.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

import com.lolsearcher.auth.exceptiontranslationfilter.LolsearcherDeniedHandler;
import com.lolsearcher.auth.usernamepassword.UserLoginFailHandler;
import com.lolsearcher.filter.LoginBanFilter;
import com.lolsearcher.service.login.OauthUserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private OauthUserService oauthUserService;
	
	@Autowired
	@Qualifier("openApiCorsFilter")
	private CorsFilter openApiCorsFilter;
	@Autowired
	private LoginBanFilter loginBanFilter;
	
	@Autowired
	private UserLoginFailHandler userLoginFailHandler;
	@Autowired
	public LolsearcherDeniedHandler lolsearcherDeniedHandler;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		
		http.addFilter(openApiCorsFilter)
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
