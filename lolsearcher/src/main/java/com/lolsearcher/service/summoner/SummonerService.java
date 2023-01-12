package com.lolsearcher.service.summoner;

import java.util.List;

import com.lolsearcher.annotation.transaction.jpa.JpaTransactional;
import com.lolsearcher.model.factory.EntityFactory;
import com.lolsearcher.model.factory.FrontServerResponseDtoFactory;
import com.lolsearcher.model.request.front.RequestSummonerDto;
import com.lolsearcher.model.request.riot.summoner.RiotGamesSummonerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.lolsearcher.api.riotgames.RiotGamesAPI;
import com.lolsearcher.model.response.front.summoner.SummonerDto;
import com.lolsearcher.model.entity.summoner.Summoner;
import com.lolsearcher.repository.summoner.SummonerRepository;

import static com.lolsearcher.constant.LolSearcherConstants.SUMMONER_RENEW_MS;

@Slf4j
@RequiredArgsConstructor
@Service
public class SummonerService {
	
	private final RiotGamesAPI riotApi;
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
			dbSummoner = updateIncorrectNameSummoners(dbSummoners, summonerName);

			if(dbSummoner != null){
				return FrontServerResponseDtoFactory.getSummonerDto(dbSummoner, true);
			}
		}

		if(dbSummoner == null || (requestRenew &&
				System.currentTimeMillis() - dbSummoner.getLastRenewTimeStamp() >= SUMMONER_RENEW_MS)){

			renewed = true;
			try{
				RiotGamesSummonerDto apiSummonerDto = riotApi.getSummonerByName(summonerName);
				Summoner apiSummoner = EntityFactory.getSummonerFromRestApiDto(apiSummonerDto);

				if(dbSummoner == null){
					summonerRepository.saveSummoner(apiSummoner);
					dbSummoner = apiSummoner;
				}else {
					summonerRepository.updateSummoner(dbSummoner, apiSummoner);
				}
			}catch (WebClientResponseException e){
				log.error(e.getMessage());

				if(e.getStatusCode() == HttpStatus.BAD_REQUEST && dbSummoner != null) { //dbSummoner의 닉네임에 대한 실제 유저가 존재하지 않는 경우
					updateIncorrectNameSummoner(dbSummoner);
				}
				throw e; /* 갱신된 값은 클라이언트가 요청한 닉네임이 아니기 때문에 갱신만 하고 예외를 발생시킴 */
			}
		}
		return FrontServerResponseDtoFactory.getSummonerDto(dbSummoner, renewed);
	}

	@JpaTransactional(propagation = Propagation.REQUIRES_NEW)
	public void rollbackLastMatchId(String summonerId, String beforeLastMatchId) {

		Summoner summoner = summonerRepository.findSummonerById(summonerId);
		summonerRepository.updateSummonerLastMatchId(summoner, beforeLastMatchId);
	}

	private Summoner updateIncorrectNameSummoners(List<Summoner> incorrectSummoners, String wantedSummonerName) {

		Summoner correctSummoner = null;

		for(Summoner incorrectSummoner : incorrectSummoners) {

			Summoner updatedSummoner = updateIncorrectNameSummoner(incorrectSummoner, wantedSummonerName);
			if(updatedSummoner != null){
				correctSummoner = updatedSummoner;
			}
		}
		return correctSummoner;
	}

	private void updateIncorrectNameSummoner(Summoner dbSummoner){
		updateIncorrectNameSummoner(dbSummoner, "NOT_WANTED_NAME");
	}

	private Summoner updateIncorrectNameSummoner(Summoner dbSummoner, String findSummonerName) {

		try {
			RiotGamesSummonerDto apiSummonerDto = riotApi.getSummonerById(dbSummoner.getSummonerId());
			Summoner apiSummoner = EntityFactory.getSummonerFromRestApiDto(apiSummonerDto);

			summonerRepository.updateSummoner(dbSummoner, apiSummoner);
			log.info("소환사 계정 : {} 는 닉네임이 '{}' -> '{}'으로 변경됨",
					dbSummoner.getSummonerId(), dbSummoner.getSummonerName(), apiSummoner.getSummonerName());

			if(apiSummoner.getSummonerName().equals(findSummonerName)){
				return apiSummoner;
			}
		}catch (WebClientResponseException e){
			if(e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				log.error("SummonerId : {} 인 유저는 현재 존재하지 않음.", dbSummoner.getSummonerId());
				summonerRepository.deleteSummoner(dbSummoner);
				log.info("SummonerId : {} 인 유저 DB에서 삭제 완료", dbSummoner.getSummonerId());
			}else {
				log.error(e.getMessage());
			}
		}
		return null;
	}
}
