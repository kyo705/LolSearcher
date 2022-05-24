package com.lolsearcher.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.lolsearcher.domain.Dto.currentgame.InGameDto;
import com.lolsearcher.restapi.RiotRestAPI;

@Service
public class InGameService {
	
	private final RiotRestAPI riotApi;
	
	@Autowired
	public InGameService(RiotRestAPI riotApi) {
		this.riotApi = riotApi;
	}
	
	public InGameDto getInGame(String summonerid) throws WebClientResponseException {
		return riotApi.getInGameBySummonerId(summonerid);
	}
}
