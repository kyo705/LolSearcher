package com.lolsearcher.domain.Dto.summoner;

import java.util.Iterator;

import com.lolsearcher.domain.entity.summoner.match.Match;
import com.lolsearcher.domain.entity.summoner.match.Member;


public class MatchDto {

	private String matchid;
	private long gameDuration;
	private long gameEndTimestamp;
	private int queueId;
	private int season;
	private MemberDto[] members;
	
	public MatchDto() {
		
	}
	
	public MatchDto(Match match) {
		this.gameDuration = match.getGameDuration();
		this.gameEndTimestamp = match.getGameEndTimestamp();
		this.matchid = match.getMatchId();
		this.queueId = match.getQueueId();
		this.season = match.getSeason();
		
		members = new MemberDto[10];
		Iterator<Member> iter = match.getMembers().iterator();
		
		while(iter.hasNext()) {
			Member member = iter.next();
			members[member.getCk().getNum()] = new MemberDto(member);
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

	public MemberDto[] getMembers() {
		return members;
	}

}
