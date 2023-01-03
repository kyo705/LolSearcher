package com.lolsearcher.controller.opnapi;

import com.lolsearcher.model.response.front.match.MatchDto;
import com.lolsearcher.model.response.front.rank.RankDto;
import com.lolsearcher.model.response.openapi.ResponseSummonerDto;
import com.lolsearcher.service.openapi.RestApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.util.List;

import static com.lolsearcher.constant.LolSearcherConstants.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class RestApiController {
	
	private final RestApiService restApiService;

	//-------------------------------- Retrieve Method ----------------------------------
	
	@GetMapping("/summoner/id/{id}")
	ResponseEntity<ResponseSummonerDto> getOneSummonerById(@PathVariable("id") String id){
		ResponseSummonerDto summoner = restApiService.getSummonerById(id);
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Link", "http://localhost:8080/docs/summoner-by-id.html; rel=\"profile\"");
		
		return ResponseEntity
				.status(HttpStatus.OK)
				.headers(headers)
				.body(summoner);
	}
	
	@GetMapping("/summoner/name/{name}")
	ResponseEntity<ResponseSummonerDto> getOneSummonerByName(@PathVariable("name") String name){
		ResponseSummonerDto summoner = restApiService.getSummonerByName(name);
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Link", "http://localhost:8080/docs/summoner-by-name.html; rel=\"profile\"");
		
		return ResponseEntity
				.status(HttpStatus.OK)
				.headers(headers)
				.body(summoner);
	}
	
	@GetMapping("/summoner/id/{id}/rank/solo/season/22")
	ResponseEntity<RankDto> getSoloRankById(@PathVariable("id") String id){
		RankDto rank = restApiService.getRankById(id, SOLO_RANK, CURRENT_SEASON_ID);
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Link", "http://localhost:8080/docs/solorank.html; rel=\"profile\"");
		
		return ResponseEntity
				.status(HttpStatus.OK)
				.headers(headers)
				.body(rank);
	}
	
	@GetMapping("/summoner/id/{id}/rank/flex/season/22")
	ResponseEntity<RankDto> getFlexRankById(@PathVariable("id") String id){
		RankDto rank = restApiService.getRankById(id, FLEX_RANK, CURRENT_SEASON_ID);
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Link", "http://localhost:8080/docs/teamrank.html; rel=\"profile\"");
		
		return ResponseEntity
				.status(HttpStatus.OK)
				.headers(headers)
				.body(rank);
	}
	
	@GetMapping("/summoner/id/{id}/ranks/season/22")
	ResponseEntity<List<RankDto>> getRanksById(@PathVariable("id") String id){
		List<RankDto> ranks = restApiService.getRanksById(id, CURRENT_SEASON_ID);
		
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
