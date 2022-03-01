package com.lolsearcher.repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;

import com.lolsearcher.domain.Dto.MostChampBuilder;
import com.lolsearcher.domain.Dto.MostChampDto;
import com.lolsearcher.domain.entity.Match;
import com.lolsearcher.domain.entity.Rank;
import com.lolsearcher.domain.entity.Summoner;

public class JpaSummonerRepository implements SummonerRepository {

	private final EntityManager em;
	
	@Autowired
	public JpaSummonerRepository(EntityManager em) {
		this.em = em;
	}
	
	//-----------------Summoner 테이블 CRUD----------------------------------
	@Override
	public void savesummoner(Summoner summoner) throws EntityExistsException {
		em.persist(summoner);
	}
	
	@Override
	public Summoner findsummonerById(String summonerid){
		Summoner summoner = em.find(Summoner.class, summonerid);
		
		return summoner;
	}
	
	
	@Override
	public void updatesummoner(Summoner apisummoner, Summoner dbsummoner) {
		dbsummoner.setAccountId(apisummoner.getAccountId());
		dbsummoner.setName(apisummoner.getName());
		dbsummoner.setProfileIconId(apisummoner.getProfileIconId());
		dbsummoner.setPuuid(apisummoner.getPuuid());
		dbsummoner.setRevisionDate(apisummoner.getRevisionDate());
		dbsummoner.setSummonerLevel(apisummoner.getSummonerLevel());
	}

	//-----------------Rank 테이블 CRUD----------------------------------

	@Override
	public void saveLeagueEntry(Set<Rank> set) throws EntityExistsException {
		Iterator<Rank> it =  set.iterator();
		while(it.hasNext()) {
			Rank rank = it.next();
			em.persist(rank);
		}
		
	}
	
	@Override
	public Set<Rank> findLeagueEntry(String id) {
		String jpql = "select r from Rank r where r.ck.summonerId = :id";
		List<Rank> list = em.createQuery(jpql, Rank.class).setParameter("id", id).getResultList();
		Set<Rank> set = new HashSet<>();
		for(Rank r : list) {
			set.add(r);
		}
		return set;
	}

	@Override
	public void updateLeagueEntry(Set<Rank> apileague, Set<Rank> dbleague) {
		
		Iterator<Rank> it1 = dbleague.iterator();
		Iterator<Rank> it2 = apileague.iterator();
		while(it1.hasNext()) {
			Rank rank1 = it1.next();
			while(it2.hasNext()) {
				Rank rank2 = it2.next();
				if(rank1.getCk().equals(rank2.getCk())) {
					rank1.setLeagueId(rank2.getLeagueId());
					rank1.setLeaguePoints(rank2.getLeaguePoints());
					rank1.setRank(rank2.getRank());
					rank1.setTier(rank2.getTier());
					rank1.setWins(rank2.getWins());
					rank1.setLosses(rank2.getLosses());
				}
			}
		}
	}

	//-----------------Match,Member 테이블 CRUD----------------------------------
	
	@Override
	public boolean findMatchid(String matchid) {
		if(em.find(Match.class, matchid)==null) {
			return false;
		}
		return true;
	}
	
	@Override
	public void saveMatch(Match match) throws EntityExistsException{
		
		//match 엔티티 영속성 컨텍스트에 저장. 연관된 member 엔티티들도 다 저장됨.
		em.persist(match);
		
	}
	
