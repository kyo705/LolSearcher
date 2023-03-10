package com.lolsearcher.service.search.rank;

import com.lolsearcher.annotation.transaction.JpaTransactional;
import com.lolsearcher.exception.exception.search.rank.IncorrectSummonerRankSizeException;
import com.lolsearcher.exception.exception.search.rank.NonUniqueRankTypeException;
import com.lolsearcher.model.entity.rank.Rank;
import com.lolsearcher.model.factory.FrontServerResponseDtoFactory;
import com.lolsearcher.model.request.search.rank.RequestRankDto;
import com.lolsearcher.model.response.front.search.rank.RankDto;
import com.lolsearcher.repository.search.rank.RankRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.lolsearcher.constant.LolSearcherConstants.CURRENT_SEASON_ID;
import static com.lolsearcher.constant.LolSearcherConstants.THE_NUMBER_OF_RANK_TYPE;

@Slf4j
@RequiredArgsConstructor
@Service
public class RankService {

	private final RankRepository rankRepository;
	
	@JpaTransactional(readOnly = true)
	public Map<String, RankDto> getOldRanks(RequestRankDto rankInfo){

		String summonerId = rankInfo.getSummonerId();
		List<Rank> ranks = rankRepository.findRanks(summonerId, CURRENT_SEASON_ID);

		Map<String, RankDto> rankDtos = new HashMap<>();

		if(ranks.size() > THE_NUMBER_OF_RANK_TYPE){ /* solo, flex 2가지 랭크 게임 밖에 없음 */
			throw new IncorrectSummonerRankSizeException(ranks.size());
		}

		for(Rank rank : ranks){
			RankDto rankDto = FrontServerResponseDtoFactory.getRankDto(rank);

			if(rankDtos.containsKey(rankDto.getQueueType())){
				throw new NonUniqueRankTypeException(rankDto.getQueueType()); /* 가져온 두 개 이하의 데이터 중 중복타입이 있으면 안됌 */
			}
			rankDtos.put(rankDto.getQueueType(), rankDto);
		}
		return rankDtos;
	}
}
