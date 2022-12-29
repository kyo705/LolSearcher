package com.lolsearcher.service.mostchamp;

import java.util.ArrayList;
import java.util.List;

import com.lolsearcher.repository.mostchamp.MostChampRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lolsearcher.model.dto.parameter.MostChampParam;
import com.lolsearcher.model.dto.mostchamp.MostChampDto;

@RequiredArgsConstructor
@Service
public class MostChampService {
	
	private final MostChampRepository mostChampRepository;

	@Transactional(readOnly = true)
	public List<MostChampDto> getMostChamp(MostChampParam param) {
		String summonerId = param.getSummonerId();
		int queue = param.getGameQueue();
		int season = param.getSeason();
		
		List<MostChampDto> mostChamps = new ArrayList<>();
		
		List<String> champIds = mostChampRepository.findMostChampionIds(summonerId, queue, season);
		for(String champId : champIds) {
			MostChampDto champ = mostChampRepository.findMostChampion(summonerId, champId, queue, season);
			mostChamps.add(champ);
		}
		return mostChamps;
	}
}
