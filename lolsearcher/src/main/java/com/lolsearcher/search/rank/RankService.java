package com.lolsearcher.search.rank;

import com.lolsearcher.annotation.transaction.JpaTransactional;
import com.lolsearcher.errors.exception.rank.IncorrectSummonerRankSizeException;
import com.lolsearcher.errors.exception.rank.NonUniqueRankTypeException;
import com.lolsearcher.search.summoner.SummonerService;
import com.lolsearcher.utils.ResponseDtoFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.lolsearcher.search.rank.RankConstant.THE_NUMBER_OF_RANK_TYPE;
import static com.lolsearcher.utils.ResponseDtoFactory.getRankDto;

@Slf4j
@RequiredArgsConstructor
@Service
public class RankService {

	private final SummonerService summonerService;
	private final RankRepository rankRepository;
	
	@JpaTransactional(readOnly = true)
	public Map<RankTypeState, RankDto> findAllById(RankRequest request){

		String summonerId = request.getSummonerId();
		int seasonId = request.getSeasonId();

		checkSummonerId(summonerId);
		List<Rank> ranks = rankRepository.findRanks(summonerId, seasonId);

		if(ranks.size() > THE_NUMBER_OF_RANK_TYPE){ /* solo, flex 2가지 랭크 게임 밖에 없음 */
			throw new IncorrectSummonerRankSizeException(ranks.size());
		}
		Map<RankTypeState, RankDto> result = new HashMap<>();
		for(Rank rank : ranks){
			if(result.containsKey(rank.getQueueType())){
				throw new NonUniqueRankTypeException(rank.getQueueType()); /* 가져온 두 개 이하의 데이터 중 중복타입이 있으면 안됌 */
			}
			RankDto rankDto = getRankDto(rank);
			result.put(rankDto.getQueueType(), rankDto);
		}
		return result;
	}

	@JpaTransactional(readOnly = true)
	public Map<RankTypeState, RankDto> findOneById(RankRequest request) {

		String summonerId = request.getSummonerId();
		int seasonId = request.getSeasonId();
		RankTypeState type = request.getRankId();

		checkSummonerId(summonerId);

		return Map.of(type, rankRepository.findRank(summonerId, seasonId, type)
				.map(ResponseDtoFactory::getRankDto)
				.orElseGet( null));
	}

	private void checkSummonerId(String summonerId) {

		summonerService.findById(summonerId);
	}
}
