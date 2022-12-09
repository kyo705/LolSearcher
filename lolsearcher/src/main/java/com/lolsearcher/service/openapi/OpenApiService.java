package com.lolsearcher.service.openapi;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.lolsearcher.api.lolsearcher.LolsearcherRestApi;

@RequiredArgsConstructor
@Service
public class OpenApiService {

	private final LolsearcherRestApi lolsearcherRestApi;
	
	public ResponseEntity<Map> findSummonerById(String summonerId, String sessionId) {
		return lolsearcherRestApi.getSummonerById(summonerId, sessionId);
	}
}
