package com.lolsearcher.domain.entity.match;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class MemberCompKey implements Serializable {

	private static final long serialVersionUID = 3555370525672043186L;
	
	@Column(name = "MATCH_ID")
	private String matchid;
	
	private int num;
	
	public MemberCompKey() {
		
	}

	public MemberCompKey(String matchid, int num) {
		super();
		this.matchid = matchid;
		this.num = num;
	}

	public String getMatchid() {
		return matchid;
	}

	public void setMatchid(String matchid) {
		this.matchid = matchid;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	@Override
	public int hashCode() {
		return Objects.hash(matchid, num);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MemberCompKey other = (MemberCompKey) obj;
		return Objects.equals(matchid, other.matchid) && num == other.num;
	}
}
