package com.lolsearcher;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import com.lolsearcher.repository.JpaSummonerRepository;
import com.lolsearcher.repository.SummonerRepository;
import com.lolsearcher.restapi.RiotRestAPI;
import com.lolsearcher.restapi.RiotRestApiv2;
import com.lolsearcher.service.InGameService;
import com.lolsearcher.service.SummonerService;


@Configuration
public class SpringConfig {

	private EntityManager em;
	private WebClient.Builder webclientBuilder;
	
	@Autowired
	public SpringConfig(EntityManager em, WebClient.Builder webclientBuilder) {
		this.em = em;
		this.webclientBuilder = webclientBuilder;
	}
	
	@Bean
	public SummonerService summonerservice() {
		return new SummonerService(summonerRepository(), riotrestapi());
	}
	
	@Bean
	public SummonerRepository summonerRepository() {
		return new JpaSummonerRepository(em);
	}
	
	@Bean
	public InGameService currentMatchService() {
		return new InGameService(riotrestapi());
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
