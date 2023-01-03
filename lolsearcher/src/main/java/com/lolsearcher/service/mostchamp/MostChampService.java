package com.lolsearcher.service.mostchamp;

import java.util.ArrayList;
import java.util.List;

import com.lolsearcher.annotation.transaction.jpa.JpaTransactional;
import com.lolsearcher.model.request.front.RequestMostChampDto;
import com.lolsearcher.repository.mostchamp.MostChampRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.lolsearcher.model.response.front.mostchamp.MostChampDto;

import static com.lolsearcher.constant.LolSearcherConstants.MOST_CHAMP_LIMITED_COUNT;

@RequiredArgsConstructor
@Service
public class MostChampService {
	
	private final MostChampRepository mostChampRepository;

	@JpaTransactional(readOnly = true)
	public List<MostChampDto> getMostChamps(RequestMostChampDto mostChampInfo) {
		String summonerId = mostChampInfo.getSummonerId();
		int queueId = mostChampInfo.getGameQueue();
		int seasonId = mostChampInfo.getSeason();
		
		List<MostChampDto> mostChamps = new ArrayList<>(MOST_CHAMP_LIMITED_COUNT);
		
		List<String> champIds = mostChampRepository.findMostChampionIds(summonerId, queueId, seasonId, MOST_CHAMP_LIMITED_COUNT);

		for(String champId : champIds) {
			MostChampDto champ = mostChampRepository.findMostChampion(summonerId, champId, queueId, seasonId);
			mostChamps.add(champ);
		}
		return mostChamps;
	}
}
