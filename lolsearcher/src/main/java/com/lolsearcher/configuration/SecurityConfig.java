package com.lolsearcher.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import com.lolsearcher.auth.UserLoginFailHandler;
import com.lolsearcher.exception.LolsearcherDeniedHandler;
import com.lolsearcher.repository.userrepository.UserRepository;
import com.lolsearcher.service.OauthUserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserLoginFailHandler userLoginFailHandler;
	
	@Autowired
	private UserRepository userRepository;
	
	/*public SecurityConfig() {}
	
	public SecurityConfig(UserLoginFailHandler userLoginFailHandler, 
			OauthUserService oauthUserService) {
		this.userLoginFailHandler = userLoginFailHandler;
		this.oauthUserService = oauthUserService;
	}*/
	
	@Bean
	public OauthUserService oauthUserService() {
		return new OauthUserService(userRepository, encodePwd());
	}
	
	@Bean
	public BCryptPasswordEncoder encodePwd() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public HttpSessionEventPublisher httpSessionEventPublisher() {
	    return new HttpSessionEventPublisher();
	}
	
	@Bean
	public LolsearcherDeniedHandler lolsearcherDeniedHandler() {
		return new LolsearcherDeniedHandler();
	}

	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		
		http.authorizeRequests()
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
				.accessDeniedHandler(lolsearcherDeniedHandler())
				.and()
			.oauth2Login()
				.loginPage("/loginForm")
				.userInfoEndpoint()
				.userService(oauthUserService())
		;
	}
}
