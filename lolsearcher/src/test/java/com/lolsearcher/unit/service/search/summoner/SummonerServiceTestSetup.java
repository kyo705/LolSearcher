package com.lolsearcher.unit.service.search.summoner;

import com.lolsearcher.model.entity.summoner.Summoner;
import com.lolsearcher.model.request.search.summoner.RequestSummonerDto;

import java.util.ArrayList;
import java.util.List;

public class SummonerServiceTestSetup {
	
	protected static List<Summoner> getSameNameSummoners(String name, int size) {

		List<Summoner> summoners = new ArrayList<>();

		for(int i=0;i<size;i++) {
			Summoner summoner = Summoner.builder()
					.summonerId("summonerId"+i)
					.summonerName(name)
					.build();
			
			summoners.add(summoner);
		}
		return summoners;
	}

	protected static RequestSummonerDto getRequestSummonerInfoWithNoRenew() {

		return RequestSummonerDto.builder()
				.summonerName("푸켓푸켓")
				.build();
    }


}
