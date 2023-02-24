package com.lolsearcher.unit.service.search.mostchamp;

import com.lolsearcher.model.entity.mostchamp.MostChampStat;
import com.lolsearcher.model.request.search.mostchamp.RequestMostChampDto;
import com.lolsearcher.model.response.front.search.mostchamp.ResponseMostChampDto;
import com.lolsearcher.repository.search.mostchamp.MostChampRepository;
import com.lolsearcher.service.search.mostchamp.MostChampService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.lolsearcher.constant.LolSearcherConstants.MOST_CHAMP_LIMITED_COUNT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class MostChampServiceUnitTest {

    @Mock
    private MostChampRepository mostChampRepository;
	private MostChampService mostChampService;

    @BeforeEach
    void upset() {
        mostChampService = new MostChampService(mostChampRepository);
    }

    @Test
    @DisplayName("클라이언트가 특정 유저의 모든 게임 타입의 모스트 챔피언 전적 통계 자료 요청시 해당 데이터를 DB로부터 가져온다.")
    public void getMostChampFromAllQueue() {

        //given
        RequestMostChampDto request = MostChampServiceTestSetup.getRequestWithAllQueueId();
        List<MostChampStat> mostChamps = MostChampServiceTestSetup.getMostChampsFromAllQueue(request);

        given(mostChampRepository.findMostChampions(
                request.getSummonerId(),
                request.getSeasonId(),
                MOST_CHAMP_LIMITED_COUNT)
        ).willReturn(mostChamps);

        //when
        List<ResponseMostChampDto> resultMostChamps = mostChampService.getMostChamps(request);

        //then
        assertThat(resultMostChamps.size()).isLessThanOrEqualTo(MOST_CHAMP_LIMITED_COUNT);
    }

    @Test
    @DisplayName("클라이언트가 특정 유저의 특정 게임 타입의 모스트 챔피언 전적 통계 자료 요청시 해당 데이터를 DB로부터 가져온다.")
    public void getMostChampFromSpecificQueue() {

        //given
        RequestMostChampDto request = MostChampServiceTestSetup.getRequestWithSpecificQueueId();
        List<MostChampStat> mostChamps = MostChampServiceTestSetup.getMostChampsFromSpecificQueue(request);

        given(mostChampRepository.findMostChampions(
                request.getSummonerId(),
                request.getSeasonId(),
                request.getQueueId(),
                MOST_CHAMP_LIMITED_COUNT)
        ).willReturn(mostChamps);

        //when
        List<ResponseMostChampDto> resultMostChamps = mostChampService.getMostChamps(request);

        //then
        assertThat(resultMostChamps.size()).isLessThanOrEqualTo(MOST_CHAMP_LIMITED_COUNT);
    }
}
