package com.lolsearcher.unit.service.summoner;

import java.util.ArrayList;
import java.util.List;

import com.lolsearcher.model.entity.summoner.Summoner;

public class SummonerServiceTestUpSet {
	
	protected static List<Summoner> getSameNameSummoners(String name, int size) {
		List<Summoner> summoners = new ArrayList<>();
		for(int i=0;i<size;i++) {
			Summoner summoner = Summoner.builder()
					.summonerId("summonerId"+i)
					.name(name)
					.build();
			
			summoners.add(summoner);
		}
		return summoners;
	}
	
	protected static Summoner getSummonerByName(String name) {
		return Summoner.builder()
				.summonerId("id"+name)
				.name(name)
				.lastRenewTimeStamp(System.currentTimeMillis())
				.build();
	}

	protected static Summoner getSummonerById(String summonerId) {
		return Summoner.builder()
				.summonerId(summonerId)
				.name(summonerId + "닉네임")
				.lastMatchId("renewedLastMatchId")
				.build();
	}

	protected static Summoner changeSummonerName(Summoner summoner, boolean isNotChange) {
		if(isNotChange){
			return summoner;
		}
		summoner.setName("변화된 닉네임");
		return summoner;
	}
}
