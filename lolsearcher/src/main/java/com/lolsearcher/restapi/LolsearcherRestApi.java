package com.lolsearcher.restapi;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.lolsearcher.domain.Dto.summoner.SummonerDto;

public interface LolsearcherRestApi {

	public ResponseEntity<Map> getSummonerById(String summonerid, String sessionid);
}
