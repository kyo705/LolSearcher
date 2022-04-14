package com.lolsearcher.service;

import com.lolsearcher.domain.Dto.CurrentGame.InGameDto;
import com.lolsearcher.restapi.RiotRestAPI;

public class InGameService {
	
	private final RiotRestAPI riotApi;
	
	public InGameService(RiotRestAPI riotApi) {
		this.riotApi = riotApi;
	}
	
	public InGameDto getInGame(String summonerid) {
		return riotApi.getInGameBySummonerId(summonerid);
	}
}
