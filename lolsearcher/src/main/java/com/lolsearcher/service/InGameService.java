package com.lolsearcher.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.lolsearcher.domain.Dto.ingame.InGameDto;
import com.lolsearcher.domain.Dto.summoner.SummonerDto;
import com.lolsearcher.domain.entity.ingame.InGame;
import com.lolsearcher.domain.entity.summoner.Summoner;
import com.lolsearcher.repository.SummonerRepository.SummonerRepository;
import com.lolsearcher.repository.ingamerepository.IngameRepository;
import com.lolsearcher.restapi.RiotRestAPI;

@Service
@Transactional
public class InGameService {
	
	private final RiotRestAPI riotApi;
	private final IngameRepository ingameRepository;
	private final SummonerRepository summonerRepository;
	
	@Autowired
	public InGameService(RiotRestAPI riotApi,
			IngameRepository ingameRepository,
			SummonerRepository summonerRepository) {
		
		this.riotApi = riotApi;
		this.ingameRepository = ingameRepository;
		this.summonerRepository = summonerRepository;
	}
	
	public InGameDto getInGame(SummonerDto summonerDto) throws WebClientResponseException {
		
		InGameDto ingameDto = null;
		
		Summoner summoner = summonerRepository.findSummonerById(summonerDto.getSummonerid());
		
		if(System.currentTimeMillis() - summoner.getLastInGameSearchTimeStamp() > 1000*60*2L) {
			summoner.setLastInGameSearchTimeStamp(System.currentTimeMillis());
			
			ingameDto = riotApi.getInGameBySummonerId(summoner.getId());
			
			InGame ingame = new InGame();
			ingame.changeDtoToEntity(ingameDto);
			
			ingameRepository.saveIngame(ingame);
		}else {
			List<InGame> ingames = ingameRepository.getIngame(summoner.getId());
			if(ingames.size()>0)
				ingameDto = new InGameDto(ingames.get(0));
		}
		
		return ingameDto;
	}

	public void removeDirtyInGame(String summonerid, long gameId) {
		
		List<InGame> ingames = ingameRepository.getIngame(summonerid);
		
		for(InGame ingame : ingames) {
			if(ingame.getGameId()!=gameId) {
				try {
					ingameRepository.deleteIngame(ingame);
				}catch(Exception e) {
					continue;
				}
			}
		}
	}
	
}
