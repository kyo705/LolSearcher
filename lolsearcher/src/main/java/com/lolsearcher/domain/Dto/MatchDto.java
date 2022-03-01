package com.lolsearcher.domain.Dto;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.lolsearcher.domain.entity.Match;
import com.lolsearcher.domain.entity.Member;

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
		this.members = entitytodto(match.getMembers());
		this.queueId = match.getQueueId();
		this.season = match.getSeason();
	}
	
	public MemberDto[] entitytodto(List<Member> list) {
		MemberDto[] array = new MemberDto[10];
		Iterator<Member> iter =  list.iterator();
		
		boolean plag1 = true;
		boolean plag2 = true;
		List<Member> errormembers1 = new ArrayList<>();
		List<Member> errormembers2 = new ArrayList<>();
		
		//포지션에 맞게 member 정리 ex) top->jungle->middle->botton->support
		while(iter.hasNext()) {
			Member member = iter.next();
			
			if(member.getTeam()==100) {
				if(member.getPositions().equals("TOP")) {
					if(array[0]==null) {
						array[0] = new MemberDto(member);
					}else {
						plag1 = false;
						errormembers1.add(member);
					}
				}else if(member.getPositions().equals("JUNGLE")) {
					if(array[1]==null) {
						array[1] = new MemberDto(member);
					}else {
						plag1 = false;
						errormembers1.add(member);
					}
				}else if(member.getPositions().equals("MIDDLE")) {
					if(array[2]==null) {
						array[2] = new MemberDto(member);
					}else {
						plag1 = false;
						errormembers1.add(member);
					}
				}else if(member.getPositions().equals("BOTTOM")) {
					if(array[3]==null) {
						array[3] = new MemberDto(member);
					}else {
						plag1 = false;
						errormembers1.add(member);
					}
				}else {
					if(array[4]==null) {
						array[4] = new MemberDto(member);
					}else {
						plag1 = false;
						errormembers1.add(member);
					}
				}
			}else {
				if(member.getPositions().equals("TOP")) {
					if(array[5]==null) {
						array[5] = new MemberDto(member);
					}else {
						plag2 = false;
						errormembers2.add(member);
					}
				}else if(member.getPositions().equals("JUNGLE")) {
					if(array[6]==null) {
						array[6] = new MemberDto(member);
					}else {
						plag2 = false;
						errormembers2.add(member);
					}
				}else if(member.getPositions().equals("MIDDLE")) {
					if(array[7]==null) {
						array[7] = new MemberDto(member);
					}else {
						plag2 = false;
						errormembers2.add(member);
					}
				}else if(member.getPositions().equals("BOTTOM")) {
					if(array[8]==null) {
						array[8] = new MemberDto(member);
					}else {
						plag2 = false;
						errormembers2.add(member);
					}
				}else {
					if(array[9]==null) {
						array[9] = new MemberDto(member);
					}else {
						plag2 = false;
						errormembers2.add(member);
					}
				}
			}
		}
		//라이엇 서버에서 포지션 제공이 오류났을 때 
		if(plag1==false) {
			Iterator<Member> itermember =  errormembers1.iterator();
			while(itermember.hasNext()) {
				Member member = itermember.next();
				
				for(int i=0;i<5;i++) {
					if(array[i]==null) {
						array[i]= new MemberDto(member);
						break;
					}
				}
			}
		}
		
		if(plag2==false) {
			Iterator<Member> itermember =  errormembers2.iterator();
			while(itermember.hasNext()) {
				Member member = itermember.next();
				
				for(int i=5;i<10;i++) {
					if(array[i]==null) {
						array[i]= new MemberDto(member);
						break;
					}
				}
			}
		}
		
		return array;
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

	public void setMembers(MemberDto[] members) {
		this.members = members;
	}
}
