package com.lolsearcher.search.summoner;

import com.lolsearcher.annotation.transaction.JpaTransactional;
import com.lolsearcher.errors.exception.summoner.NotExistedSummonerInDBException;
import com.lolsearcher.errors.exception.summoner.NotExistedSummonerInGameServerException;
import com.lolsearcher.utils.ResponseDtoFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.lolsearcher.utils.ResponseDtoFactory.getSummonerDto;

@Slf4j
@RequiredArgsConstructor
@Service
public class SummonerService {

    private final SummonerAPI summonerApi;
    private final SummonerRepository summonerRepository;

    @JpaTransactional(readOnly = true)
    public SummonerDto findByName(String name){

        List<Summoner> summoners = summonerRepository.findByName(name);

        if(summoners.size() == 0){
            log.info("닉네임 '{}' 유저 정보는 현재 DB에 존재하지 않습니다.", name);
            throw new NotExistedSummonerInDBException(name);
        }
        if(summoners.size() == 1){
            return getSummonerDto(summoners.get(0));
        }
        List<String> summonerIds = summoners.stream()
                .map(Summoner::getSummonerId)
                .collect(Collectors.toList());

        return summonerApi
                .updateSameNameSummoners(summonerIds)
                .map(ResponseDtoFactory::getSummonerDto)
                .orElseThrow(() -> new NotExistedSummonerInGameServerException(name));
    }

    @JpaTransactional(readOnly = true)
    public SummonerDto findById(String id) {

        return summonerRepository.findById(id)
                .map(ResponseDtoFactory::getSummonerDto)
                .orElseThrow(() -> new EmptyResultDataAccessException(
                        String.format("summonerId : %s 는 존재하지 않는 사용자입니다.", id), 1));
    }
}
