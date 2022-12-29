package com.lolsearcher.service.summoner;

import java.util.List;

import com.lolsearcher.exception.summoner.MoreSummonerException;
import com.lolsearcher.exception.summoner.NoSummonerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
	
	@Transactional(readOnly = true)
	public SummonerDto findDbSummoner(String summonerName) throws WebClientResponseException {
		List<Summoner> dbSummoners = summonerRepository.findSummonerByName(summonerName);
		
		if(dbSummoners.size()==0) {
			log.error("닉네임 '{}'는 현재 DB에 존재하지 않습니다.", summonerName);
			throw new NoSummonerException(1);
		}
		if(dbSummoners.size()>=2) {
			log.error("닉네임 '{}'는 현재 DB에 2 이상 존재합니다.", summonerName);
			throw new MoreSummonerException(1, dbSummoners.size());
		}
		return new SummonerDto(dbSummoners.get(0));
	}
	
	
	@Transactional(noRollbackFor = WebClientResponseException.class)
	public void updateDbSummoner(String name) throws WebClientResponseException {
		List<Summoner> dbSummoners = summonerRepository.findSummonerByName(name);
		
		for(Summoner dbSummoner : dbSummoners) {
			try {
				Summoner renewedSummoner = riotApi.getSummonerById(dbSummoner.getSummonerId());
				renewSummoner(dbSummoner, renewedSummoner);
			}catch(WebClientResponseException e) {
				if(e.getStatusCode()==HttpStatus.BAD_REQUEST) {
					log.error("'{}' 닉네임에 해당하는 유저는 게임 내에 존재하지 않음", name);
					summonerRepository.deleteSummoner(dbSummoner);
				}else {
					log.error(e.getMessage());
					throw e;
				}
			}
		}
	}
	
	@Transactional
	public SummonerDto renewSummoner(String summonerName) throws WebClientResponseException, DataIntegrityViolationException {
		Summoner apiSummoner = riotApi.getSummonerByName(summonerName);
		try {
			Summoner dbSummoner = summonerRepository.findSummonerById(apiSummoner.getSummonerId());
			renewSummoner(dbSummoner, apiSummoner);

			return new SummonerDto(dbSummoner);

		}catch(EmptyResultDataAccessException e) {
			summonerRepository.saveSummoner(apiSummoner);

			return new SummonerDto(apiSummoner);
		}
	}
	
	
	private void renewSummoner(Summoner before, Summoner after) {
		before.setRevisionDate(after.getRevisionDate());
		before.setName(after.getName());
		before.setProfileIconId(after.getProfileIconId());
		before.setSummonerLevel(after.getSummonerLevel());
		before.setLastRenewTimeStamp(after.getLastRenewTimeStamp());
	}
}
