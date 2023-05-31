package com.lolsearcher.unit.service.search.match;

import com.lolsearcher.search.match.MatchRepository;
import com.lolsearcher.search.match.MatchService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MatchServiceUnitTest {

	@Mock private MatchRepository matchRepository;
	private MatchService matchService;
/*
	@BeforeEach
	void upset() {
		matchService = new MatchService(matchRepository);
	}

	@Test
	@DisplayName("요청 조건에 맞는 Match 데이터를 반환한다.")
	void getMatchesInDB() throws IllegalAccessException {

		//given
		RequestMatchDto request = MatchServiceTestSetup.getRequestMatchDto();
		List<Match> matches = MatchServiceTestSetup.getDBMatches(request);

		given(matchRepository.findMatches(
				request.getSummonerId(), request.getQueueId(), request.getChampionId(), request.getCount()
				)).willReturn(matches);

		//when
		List<MatchDto> result = matchService.findMatches(request);

		//then
		assertThat(result.size()).isLessThanOrEqualTo(request.getCount());
		for(MatchDto match : result){

			assertThat(match.getQueueId()).isEqualTo(request.getQueueId());

			Set<String> set = new HashSet<>();

			for(TeamDto team : match.getTeams()){
				for(ParticipantDto participant : team.getParticipantDtoList()){
					set.add(participant.getSummonerId());
				}
			}
			assertThat(request.getSummonerId()).isIn(set);
		}
	}*/
}
