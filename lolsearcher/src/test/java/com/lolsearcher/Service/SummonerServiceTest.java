package com.lolsearcher.Service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.lolsearcher.domain.Dto.Summoner.SummonerDto;
import com.lolsearcher.service.SummonerService;

@SpringBootTest
@Transactional
public class SummonerServiceTest {

	@Autowired
	SummonerService summonerService;
	SummonerDto summonerdto1;
	
	@Test
	@Rollback
	public void 소환사정보저장() {
		//given
		String summonerName = "도구안락사달인";
		
		//when
		Thread thread = new Thread(()->{
			summonerdto1 = summonerService.setSummoner(summonerName);
		});
		thread.start();
		
		SummonerDto summonerdto2 = summonerService.setSummoner(summonerName);
		
		//then
		assertThat(summonerdto1.getName()).isEqualTo(summonerdto2.getName());
	}
}
