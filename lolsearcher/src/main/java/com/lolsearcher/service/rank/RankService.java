package com.lolsearcher.service.rank;

import java.util.List;

import com.lolsearcher.repository.rank.RankRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.lolsearcher.api.riotgames.RiotRestAPI;
import com.lolsearcher.model.dto.rank.RankDto;
import com.lolsearcher.model.dto.rank.TotalRanks;
import com.lolsearcher.model.entity.rank.Rank;
import com.lolsearcher.model.entity.rank.RankCompKey;

@RequiredArgsConstructor
@Service
public class RankService {
	private static final int SEASON_ID = 22;
	private static final String SOLO_RANK = "RANKED_SOLO_5x5";
	private static final String FLEX_RANK = "RANKED_FLEX_SR";
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final RiotRestAPI riotApi;

	private final RankRepository rankRepository;
	
	@Transactional(readOnly = true)
	public TotalRanks getLeague(String summonerId){
		RankCompKey soloRankKey = new RankCompKey(summonerId, SOLO_RANK, SEASON_ID);
		RankCompKey flexRankKey = new RankCompKey(summonerId, FLEX_RANK, SEASON_ID);
		
		Rank soloRank = rankRepository.findRank(soloRankKey);
		Rank flexRank = rankRepository.findRank(flexRankKey);
		
		TotalRanks ranksDto = new TotalRanks();
		if(soloRank!=null) {
			ranksDto.setSolorank(new RankDto(soloRank));
		}
		if(flexRank!=null) {
			ranksDto.setTeamrank(new RankDto(flexRank));
		}
		return ranksDto;
	}
	
	@Transactional
	public TotalRanks setLeague(String summonerId) throws WebClientResponseException, DataIntegrityViolationException {
		TotalRanks totalRanks = new TotalRanks();
		
		List<Rank> apiRanks = riotApi.getLeague(summonerId);
		for(Rank rank : apiRanks) {
			rankRepository.saveRank(rank);
			
			if(rank.getCk().getQueueType().equals(SOLO_RANK)) {
				totalRanks.setSolorank(new RankDto(rank));
			}else {
				totalRanks.setTeamrank(new RankDto(rank));
			}
		}
		return totalRanks;
	}
}
