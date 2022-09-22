package com.lolsearcher.configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import com.lolsearcher.restapi.RiotRestAPI;
import com.lolsearcher.restapi.RiotRestApiv2;
import com.lolsearcher.service.ThreadService;



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
	
	@Bean
	public RiotRestAPI riotRestApi(WebClient webclient,
			ExecutorService executorService,
			ThreadService threadService) {
		
		return new RiotRestApiv2(webclient, executorService, threadService);
	}
}
