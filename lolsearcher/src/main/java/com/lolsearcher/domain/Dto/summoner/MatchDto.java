package com.lolsearcher.domain.Dto.summoner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.lolsearcher.domain.entity.match.Match;
import com.lolsearcher.domain.entity.match.Member;


public class MatchDto {

	private String matchid;
	private long gameDuration;
	private long gameEndTimestamp;
	private int queueId;
	private int season;
	private List<MemberDto> members;
	
	public MatchDto() {
		
	}
	
	public MatchDto(Match match) {
		this.gameDuration = match.getGameDuration();
		this.gameEndTimestamp = match.getGameEndTimestamp();
		this.matchid = match.getMatchId();
		this.queueId = match.getQueueId();
		this.season = match.getSeason();
		
		members = new ArrayList<>();
		Iterator<Member> iter = match.getMembers().iterator();
		while(iter.hasNext()) {
			Member member = iter.next();
			members.add(new MemberDto(member));
		}
	}

	public String getMatchid() {
		return matchid;
	}

	public void setMatchid(String matchid) {
		this.matchid = matchid;
	}

	public long getGameDuration() {
		return gameDuration;
	}

	public void setGameDuration(long gameDuration) {
		this.gameDuration = gameDuration;
	}

	public long getGameEndTimestamp() {
		return gameEndTimestamp;
	}

	public void setGameEndTimestamp(long gameEndTimestamp) {
		this.gameEndTimestamp = gameEndTimestamp;
	}

	public int getQueueId() {
		return queueId;
	}

	public void setQueueId(int queueId) {
		this.queueId = queueId;
	}

	public int getSeason() {
		return season;
	}

	public void setSeason(int season) {
		this.season = season;
	}

	public List<MemberDto> getMembers() {
		return members;
	}

}
