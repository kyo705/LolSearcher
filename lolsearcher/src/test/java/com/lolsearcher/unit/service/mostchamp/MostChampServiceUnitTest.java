package com.lolsearcher.unit.service.mostchamp;

import static com.lolsearcher.constant.LolSearcherConstants.MOST_CHAMP_LIMITED_COUNT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.lolsearcher.model.dto.parameter.MostChampParam;
import com.lolsearcher.model.dto.mostchamp.MostChampDto;
import com.lolsearcher.repository.mostchamp.MostChampRepository;
import com.lolsearcher.service.mostchamp.MostChampService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class MostChampServiceUnitTest {

    @Mock
    private MostChampRepository mostChampRepository;
	private MostChampService mostChampService;

    @BeforeEach
    void upset() {
        mostChampService = new MostChampService(mostChampRepository);
    }
	//----------------------getMostchamp() 메소드 Test Case------------------------------------
	
		@ParameterizedTest
		@MethodSource("com.lolsearcher.unit.service.mostchamp.MostChampServiceTestUpSet#getMostChampParameter")
		@DisplayName("클라이언트의 요구사항에 적절한 유저의 모스트 챔피언 전적 통계 자료를 DB로 부터 가져오는데 성공하다.")
		void getMostChampBySuccess(MostChampParam mostChampParam) {
			//given
            List<String> mostChampionIds = MostChampServiceTestUpSet.getMostChampIds(mostChampParam);
            List<MostChampDto> mostChamps = MostChampServiceTestUpSet.getMostChamps(mostChampionIds, mostChampParam);

            given(mostChampRepository.findMostChampionIds(
                    mostChampParam.getSummonerId(),
                    mostChampParam.getGameQueue(),
                    mostChampParam.getSeason(),
                    MOST_CHAMP_LIMITED_COUNT)
            ).willReturn(mostChampionIds);

            for(int i=0; i<mostChampionIds.size(); i++){
                given(mostChampRepository.findMostChampion(
                        mostChampParam.getSummonerId(),
                        mostChampionIds.get(i),
                        mostChampParam.getGameQueue(),
                        mostChampParam.getSeason())
                ).willReturn(mostChamps.get(i));
            }

			//when
			List<MostChampDto> resultMostChamps = mostChampService.getMostChamp(mostChampParam);

			//then
            assertThat(resultMostChamps.size()).isEqualTo(mostChampionIds.size());
            for(int i=0; i<resultMostChamps.size(); i++){
                assertThat(resultMostChamps.get(i)).isEqualTo(mostChamps.get(i));
            }
		}
}
