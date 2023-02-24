package com.lolsearcher.unit.service.search.match;

import com.lolsearcher.model.entity.match.Match;
import com.lolsearcher.model.request.search.match.RequestMatchDto;
import com.lolsearcher.model.response.front.search.match.MatchDto;
import com.lolsearcher.model.response.front.search.match.ParticipantDto;
import com.lolsearcher.model.response.front.search.match.TeamDto;
import com.lolsearcher.repository.search.match.MatchRepository;
import com.lolsearcher.service.search.match.MatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class MatchServiceUnitTest {

	@Mock private MatchRepository matchRepository;
	private MatchService matchService;
	
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
		List<MatchDto> result = matchService.getMatchesInDB(request);

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
	}
}
