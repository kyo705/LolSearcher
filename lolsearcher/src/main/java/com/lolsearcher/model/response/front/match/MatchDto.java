package com.lolsearcher.model.response.front.match;

import java.util.List;

import com.lolsearcher.model.entity.match.Match;
import lombok.Data;

@Data
public class MatchDto {
	private String matchId;
	private long gameDuration;
	private long gameEndTimestamp;
	private int queueId;
	private int seasonId;
	private List<ParticipantDto> members;

	public MatchDto(Match match){
		//Entity를 Model로 변환
		this.matchId = match.getMatchId();
		this.gameDuration = match.getGameDuration();
		this.gameEndTimestamp = match.getGameEndTimestamp();
		this.queueId = match.getQueueId();
		this.seasonId = match.getSeason();
	}
}
