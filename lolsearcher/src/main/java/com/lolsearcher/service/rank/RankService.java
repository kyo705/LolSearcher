package com.lolsearcher.service.rank;

import java.util.List;

import com.lolsearcher.annotation.transaction.jpa.JpaTransactional;
import com.lolsearcher.repository.rank.RankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.lolsearcher.api.riotgames.RiotRestAPI;
import com.lolsearcher.model.response.front.rank.RankDto;
import com.lolsearcher.model.response.front.rank.TotalRankDtos;
import com.lolsearcher.model.entity.rank.Rank;
import com.lolsearcher.model.entity.rank.RankCompKey;

import static com.lolsearcher.constant.LolSearcherConstants.*;

@RequiredArgsConstructor
@Service
public class RankService {
	
	private final RiotRestAPI riotApi;

	private final RankRepository rankRepository;
	
	@JpaTransactional(readOnly = true)
	public TotalRankDtos getOldRanks(String summonerId){
		RankCompKey soloRankKey = new RankCompKey(summonerId, SOLO_RANK, CURRENT_SEASON_ID);
		RankCompKey flexRankKey = new RankCompKey(summonerId, FLEX_RANK, CURRENT_SEASON_ID);
		
		Rank soloRank = rankRepository.findRank(soloRankKey);
		Rank flexRank = rankRepository.findRank(flexRankKey);
		
		TotalRankDtos ranksDto = new TotalRankDtos();
		if(soloRank!=null) {
			ranksDto.setSolorank(new RankDto(soloRank));
		}
		if(flexRank!=null) {
			ranksDto.setTeamrank(new RankDto(flexRank));
		}
		return ranksDto;
	}
	
	@JpaTransactional
	public TotalRankDtos getRenewRanks(String summonerId){
		TotalRankDtos totalRankDtos = new TotalRankDtos();
		
		List<Rank> apiRanks = riotApi.getLeague(summonerId);
		for(Rank rank : apiRanks) {
			rankRepository.saveRank(rank);
			
			if(rank.getCk().getQueueType().equals(SOLO_RANK)) {
				totalRankDtos.setSolorank(new RankDto(rank));
			}else {
				totalRankDtos.setTeamrank(new RankDto(rank));
			}
		}
		return totalRankDtos;
	}
}
