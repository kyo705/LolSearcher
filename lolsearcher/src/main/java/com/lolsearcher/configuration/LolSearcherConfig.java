package com.lolsearcher.configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class LolSearcherConfig {

	private WebClient.Builder webclientBuilder;
	
	public LolSearcherConfig(WebClient.Builder webclientBuilder) {
		this.webclientBuilder = webclientBuilder;
	}
	
	@Bean
	public WebClient webclient() {
		return webclientBuilder.build();
	}
	@Bean
	public ExecutorService matchSavingThreadPool() {
		return Executors.newFixedThreadPool(100);
	}
	
	//-------------------- 시큐리티 관련 @bean 등록 ----------------------------
	
	@Bean
	public BCryptPasswordEncoder encodePwd() {
		return new BCryptPasswordEncoder();
	}
	@Bean
	public HttpSessionEventPublisher httpSessionEventPublisher() {
	    return new HttpSessionEventPublisher();
	}

}
