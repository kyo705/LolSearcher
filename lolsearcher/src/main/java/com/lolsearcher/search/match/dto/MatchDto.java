package com.lolsearcher.search.match.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static com.lolsearcher.search.match.MatchConstant.THE_NUMBER_OF_MEMBER;

@Getter
@Setter
public class MatchDto {

	private String matchId;
	private LocalTime gameDuration;
	private LocalDateTime gameEndTimestamp;
	private int queueId;
	private int seasonId;
	private String version;
	private final List<SummaryMemberDto> summaryMember = new ArrayList<>(THE_NUMBER_OF_MEMBER);
}
