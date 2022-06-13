package com.lolsearcher.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lolsearcher.domain.Dto.summoner.MatchDto;
import com.lolsearcher.domain.Dto.summoner.RankDto;
import com.lolsearcher.domain.Dto.summoner.SummonerDto;
import com.lolsearcher.service.RestApiService;

@RestController
@RequestMapping("/api")
public class RestApiController {

	private static final String solo = "RANKED_SOLO_5x5";
	private static final String flex = "RANKED_FLEX_SR";
	private static final int season = 22;
	
	private RestApiService restApiService;
	
	public RestApiController(RestApiService restApiService) {
		this.restApiService = restApiService;
	}

	@GetMapping("/summoner/id/{id}")
	ResponseEntity<SummonerDto> getOneSummonerById(@PathVariable("id") String id){
		
		SummonerDto summoner = restApiService.getSummonerById(id);
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Link", "http://localhost:8080/docs/summoner-by-id.html; rel=\"profile\"");
		
		return ResponseEntity
				.status(HttpStatus.OK)
				.headers(headers)
				.body(summoner);
	}
	
	@GetMapping("/summoner/name/{name}")
	ResponseEntity<SummonerDto> getOneSummonerByName(@PathVariable("name") String name){
		
		SummonerDto summoner = restApiService.getSummonerByName(name);
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Link", "http://localhost:8080/docs/summoner-by-name.html; rel=\"profile\"");
		
		return ResponseEntity
				.status(HttpStatus.OK)
				.headers(headers)
				.body(summoner);
	}
	
	@GetMapping("/summoner/id/{id}/rank/solo/season/22")
	ResponseEntity<RankDto> getSoloRankById(@PathVariable("id") String id){
		
		RankDto rank = restApiService.getRankById(id, solo, season);
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Link", "http://localhost:8080/docs/solorank.html; rel=\"profile\"");
		
		return ResponseEntity
				.status(HttpStatus.OK)
				.headers(headers)
				.body(rank);
	}
	
	@GetMapping("/summoner/id/{id}/rank/flex/season/22")
	ResponseEntity<RankDto> getFlexRankById(@PathVariable("id") String id){
		
		RankDto rank = restApiService.getRankById(id, flex, season);
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Link", "http://localhost:8080/docs/teamrank.html; rel=\"profile\"");
		
		return ResponseEntity
				.status(HttpStatus.OK)
				.headers(headers)
				.body(rank);
	}
	
	@GetMapping("/summoner/id/{id}/ranks/season/22")
	ResponseEntity<List<RankDto>> getRanksById(@PathVariable("id") String id){
		
		List<RankDto> ranks = restApiService.getRanksById(id, season);
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Link", "http://localhost:8080/docs/ranks.html; rel=\"profile\"");
		
		return ResponseEntity
				.status(HttpStatus.OK)
				.headers(headers)
				.body(ranks);
	}
	
	@GetMapping("/summoner/id/{id}/matcheIds")
	ResponseEntity<List<String>> getMatchIds(
			@PathVariable("id") String summonerId,
			@RequestParam(defaultValue = "0", name = "start") int start,
			@RequestParam(defaultValue = "100", name = "count") int count){
		
		List<String> matchIds = restApiService.getMatchIds(summonerId, start, count);
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Link", "http://localhost:8080/docs/matchids.html; rel=\"profile\"");
		
		return ResponseEntity
				.status(HttpStatus.OK)
				.headers(headers)
				.body(matchIds);
	}
	
	@GetMapping("/match/{matchid}")
	ResponseEntity<MatchDto> getOneMatch(@PathVariable("matchid") String matchId){
		
		MatchDto match = restApiService.getMatch(matchId);
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Link", "http://localhost:8080/docs/match.html; rel=\"profile\"");
		
		return ResponseEntity
				.status(HttpStatus.OK)
				.headers(headers)
				.body(match);
	}
}
