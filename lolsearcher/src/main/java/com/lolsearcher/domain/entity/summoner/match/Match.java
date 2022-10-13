package com.lolsearcher.domain.entity.summoner.match;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;

@Entity
@Table(name = "MATCHES")
public class Match {

	@Id
	@Column(name = "id")
	private String matchid;
	private long gameDuration;
	private long gameEndTimestamp;
	private int queueId;
	private int season;
	
	@BatchSize(size = 100)
	@OneToMany(mappedBy = "match", cascade = CascadeType.ALL)
	private List<Member> members = new ArrayList<>();
	
	
	public String getMatchId() {
		return matchid;
	}
	public void setMatchId(String matchId) {
		this.matchid = matchId;
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
	public List<Member> getMembers() {
		return members;
	}
	public void setMembers(List<Member> members) {
		this.members = members;
	}
	
	public void newMember(Member member) {
		this.members.add(member);
	}
	public void removeMember(Member member) {
		this.members.remove(member);
	}
	public int getSeason() {
		return season;
	}
	public void setSeason(int season) {
		this.season = season;
	}
	
}
