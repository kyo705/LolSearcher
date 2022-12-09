package com.lolsearcher.unit.service.mostchamp;

import com.lolsearcher.model.dto.parameter.MostChampParam;
import com.lolsearcher.model.dto.mostchamp.MostChampDto;
import org.junit.jupiter.params.provider.Arguments;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class MostChampServiceTestUpSet {

    protected static List<String> getMostChampIds(MostChampParam mostChampParam) {
        return List.of("Varus", "Taliyah", "Swain");
    }

    protected static List<MostChampDto> getMostChamps(List<String> mostChampionIds, MostChampParam mostChampParam) {
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
                        MostChampParam.builder()
                                .season(22)
                                .summonerId("summonerId1")
                                .gameQueue(420)
                                .build()
                )
        );
    }
}
