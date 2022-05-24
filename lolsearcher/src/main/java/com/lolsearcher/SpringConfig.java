package com.lolsearcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import com.lolsearcher.restapi.RiotRestAPI;
import com.lolsearcher.restapi.RiotRestApiv2;


@Configuration
public class SpringConfig {

	private WebClient.Builder webclientBuilder;
	
	@Autowired
	public SpringConfig( WebClient.Builder webclientBuilder) {
		this.webclientBuilder = webclientBuilder;
	}
	
	@Bean
	public RiotRestAPI riotrestapi() {
		return new RiotRestApiv2(webclient());
	}
	
	@Bean
	public WebClient webclient() {
		return webclientBuilder.build();
	}
	
}
