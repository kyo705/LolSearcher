package com.lolsearcher.domain.Dto;

import com.lolsearcher.domain.entity.Member;

public class MemberDto {

	private String summonerid;
	private String matchid;
	private String puuid;
	private String name;
	private String championid;
	private String positions;
	private boolean wins;
	
	private int team;
	private int champLevel;
	private int cs;
	private int gold;
	private int bountylevel;
	
	private int kills;
	private int deaths;
	private int assists;
	
	private int visionscore;
	private int detectorwardplaced;
	private int wardkill;
	private int wardpalced;
	private int visionWardsBoughtInGame;
	
	private int baronkills;
	private int dragonkills;
	private int inhibitorkills; //¾ïÁ¦±â Á¦°Å È½¼ö
	private int nexuskills;     //³Ø¼­½º Á¦°Å È½¼ö
	
	private int doublekills;
	private int triplekills;
	private int quadrakills;
	private int pentakills;
	
	private int item0;
	private int item1;
	private int item2;
	private int item3;
	private int item4;
	private int item5;
	private int item6;
	
	public MemberDto() {
		
	}
	
	public MemberDto(Member member) {
		
		this.matchid = member.getCk().getMatchid();
		this.summonerid = member.getCk().getSummonerid();
		this.puuid = member.getPuuid();
		this.name = member.getName();
		this.championid = member.getChampionid();
		this.positions = member.getPositions();
		this.wins = member.getWins();
		
		this.team = member.getTeam();
		this.champLevel = member.getChampLevel();
		this.cs = member.getCs();
		this.gold = member.getGold();
		this.bountylevel = member.getBountylevel();
		
		this.item0 = member.getItem0();
		this.item1 = member.getItem1();
		this.item2 = member.getItem2();
		this.item3 = member.getItem3();
		this.item4 = member.getItem4();
		this.item5 = member.getItem5();
		this.item6 = member.getItem6();
		
		this.kills = member.getKills();
		this.deaths = member.getDeaths();
		this.assists = member.getAssists();
		
		this.baronkills = member.getBaronkills();
		this.dragonkills = member.getDragonkills();
		this.inhibitorkills = member.getInhibitorkills();
		this.nexuskills = member.getNexuskills();
		
		this.doublekills = member.getDoublekills();
		this.triplekills = member.getTriplekills();
		this.quadrakills = member.getQuadrakills();
		this.pentakills = member.getPentakills();
		
		this.setVisionWardsBoughtInGame(member.getVisionWardsBoughtInGame());
		this.visionscore = member.getVisionscore();
		this.detectorwardplaced = member.getDetectorwardplaced();
		this.wardkill = member.getWardkill();
		this.wardpalced = member.getWardpalced();
	}

	public String getSummonerid() {
		return summonerid;
	}

	public void setSummonerid(String summonerid) {
		this.summonerid = summonerid;
	}

	public String getMatchid() {
		return matchid;
	}

	public void setMatchid(String matchid) {
		this.matchid = matchid;
	}

	public String getPuuid() {
		return puuid;
	}

	public void setPuuid(String puuid) {
		this.puuid = puuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getChampionid() {
		return championid;
	}

	public void setChampionid(String championid) {
		this.championid = championid;
	}

	public String getPositions() {
		return positions;
	}

	public void setPositions(String positions) {
		this.positions = positions;
	}

	public boolean isWins() {
		return wins;
	}

	public void setWins(boolean wins) {
		this.wins = wins;
	}

	public int getChampLevel() {
		return champLevel;
	}

	public void setChampLevel(int champLevel) {
		this.champLevel = champLevel;
	}

	public int getCs() {
		return cs;
	}

	public void setCs(int cs) {
		this.cs = cs;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public int getBountylevel() {
		return bountylevel;
	}

	public void setBountylevel(int bountylevel) {
		this.bountylevel = bountylevel;
	}

	public int getKills() {
		return kills;
	}

	public void setKills(int kills) {
		this.kills = kills;
	}

	public int getDeaths() {
		return deaths;
	}

	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}

	public int getAssists() {
		return assists;
	}

	public void setAssists(int assists) {
		this.assists = assists;
	}

	public int getVisionscore() {
		return visionscore;
	}

	public void setVisionscore(int visionscore) {
		this.visionscore = visionscore;
	}

	public int getDetectorwardplaced() {
		return detectorwardplaced;
	}

	public void setDetectorwardplaced(int detectorwardplaced) {
		this.detectorwardplaced = detectorwardplaced;
	}

	public int getWardkill() {
		return wardkill;
	}

	public void setWardkill(int wardkill) {
		this.wardkill = wardkill;
	}

	public int getWardpalced() {
		return wardpalced;
	}

	public void setWardpalced(int wardpalced) {
		this.wardpalced = wardpalced;
	}

	public int getBaronkills() {
		return baronkills;
	}

	public void setBaronkills(int baronkills) {
		this.baronkills = baronkills;
	}

	public int getDragonkills() {
		return dragonkills;
	}

	public void setDragonkills(int dragonkills) {
		this.dragonkills = dragonkills;
	}

	public int getInhibitorkills() {
		return inhibitorkills;
	}

	public void setInhibitorkills(int inhibitorkills) {
		this.inhibitorkills = inhibitorkills;
	}

	public int getNexuskills() {
		return nexuskills;
	}

	public void setNexuskills(int nexuskills) {
		this.nexuskills = nexuskills;
	}

	public int getDoublekills() {
		return doublekills;
	}

	public void setDoublekills(int doublekills) {
		this.doublekills = doublekills;
	}

	public int getTriplekills() {
		return triplekills;
	}

	public void setTriplekills(int triplekills) {
		this.triplekills = triplekills;
	}

	public int getQuadrakills() {
		return quadrakills;
	}

	public void setQuadrakills(int quadrakills) {
		this.quadrakills = quadrakills;
	}

	public int getPentakills() {
		return pentakills;
	}

	public void setPentakills(int pentakills) {
		this.pentakills = pentakills;
	}

	public int getItem0() {
		return item0;
	}

	public void setItem0(int item0) {
		this.item0 = item0;
	}

	public int getItem1() {
		return item1;
	}

	public void setItem1(int item1) {
		this.item1 = item1;
	}

	public int getItem2() {
		return item2;
	}

	public void setItem2(int item2) {
		this.item2 = item2;
	}

	public int getItem3() {
		return item3;
	}

	public void setItem3(int item3) {
		this.item3 = item3;
	}

	public int getItem4() {
		return item4;
	}

	public void setItem4(int item4) {
		this.item4 = item4;
	}

	public int getItem5() {
		return item5;
	}

	public void setItem5(int item5) {
		this.item5 = item5;
	}

	public int getItem6() {
		return item6;
	}

	public void setItem6(int item6) {
		this.item6 = item6;
	}

	public int getVisionWardsBoughtInGame() {
		return visionWardsBoughtInGame;
	}

	public void setVisionWardsBoughtInGame(int visionWardsBoughtInGame) {
		this.visionWardsBoughtInGame = visionWardsBoughtInGame;
	}

	public int getTeam() {
		return team;
	}

	public void setTeam(int team) {
		this.team = team;
	}

}
