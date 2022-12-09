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
	
	protected static Summoner getRealSummoner(Summoner summoner) {
		return Summoner.builder()
				.summonerId(summoner.getSummonerId())
				.name("닉네임"+summoner.getSummonerId())
				.build();
	}
	
	protected static Summoner getSummoner(String name) {
		return Summoner.builder()
				.summonerId("id"+name)
				.name(name)
				.lastRenewTimeStamp(System.currentTimeMillis())
				.build();
	}
}
