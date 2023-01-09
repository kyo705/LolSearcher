package com.lolsearcher.service.rank;

import com.lolsearcher.annotation.transaction.jpa.JpaTransactional;
import com.lolsearcher.api.riotgames.RiotGamesAPI;
import com.lolsearcher.exception.rank.IncorrectSummonerRankSizeException;
import com.lolsearcher.exception.rank.NonUniqueRankTypeException;
import com.lolsearcher.model.entity.rank.Rank;
import com.lolsearcher.model.factory.EntityFactory;
import com.lolsearcher.model.factory.ResponseDtoFactory;
import com.lolsearcher.model.request.riot.rank.RiotGamesRankDto;
import com.lolsearcher.model.response.front.rank.RankDto;
import com.lolsearcher.repository.rank.RankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.lolsearcher.constant.LolSearcherConstants.*;

@RequiredArgsConstructor
@Service
public class RankService {
	
	private final RiotGamesAPI riotApi;
	private final RankRepository rankRepository;
	
	@JpaTransactional(readOnly = true)
	public Map<String, RankDto> getOldRanks(String summonerId){
		
		List<Rank> ranks = rankRepository.findRanks(summonerId, CURRENT_SEASON_ID);

		Map<String, RankDto> rankDtos = new HashMap<>();

		if(ranks.size() > THE_NUMBER_OF_RANK_TYPE){ /* solo, flex 2가지 랭크 게임 밖에 없음 */
			throw new IncorrectSummonerRankSizeException(ranks.size());
		}
		for(Rank rank : ranks){
			RankDto rankDto = ResponseDtoFactory.getRankDto(rank);

			if(rankDtos.containsKey(rankDto.getQueueType())){
				throw new NonUniqueRankTypeException(rankDto.getQueueType()); /* 가져온 두 개 이하의 데이터 중 중복타입이 있으면 안됌 */
			}
			rankDtos.put(rankDto.getQueueType(), rankDto);
		}
		return rankDtos;
	}
	
	@JpaTransactional
	public Map<String, RankDto> getRenewRanks(String summonerId){

		Map<String, RankDto> rankDtos = new HashMap<>();

		List<RiotGamesRankDto> apiRankDtos = riotApi.getLeague(summonerId);

		if(apiRankDtos.size() > THE_NUMBER_OF_RANK_TYPE){ /* solo, flex 2가지 랭크 게임 밖에 없음 */
			throw new IncorrectSummonerRankSizeException(apiRankDtos.size());
		}
		for(RiotGamesRankDto apiRankDto : apiRankDtos) {
			Rank apiRank = EntityFactory.getRankFromRestApiDto(apiRankDto);
			rankRepository.saveRank(apiRank);

			RankDto rankDto = ResponseDtoFactory.getRankDto(apiRank);
			if(rankDtos.containsKey(rankDto.getQueueType())){
				throw new NonUniqueRankTypeException(rankDto.getQueueType()); /* 가져온 두 개 이하의 데이터 중 중복타입이 있으면 안됌 */
			}
			rankDtos.put(apiRank.getQueueType(), rankDto);
		}
		return rankDtos;
	}
}
