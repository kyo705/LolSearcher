package com.lolsearcher.service.search.mostchamp;

import com.lolsearcher.annotation.transaction.jpa.JpaTransactional;
import com.lolsearcher.constant.enumeration.GameType;
import com.lolsearcher.model.entity.mostchamp.MostChampStat;
import com.lolsearcher.model.factory.FrontServerResponseDtoFactory;
import com.lolsearcher.model.request.RequestMostChampDto;
import com.lolsearcher.model.response.front.mostchamp.ResponseMostChampDto;
import com.lolsearcher.repository.search.mostchamp.MostChampRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.lolsearcher.constant.LolSearcherConstants.MOST_CHAMP_LIMITED_COUNT;

@RequiredArgsConstructor
@Service
public class MostChampService {
	
	private final MostChampRepository mostChampRepository;

	@JpaTransactional(readOnly = true)
	public List<ResponseMostChampDto> getMostChamps(RequestMostChampDto mostChampInfo) {

		String summonerId = mostChampInfo.getSummonerId();
		int queueId = mostChampInfo.getQueueId();
		int seasonId = mostChampInfo.getSeasonId();

		List<MostChampStat> mostChampStats;

		if(queueId == GameType.ALL_QUEUE_ID.getQueueId()){
			mostChampStats = mostChampRepository.findMostChampions(summonerId, seasonId, MOST_CHAMP_LIMITED_COUNT);
		}else{
			mostChampStats = mostChampRepository.findMostChampions(summonerId, seasonId, queueId, MOST_CHAMP_LIMITED_COUNT);
		}

		return mostChampStats
				.stream()
				.map(FrontServerResponseDtoFactory::getResponseMostChampDto)
				.collect(Collectors.toList());
	}
}
