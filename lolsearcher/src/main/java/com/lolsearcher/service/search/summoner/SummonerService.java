package com.lolsearcher.service.search.summoner;

import com.lolsearcher.annotation.transaction.JpaTransactional;
import com.lolsearcher.api.lolsearcher.ReactiveLolSearcherServerApi;
import com.lolsearcher.exception.exception.summoner.NotExistedSummonerInDBException;
import com.lolsearcher.exception.exception.summoner.NotExistedSummonerInGameServerException;
import com.lolsearcher.model.entity.summoner.Summoner;
import com.lolsearcher.model.factory.FrontServerResponseDtoFactory;
import com.lolsearcher.model.request.search.summoner.RequestSummonerDto;
import com.lolsearcher.model.response.front.search.summoner.SummonerDto;
import com.lolsearcher.repository.search.summoner.SummonerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class SummonerService {

    private final ReactiveLolSearcherServerApi reactiveLolSearcherServerApi;
    private final SummonerRepository summonerRepository;

    @JpaTransactional(readOnly = true)
    public SummonerDto getSummonerDto(RequestSummonerDto request){

        String summonerName = request.getSummonerName();

        List<Summoner> summoners = summonerRepository.findSummonerByName(summonerName);

        if(summoners.size() == 0){
            log.info("닉네임 '{}' 유저 정보는 현재 DB에 존재하지 않습니다.", summonerName);
            throw new NotExistedSummonerInDBException(summonerName); //exception handler 에서 갱신 요청하는 uri로 redirect 하는 로직 설계
        }
        if(summoners.size() == 1){
            return FrontServerResponseDtoFactory.getSummonerDto(summoners.get(0));
        }

        List<String> summonerIds = summoners.stream().map(Summoner::getSummonerId).collect(Collectors.toList());
        Summoner realSummoner = reactiveLolSearcherServerApi.updateSameNameSummoners(summonerIds);

        if(realSummoner == null){
            throw new NotExistedSummonerInGameServerException(summonerName);
        }
        return FrontServerResponseDtoFactory.getSummonerDto(realSummoner);
    }
}
