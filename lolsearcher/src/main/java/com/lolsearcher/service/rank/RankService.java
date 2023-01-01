package com.lolsearcher.service.rank;

import java.util.List;

import com.lolsearcher.annotation.transaction.jpa.JpaTransactional;
import com.lolsearcher.constant.RankConstants;
import com.lolsearcher.repository.rank.RankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.lolsearcher.api.riotgames.RiotRestAPI;
import com.lolsearcher.model.dto.rank.RankDto;
import com.lolsearcher.model.dto.rank.TotalRanks;
import com.lolsearcher.model.entity.rank.Rank;
import com.lolsearcher.model.entity.rank.RankCompKey;

@RequiredArgsConstructor
@Service
public class RankService {
	
	private final RiotRestAPI riotApi;

	private final RankRepository rankRepository;
	
	@JpaTransactional(readOnly = true)
	public TotalRanks getLeague(String summonerId){
		RankCompKey soloRankKey = new RankCompKey(summonerId, RankConstants.SOLO_RANK, RankConstants.SEASON_ID);
		RankCompKey flexRankKey = new RankCompKey(summonerId, RankConstants.FLEX_RANK, RankConstants.SEASON_ID);
		
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
	
	@JpaTransactional
	public TotalRanks setLeague(String summonerId){
		TotalRanks totalRanks = new TotalRanks();
		
		List<Rank> apiRanks = riotApi.getLeague(summonerId);
		for(Rank rank : apiRanks) {
			rankRepository.saveRank(rank);
			
			if(rank.getCk().getQueueType().equals(RankConstants.SOLO_RANK)) {
				totalRanks.setSolorank(new RankDto(rank));
			}else {
				totalRanks.setTeamrank(new RankDto(rank));
			}
		}
		return totalRanks;
	}
}
