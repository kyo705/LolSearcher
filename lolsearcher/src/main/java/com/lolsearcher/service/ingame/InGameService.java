package com.lolsearcher.service.ingame;

import java.util.List;

import com.lolsearcher.exception.ingame.MoreInGameException;
import com.lolsearcher.exception.ingame.NoInGameException;
import com.lolsearcher.exception.summoner.MoreSummonerException;
import com.lolsearcher.exception.summoner.NoSummonerException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.lolsearcher.api.riotgames.RiotRestAPI;
import com.lolsearcher.model.dto.ingame.InGameDto;
import com.lolsearcher.model.entity.ingame.InGame;
import com.lolsearcher.model.entity.summoner.Summoner;
import com.lolsearcher.repository.summoner.SummonerRepository;
import com.lolsearcher.repository.ingame.InGameRepository;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

@RequiredArgsConstructor
@Service
@Transactional
public class InGameService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final RiotRestAPI riotGames;
	private final InGameRepository ingameRepository;
	private final SummonerRepository summonerRepository;
	
	public InGameDto getOldInGame(String summonerId) throws WebClientResponseException {
		logger.info("InGame 기존 데이터 조회");
		List<InGame> inGames = ingameRepository.getInGamesBySummonerId(summonerId);

		if(inGames.size()==0){
			logger.error("인게임 데이터가 현재 DB에 존재하지 않습니다.");
			throw new NoInGameException(1);
		}
		if(inGames.size()>1){
			logger.error("인게임 데이터가 현재 DB에 2 이상 존재합니다.");
			throw new MoreInGameException(1, inGames.size());
		}
		return new InGameDto(inGames.get(0));
	}

	public InGameDto getRenewInGame(String summonerId) throws WebClientResponseException {
		logger.info("InGame 데이터 갱신");

		InGame inGame = riotGames.getInGameBySummonerId(summonerId);
		ingameRepository.saveInGame(inGame);
		try{
			Summoner summoner = summonerRepository.findSummonerById(summonerId);
			summoner.setLastInGameSearchTimeStamp(System.currentTimeMillis());
		}catch (NoResultException e){
			throw new NoSummonerException(1);
		} catch (NonUniqueResultException e){
			throw new MoreSummonerException(1);
		}
		return new InGameDto(inGame);
	}

	public void removeDirtyInGame(String summonerId, long inGameId) {
		List<InGame> inGames = ingameRepository.getInGamesBySummonerId(summonerId);

		for(InGame inGame : inGames) {
			if(inGame.getGameId()!=inGameId) {
				try {
					ingameRepository.deleteInGame(inGame);
				}catch(Exception e) {
					logger.error("GameId : '{}' 삭제 에러", inGame.getGameId());
				}
			}
		}
	}
}
