package com.lolsearcher.service.summoner;

import java.util.List;

import com.lolsearcher.annotation.transaction.jpa.JpaTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.lolsearcher.api.riotgames.RiotRestAPI;
import com.lolsearcher.model.dto.summoner.SummonerDto;
import com.lolsearcher.model.entity.summoner.Summoner;
import com.lolsearcher.repository.summoner.SummonerRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class SummonerService {
	
	private final RiotRestAPI riotApi;
	private final SummonerRepository summonerRepository;
	
	@JpaTransactional(noRollbackFor = WebClientResponseException.class)
	public SummonerDto findOldSummoner(String summonerName) {
		List<Summoner> dbSummoners = summonerRepository.findSummonerByName(summonerName);
		
		if(dbSummoners.size()==0) {
			return null;
		}
		if(dbSummoners.size()>=2) {
			return updateIncorrectSummoners(dbSummoners, summonerName);
		}
		return new SummonerDto(dbSummoners.get(0));
	}
	
	@JpaTransactional
	public SummonerDto findRecentSummoner(String summonerName) {
		Summoner apiSummoner = riotApi.getSummonerByName(summonerName);
		try {
			Summoner dbSummoner = summonerRepository.findSummonerById(apiSummoner.getSummonerId());
			renewDbSummoner(dbSummoner, apiSummoner);

			return new SummonerDto(dbSummoner);

		}catch(EmptyResultDataAccessException e) {
			summonerRepository.saveSummoner(apiSummoner);

			return new SummonerDto(apiSummoner);
		}
	}

	@JpaTransactional(propagation = Propagation.REQUIRES_NEW)
	public void rollbackLastMatchId(String summonerId, String beforeLastMatchId) {

		Summoner summoner = summonerRepository.findSummonerById(summonerId);

		summoner.setLastMatchId(beforeLastMatchId);
	}

	private SummonerDto updateIncorrectSummoners(List<Summoner> incorrectSummoners, String wantedSummonerName) {

		SummonerDto summonerDto = null;

		for(Summoner incorrectSummoner : incorrectSummoners) {
			try {
				Summoner renewedSummoner = riotApi.getSummonerById(incorrectSummoner.getSummonerId());
				renewDbSummoner(incorrectSummoner, renewedSummoner);

				if(renewedSummoner.getName().equals(wantedSummonerName)){
					summonerDto = new SummonerDto(renewedSummoner);
				}
			}catch(WebClientResponseException e) {
				if(e.getStatusCode()==HttpStatus.BAD_REQUEST) {
					log.error("'{}' 닉네임에 해당하는 유저는 게임 내에 존재하지 않음", incorrectSummoner.getName());
					summonerRepository.deleteSummoner(incorrectSummoner);
				}else {
					log.error(e.getMessage());
					throw e;
				}
			}
		}

		return summonerDto;
	}
	
	
	private void renewDbSummoner(Summoner before, Summoner after) {
		before.setRevisionDate(after.getRevisionDate());
		before.setName(after.getName());
		before.setProfileIconId(after.getProfileIconId());
		before.setSummonerLevel(after.getSummonerLevel());
		before.setLastRenewTimeStamp(after.getLastRenewTimeStamp());
	}


}
