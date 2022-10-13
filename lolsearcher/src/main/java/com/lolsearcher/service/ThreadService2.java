package com.lolsearcher.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.lolsearcher.domain.entity.summoner.match.Match;
import com.lolsearcher.repository.SummonerRepository.SummonerRepository;
import com.lolsearcher.restapi.RiotRestAPI;

@Service
public class ThreadService2 {

	private final SummonerRepository summonerRepository;
	private final RiotRestAPI riotApi;
	
	public ThreadService2(
			SummonerRepository summonerRepository, 
			RiotRestAPI riotApi) {
		
		this.summonerRepository = summonerRepository;
		this.riotApi = riotApi;
	}
	
	@Transactional
	public List<String> findMatch(List<String> matchIds) {
		// TODO Auto-generated method stub
		List<Match> matches = new ArrayList<>();
		List<String> failMatchIds = new ArrayList<>();
		
		for(int i=0; i<matchIds.size();) {
			String matchId = matchIds.get(i);
			
			try {
				Match match = riotApi.getOneMatchByBlocking(matchId);
				matches.add(match);
				i++;
			}catch(WebClientResponseException e1) {
				if(e1.getStatusCode().value()==429) {
					
					for(Match match : matches) {
						try {
							summonerRepository.saveMatch(match);
						}catch(DataIntegrityViolationException e) {
							//중복 데이터 삽입 시 에러 발생 -> 무시하고 다음 데이터 저장하면 됌 
							System.out.println(e.getLocalizedMessage());
						}
					}
					
					failMatchIds.addAll(matchIds.subList(0, i));
					
					return failMatchIds;
				}
			}catch(Exception e2) {
				break;
			}
		}
		
		return failMatchIds;
	}
	
	@Transactional
	public void saveMatches(List<Match> matches) {
		for(Match match : matches) {
			try {
				summonerRepository.saveMatch(match);
			}catch(DataIntegrityViolationException e) {
				//중복 데이터 삽입 시 에러 발생 -> 무시하고 다음 데이터 저장하면 됌 
				System.out.println(e.getLocalizedMessage());
			}
		}
	}
	
	
}
