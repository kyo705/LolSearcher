package com.lolsearcher.unit.service.mostchamp;

import com.lolsearcher.model.response.front.mostchamp.MostChampDto;
import com.lolsearcher.model.request.front.RequestMostChampDto;
import org.junit.jupiter.params.provider.Arguments;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class MostChampServiceTestUpSet {

    protected static List<String> getMostChampIds() {
        return List.of("Varus", "Taliyah", "Swain");
    }

    protected static List<MostChampDto> getMostChamps(List<String> mostChampionIds) {
        List<MostChampDto> mostChampDtos = new ArrayList<>(mostChampionIds.size());

        for(int i=0; i<mostChampionIds.size(); i++){
            MostChampDto mostChampDto = MostChampDto.builder()
                    .championId(mostChampionIds.get(i))
                    .avgKill(i*3)
                    .avgAssist(i*10)
                    .avgDeath(i*5)
                    .avgCs(i*80)
                    .totalWinCount(i*50L)
                    .totalGameCount(i*100L)
                    .build();

            mostChampDtos.add(mostChampDto);
        }
        return mostChampDtos;
    }

    protected static Stream<Arguments> getMostChampParameter() {
        return Stream.of(
                Arguments.arguments(
                        RequestMostChampDto.builder()
                                .season(22)
                                .summonerId("summonerId1")
                                .gameQueue(420)
                                .build()
                )
        );
    }
}
