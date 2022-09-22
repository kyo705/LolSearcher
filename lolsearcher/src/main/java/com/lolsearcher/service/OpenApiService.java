package com.lolsearcher.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.lolsearcher.domain.Dto.summoner.SummonerDto;
import com.lolsearcher.restapi.LolsearcherRestApi;

@Service
public class OpenApiService {

	private final LolsearcherRestApi lolsearcherRestApi;
	
	public OpenApiService(LolsearcherRestApi lolsearcherRestApi) {
		this.lolsearcherRestApi = lolsearcherRestApi;
	}
	
	public ResponseEntity<Map> findSummonerById(String summonerid, String sessionid) {
		ResponseEntity<Map> summoner = lolsearcherRestApi.getSummonerById(summonerid, sessionid);
		
		return summoner;
	}
}
