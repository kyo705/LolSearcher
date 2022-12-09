package com.lolsearcher.controller;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lolsearcher.model.dto.match.MatchDto;
import com.lolsearcher.model.dto.rank.RankDto;
import com.lolsearcher.model.dto.summoner.SummonerDto;
import com.lolsearcher.model.entity.match.Match;
import com.lolsearcher.service.openapi.RestApiService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class RestApiController {

	private static final String SOLO = "RANKED_SOLO_5x5";
	private static final String FLEX = "RANKED_FLEX_SR";
	private static final int SEASON = 22;
	
	private final RestApiService restApiService;

	//-------------------------------- Retrieve Method ----------------------------------
	
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
		RankDto rank = restApiService.getRankById(id, SOLO, SEASON);
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Link", "http://localhost:8080/docs/solorank.html; rel=\"profile\"");
		
		return ResponseEntity
				.status(HttpStatus.OK)
				.headers(headers)
				.body(rank);
	}
	
	@GetMapping("/summoner/id/{id}/rank/flex/season/22")
	ResponseEntity<RankDto> getFlexRankById(@PathVariable("id") String id){
		RankDto rank = restApiService.getRankById(id, FLEX, SEASON);
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Link", "http://localhost:8080/docs/teamrank.html; rel=\"profile\"");
		
		return ResponseEntity
				.status(HttpStatus.OK)
				.headers(headers)
				.body(rank);
	}
	
	@GetMapping("/summoner/id/{id}/ranks/season/22")
	ResponseEntity<List<RankDto>> getRanksById(@PathVariable("id") String id){
		List<RankDto> ranks = restApiService.getRanksById(id, SEASON);
		
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
	
	//---------------------------------- Create Method ----------------------------------
	
	@PostMapping("/match")
	ResponseEntity<Map<String, String>> setMatches(@RequestParam List<Match> matches){
		
		restApiService.setMatches(matches);
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Link", "http://localhost:8080/docs/match.html; rel=\"profile\"");
		
		Map<String, String> body = new HashMap<>();
		body.put("request", "success");
		
		return ResponseEntity
				.status(HttpStatus.OK)
				.headers(headers)
				.body(body);
	}
	
	@PostMapping("/match/{matchid}")
	ResponseEntity<Map<String, String>> setOneMatch(@PathVariable("matchid") String matchId,
													@RequestParam Match match){
		
		restApiService.setOneMatch(match);
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Link", "http://localhost:8080/docs/match.html; rel=\"profile\"");
		
		Map<String, String> body = new HashMap<>();
		body.put("request", "success");
		
		return ResponseEntity
				.status(HttpStatus.OK)
				.headers(headers)
				.body(body);
	}
	
	//---------------------------------- Update Method ----------------------------------
	
	
	//---------------------------------- Delete Method ----------------------------------
	
	
	
	//---------------------------------- Error Controller ----------------------------------
	@GetMapping(path = "/error/forbidden")
	public void ForbiddenHandler(){
		String errorMessage = "no authority";
		
		throw new AccessDeniedException(errorMessage);
	}
	
	@GetMapping("/**")
	public void getUrlException() throws MalformedURLException {
		throw new MalformedURLException("bad url request");
	}
}
