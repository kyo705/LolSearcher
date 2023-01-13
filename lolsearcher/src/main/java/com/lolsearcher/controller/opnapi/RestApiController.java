package com.lolsearcher.controller.opnapi;

import com.lolsearcher.model.factory.OpenApiResponseDtoFactory;
import com.lolsearcher.model.output.front.match.MatchDto;
import com.lolsearcher.model.output.openapi.OpenApiRankDto;
import com.lolsearcher.model.output.openapi.OpenApiSummonerDto;
import com.lolsearcher.service.openapi.RestApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
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
	ResponseEntity<OpenApiSummonerDto> getOneSummonerById(@PathVariable("id") String id){
		OpenApiSummonerDto summoner = restApiService.getSummonerById(id);
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Link", "http://localhost:8080/docs/summoner-by-id.html; rel=\"profile\"");
		
		return OpenApiResponseDtoFactory.getResponseEntity(headers, summoner);
	}
	
	@GetMapping("/summoner/name/{name}")
	ResponseEntity<OpenApiSummonerDto> getOneSummonerByName(@PathVariable("name") String name){
		OpenApiSummonerDto summoner = restApiService.getSummonerByName(name);
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Link", "http://localhost:8080/docs/summoner-by-name.html; rel=\"profile\"");

		return OpenApiResponseDtoFactory.getResponseEntity(headers, summoner);
	}
	
	@GetMapping("/summoner/id/{id}/rank/solo/season/22")
	ResponseEntity<OpenApiRankDto> getSoloRankById(@PathVariable("id") String id){
		OpenApiRankDto rank = restApiService.getRankById(id, SOLO_RANK, CURRENT_SEASON_ID);
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Link", "http://localhost:8080/docs/solorank.html; rel=\"profile\"");

		return OpenApiResponseDtoFactory.getResponseEntity(headers, rank);
	}
	
	@GetMapping("/summoner/id/{id}/rank/flex/season/22")
	ResponseEntity<OpenApiRankDto> getFlexRankById(@PathVariable("id") String id){
		OpenApiRankDto rank = restApiService.getRankById(id, FLEX_RANK, CURRENT_SEASON_ID);
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Link", "http://localhost:8080/docs/teamrank.html; rel=\"profile\"");

		return OpenApiResponseDtoFactory.getResponseEntity(headers, rank);
	}
	
	@GetMapping("/summoner/id/{id}/ranks/season/22")
	ResponseEntity<List<OpenApiRankDto>> getRanksById(@PathVariable("id") String id){
		List<OpenApiRankDto> ranks = restApiService.getRanksById(id, CURRENT_SEASON_ID);
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Link", "http://localhost:8080/docs/ranks.html; rel=\"profile\"");

		return OpenApiResponseDtoFactory.getResponseEntity(headers, ranks);
	}
	
	@GetMapping("/summoner/id/{id}/matcheIds")
	ResponseEntity<List<String>> getMatchIds(
			@PathVariable("id") String summonerId,
			@RequestParam(defaultValue = "0", name = "start") int start,
			@RequestParam(defaultValue = "100", name = "count") int count){
		
		List<String> matchIds = restApiService.getMatchIds(summonerId, start, count);
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Link", "http://localhost:8080/docs/matchids.html; rel=\"profile\"");

		return OpenApiResponseDtoFactory.getResponseEntity(headers, matchIds);
	}
	
	@GetMapping("/match/{matchid}")
	ResponseEntity<MatchDto> getOneMatch(@PathVariable("matchid") String matchId){
		MatchDto match = restApiService.getMatch(matchId);
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Link", "http://localhost:8080/docs/match.html; rel=\"profile\"");

		return OpenApiResponseDtoFactory.getResponseEntity(headers, match);
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
