package com.lolsearcher;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import com.lolsearcher.restapi.RiotRestAPI;
import com.lolsearcher.restapi.RiotRestApiv2;


@Configuration
public class LolSearcherConfig {

	private WebClient.Builder webclientBuilder;
	
	@Autowired
	public LolSearcherConfig(WebClient.Builder webclientBuilder) {
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
	
	@Bean
	public ExecutorService matchSavingThreadPool() {
		return Executors.newFixedThreadPool(100);
	}
	
}
