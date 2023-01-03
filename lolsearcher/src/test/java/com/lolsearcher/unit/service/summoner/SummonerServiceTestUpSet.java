package com.lolsearcher.unit.service.summoner;

import java.util.ArrayList;
import java.util.List;

import com.lolsearcher.model.entity.summoner.Summoner;
import com.lolsearcher.model.request.front.RequestSummonerDto;

import static com.lolsearcher.constant.LolSearcherConstants.SUMMONER_RENEW_MS;

public class SummonerServiceTestUpSet {
	
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
	
	protected static Summoner getSummonerByNameWithRenewedRecently(String name) {
		return Summoner.builder()
				.summonerId("id"+name)
				.summonerName(name)
				.lastRenewTimeStamp(System.currentTimeMillis())
				.build();
	}

	protected static Summoner getSummonerByNameWithNotRenewed(String summonerName) {
		return Summoner.builder()
				.summonerId("id"+summonerName)
				.summonerName(summonerName)
				.lastRenewTimeStamp(System.currentTimeMillis() - SUMMONER_RENEW_MS)
				.build();
	}

	protected static Summoner getSummonerById(String summonerId) {
		return Summoner.builder()
				.summonerId(summonerId)
				.summonerName(summonerId + "닉네임")
				.lastMatchId("renewedLastMatchId")
				.build();
	}

	protected static Summoner changeSummonerName(Summoner summoner, boolean isNotChange) {
		if(isNotChange){
			return summoner;
		}
		summoner.setSummonerName("변화된 닉네임");
		return summoner;
	}

	protected static RequestSummonerDto getRequestSummonerInfoWithNoRenew() {
		return RequestSummonerDto.builder()
				.summonerName("푸켓푸켓")
				.requestRenew(false)
				.build();
    }

	protected static RequestSummonerDto getRequestSummonerInfoWithRenew() {
		return RequestSummonerDto.builder()
				.summonerName("푸켓푸켓")
				.requestRenew(true)
				.build();
	}


}