	@Override
	public List<String> findMatchList(String summonerid, int gametype, String champion, int count) {
		
		if(gametype==-1) {
			if(champion.equals("all")) {
				String jpql = "select distinct m.matchid from Match m join m.members k "
						+ "where k.ck.summonerid = :summonerid "
						+ "ORDER BY m.matchid DESC";
				
				return em.createQuery(jpql, String.class).setParameter("summonerid", summonerid).getResultList();
			}else {
				String jpql = "select distinct m.matchid from Match m join m.members k "
						+ "where k.ck.summonerid = :summonerid and m.championid = :champion "
						+ "ORDER BY m.matchid DESC";
				
				return em.createQuery(jpql, String.class).setParameter("summonerid", summonerid)
						.setParameter("champion", champion).getResultList();
			}
		}else {
			if(champion.equals("all")) {
				String jpql = "select distinct m.matchid from Match m join m.members k "
						+ "where k.ck.summonerid = :summonerid and m.queueId = :gametype "
						+ "ORDER BY m.matchid DESC";
				//m.members.ck.summonerid = :summonerid and .setParameter("summonerid", summonerid)
				
				List<String> lists = em.createQuery(jpql,String.class).setParameter("summonerid", summonerid)
						.setParameter("gametype", gametype).getResultList();
				
				return lists;
			}else {
				String jpql = "select distinct m.matchid from Match m join fetch m.members "
						+ "where m.members.ck.summonerid = :summonerid and m.championid = :champion and m.gametype = :gametype "
						+ "ORDER BY m.matchid DESC";
				return em.createQuery(jpql, String.class).setParameter("summonerid", summonerid)
						.setParameter("champion", champion).setParameter("gametype", gametype).getResultList();
			}
		}
	}
	
	@Override
	public Match findMatch(String matchid) {
		String jpql = "select distinct m from Match m join fetch m.members where m.matchid = :matchid";
		
		return em.createQuery(jpql, Match.class).setParameter("matchid", matchid).getSingleResult();
	}
	
	@Override
	public List<String> findMostchampids(String summonerid, int queue, int season) {
		List<String> champids = new ArrayList<>();
		String jpql;
		List results; //집계함수(ex. count(),avg() ...)를 통해 받는 값은 long type임. jpa에서 그렇게 제공해줌
		if(queue==-1) {
			jpql = "select m.championid, COUNT(m.championid) AS c from Member m "
					+ "where m.ck.summonerid = :summonerid and m.match.season = :season "
					+ "GROUP BY m.championid ORDER BY c DESC";
			
			results = em.createQuery(jpql).
					setParameter("summonerid", summonerid).setParameter("season", season)
					.getResultList();
		}else {
			jpql = "select m.championid, COUNT(m.championid) AS c from Member m "
					+ "where m.ck.summonerid = :summonerid and m.match.queueId = :queue and m.match.season = :season "
					+ "GROUP BY m.championid ORDER BY c DESC";
			
			results = em.createQuery(jpql).
					setParameter("summonerid", summonerid).setParameter("queue", queue)
					.setParameter("season", season).getResultList();
		}
		
		int i = 0;
		for(Object result : results) {
			if(i>=5) 
				break;
			
			Object[] obj = (Object[])result;
			champids.add((String)obj[0]);
			i++;
		}
		
		return champids;
	}

	@Override
	public MostChampDto findChamp(String summonerid, String champid, int queue, int season) {
		
		String jpql;
		Object result;
		
		if(queue==-1) {
			jpql = "select avg(m.cs), avg(m.kills),avg(m.deaths), avg(m.assists),count(m.championid)"
					+ " as w from Member m "
					+ "where m.championid = :championid and m.ck.summonerid = :summonerid and m.match.season = :season "
					+ "group by m.championid";
			//(select count(a.win) from Member a where a.wins=:win group by a.wins) .setParameter("win", true) .setTotalwin((int)o[5]
			result = em.createQuery(jpql).setParameter("summonerid", summonerid)
					.setParameter("championid", champid).setParameter("season", season).getSingleResult();
		}else {
			jpql = "select avg(m.cs), avg(m.kills),avg(m.deaths), avg(m.assists),count(m.championid)"
					+ " as w from Member m "
					+ "where m.championid = :championid and m.ck.summonerid = :summonerid and m.match.queueId = :queue and "
					+ "m.match.season = :season group by m.championid";
			
			result = em.createQuery(jpql)
					.setParameter("summonerid", summonerid).setParameter("championid", champid)
					.setParameter("queue", queue).setParameter("season", season).getSingleResult();
		}
		
		Object[] o = (Object[])result;
		MostChampDto champ = new MostChampBuilder().setAvgcs((double)o[0]).setAvgkill((double)o[1])
				.setAvgdeath((double)o[2]).setAvgassist((double)o[3]).setTotalgame((long)o[4])
				.setChampionid(champid).build();
		
		return champ;
	}
}
