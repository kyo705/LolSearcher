package com.lolsearcher.unit.service.search.mostchamp;

import com.lolsearcher.constant.enumeration.GameType;
import com.lolsearcher.model.entity.mostchamp.MostChampStat;
import com.lolsearcher.model.request.search.mostchamp.RequestMostChampDto;

import java.util.List;

import static com.lolsearcher.constant.LolSearcherConstants.CURRENT_SEASON_ID;

public class MostChampServiceTestSetup {

    public static RequestMostChampDto getRequestWithAllQueueId() {

        return RequestMostChampDto.builder()
                .summonerId("summonerId1")
                .queueId(GameType.ALL_QUEUE_ID.getQueueId())
                .seasonId(CURRENT_SEASON_ID)
                .build();
    }

    public static RequestMostChampDto getRequestWithSpecificQueueId() {

        return RequestMostChampDto.builder()
                .summonerId("summonerId1")
                .queueId(GameType.SOLO_RANK_MODE.getQueueId())
                .seasonId(CURRENT_SEASON_ID)
                .build();
    }

    public static List<MostChampStat> getMostChampsFromSpecificQueue(RequestMostChampDto request) {

        MostChampStat zedStat = MostChampStat.builder()
                .summonerId(request.getSummonerId())
                .seasonId(request.getSeasonId())
                .championId(1)
                .build();

        MostChampStat talonStat = MostChampStat.builder()
                .summonerId(request.getSummonerId())
                .seasonId(request.getSeasonId())
                .championId(2)
                .build();

        MostChampStat yasuoStat = MostChampStat.builder()
                .summonerId(request.getSummonerId())
                .seasonId(request.getSeasonId())
                .championId(3)
                .build();

        return List.of(zedStat, talonStat, yasuoStat);
    }

    public static List<MostChampStat> getMostChampsFromAllQueue(RequestMostChampDto request) {

        MostChampStat zedStat = MostChampStat.builder()
                .summonerId(request.getSummonerId())
                .seasonId(request.getSeasonId())
                .championId(1)
                .build();

        MostChampStat talonStat = MostChampStat.builder()
                .summonerId(request.getSummonerId())
                .seasonId(request.getSeasonId())
                .championId(2)
                .build();

        MostChampStat yasuoStat = MostChampStat.builder()
                .summonerId(request.getSummonerId())
                .seasonId(request.getSeasonId())
                .championId(3)
                .build();

        return List.of(zedStat, talonStat, yasuoStat);
    }


}
