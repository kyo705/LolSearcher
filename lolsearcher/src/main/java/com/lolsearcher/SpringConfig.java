package com.lolsearcher;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lolsearcher.repository.JpaSummonerRepository;
import com.lolsearcher.repository.SummonerRepository;
import com.lolsearcher.restapi.RiotRestAPI;
import com.lolsearcher.restapi.RiotRestApiv1;
import com.lolsearcher.service.Summonerservice;


@Configuration
public class SpringConfig {

	private EntityManager em;
	
	@Autowired
	public SpringConfig(EntityManager em) {
		this.em = em;
	}
	
	@Bean
	public Summonerservice summonerservice() {
		
		return new Summonerservice(summonerRepository(), riotrestapi());
	}
	
	@Bean
	public SummonerRepository summonerRepository() {
		
		return new JpaSummonerRepository(em);
	}
	@Bean
	public RiotRestAPI riotrestapi() {
		return new RiotRestApiv1();
	}
	
}
