package com.lolsearcher.model.response.front.search.match;

import com.lolsearcher.constant.LolSearcherConstants;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MatchDto {

	private long gameDuration;
	private long gameEndTimestamp;
	private int queueId;
	private int seasonId;
	private String version;
	private final List<TeamDto> teams = new ArrayList<>(LolSearcherConstants.THE_NUMBER_OF_TEAMS);
}
