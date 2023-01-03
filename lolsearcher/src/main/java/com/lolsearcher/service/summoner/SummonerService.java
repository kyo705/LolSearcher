package com.lolsearcher.service.summoner;

import java.util.List;

import com.lolsearcher.annotation.transaction.jpa.JpaTransactional;
import com.lolsearcher.model.request.front.RequestSummonerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.lolsearcher.api.riotgames.RiotRestAPI;
import com.lolsearcher.model.response.front.summoner.SummonerDto;
import com.lolsearcher.model.entity.summoner.Summoner;
import com.lolsearcher.repository.summoner.SummonerRepository;

import static com.lolsearcher.constant.LolSearcherConstants.SUMMONER_RENEW_MS;

@Slf4j
@RequiredArgsConstructor
@Service
public class SummonerService {
	
	private final RiotRestAPI riotApi;
	private final SummonerRepository summonerRepository;
	
	@JpaTransactional(noRollbackFor = WebClientResponseException.class)
	public SummonerDto getSummonerDto(RequestSummonerDto summonerInfo) {

		boolean renewed = false; /* 갱신이 되었는지 판단하는 flag => responseDto에 넣어줌 */

		String summonerName = summonerInfo.getSummonerName();
		boolean requestRenew = summonerInfo.isRequestRenew();

		Summoner dbSummoner = null;

		List<Summoner> dbSummoners = summonerRepository.findSummonerByName(summonerName);

		if(dbSummoners.size()==1){
			dbSummoner = dbSummoners.get(0);
		} else if(dbSummoners.size() >= 2){
			dbSummoner = updateIncorrectSummoners(dbSummoners, summonerName);

			if(dbSummoner != null){
				return getSummonerDto(dbSummoner, true);
			}
		}

		if(dbSummoner == null || (requestRenew &&
				System.currentTimeMillis() - dbSummoner.getLastRenewTimeStamp() >= SUMMONER_RENEW_MS)){

			renewed = true;
			try{
				Summoner apiSummoner = riotApi.getSummonerByName(summonerName);

				if(dbSummoner == null){
					summonerRepository.saveSummoner(apiSummoner);
					dbSummoner = apiSummoner;
				}else {
					renewDbSummoner(dbSummoner, apiSummoner);
				}
			}catch (WebClientResponseException e){
				if(e.getStatusCode() == HttpStatus.BAD_REQUEST && dbSummoner != null) {
					//해당 계정 닉네임 업데이트
					Summoner apiSummoner = riotApi.getSummonerById(dbSummoner.getSummonerId());

					renewDbSummoner(dbSummoner, apiSummoner);
					log.info("소환사 계정 : {} 는 닉네임이 '{}' -> '{}'으로 변경됨",
							dbSummoner.getSummonerId(), dbSummoner.getSummonerName(), apiSummoner.getSummonerName());
				}
				log.error(e.getMessage());
				throw e; /* 갱신된 값은 클라이언트가 요청한 닉네임이 아니기 때문에 갱신만 하고 예외를 발생시킴 */
			}
		}

		return getSummonerDto(dbSummoner, renewed);
	}

	@JpaTransactional(propagation = Propagation.REQUIRES_NEW)
	public void rollbackLastMatchId(String summonerId, String beforeLastMatchId) {

		Summoner summoner = summonerRepository.findSummonerById(summonerId);

		summoner.setLastMatchId(beforeLastMatchId);
	}

	private Summoner updateIncorrectSummoners(List<Summoner> incorrectSummoners, String wantedSummonerName) {

		Summoner correctSummoner = null;

		for(Summoner incorrectSummoner : incorrectSummoners) {
			try {
				Summoner renewedSummoner = riotApi.getSummonerById(incorrectSummoner.getSummonerId());
				renewDbSummoner(incorrectSummoner, renewedSummoner);

				if(renewedSummoner.getSummonerName().equals(wantedSummonerName)){
					correctSummoner = renewedSummoner;
				}
			}catch(WebClientResponseException e) {
				if(e.getStatusCode()==HttpStatus.BAD_REQUEST) {
					log.error("'{}' 닉네임에 해당하는 유저는 게임 내에 존재하지 않음", incorrectSummoner.getSummonerName());
					summonerRepository.deleteSummoner(incorrectSummoner);
				}else {
					log.error(e.getMessage());
					throw e;
				}
			}
		}
		return correctSummoner;
	}
	
	
	private void renewDbSummoner(Summoner before, Summoner after) {
		before.setRevisionDate(after.getRevisionDate());
		before.setSummonerName(after.getSummonerName());
		before.setProfileIconId(after.getProfileIconId());
		before.setSummonerLevel(after.getSummonerLevel());
		before.setLastRenewTimeStamp(after.getLastRenewTimeStamp());
	}

	private SummonerDto getSummonerDto(Summoner dbSummoner, boolean renewed) {

		return SummonerDto
				.builder()
				.summonerId(dbSummoner.getSummonerId())
				.puuId(dbSummoner.getPuuid())
				.renewed(renewed)
				.name(dbSummoner.getSummonerName())
				.profileIconId(dbSummoner.getProfileIconId())
				.summonerLevel(dbSummoner.getSummonerLevel())
				.lastRenewTimeStamp(dbSummoner.getLastRenewTimeStamp())
				.build();
	}
}
