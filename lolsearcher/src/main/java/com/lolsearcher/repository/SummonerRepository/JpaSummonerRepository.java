package com.lolsearcher.repository.SummonerRepository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import com.lolsearcher.domain.Dto.summoner.MostChampDto;
import com.lolsearcher.domain.entity.summoner.Summoner;
import com.lolsearcher.domain.entity.summoner.match.Match;
import com.lolsearcher.domain.entity.summoner.rank.Rank;

@Repository
public class JpaSummonerRepository implements SummonerRepository {

	private final EntityManager em;
	
	@Autowired
	public JpaSummonerRepository(EntityManager em) {
		this.em = em;
	}
	
	//-----------------Summoner 테이블 CRUD----------------------------------
	@Override
	public void saveSummoner(Summoner summoner) throws DataIntegrityViolationException {
		em.merge(summoner);
	}
	
	@Override
	public Summoner findSummonerById(String summonerid){
		return em.find(Summoner.class, summonerid);
	}
	
	@Override
	public List<Summoner> findSummonerByName(String summonername) {
		String jpql = "SELECT s FROM Summoner s WHERE s.name = :summonerName "
				+ "ORDER BY s.lastRenewTimeStamp DESC";
		
		return em.createQuery(jpql, Summoner.class)
				.setParameter("summonerName", summonername)
				.getResultList();
	}

	@Override
	public void deleteSummoner(Summoner summoner) {
		String jpql = "DELETE FROM Summoner s WHERE s.id = :id";
		
		em.createQuery(jpql)
		.setParameter("id", summoner.getId())
		.executeUpdate();
	}

	//-----------------Rank 테이블 CRUD----------------------------------

	@Override
	public void saveLeagueEntry(List<Rank> list) throws DataIntegrityViolationException {
		Iterator<Rank> it =  list.iterator();
		
		while(it.hasNext()) {
			Rank rank = it.next();
			em.merge(rank);
		}
	}
	
	@Override
	public List<Rank> findLeagueEntry(String id, int seasonId) {
		String jpql = "SELECT r FROM Rank r "
				+ "WHERE r.ck.summonerId = :id AND r.ck.seasonId = :seasonId";
		
		return em.createQuery(jpql, Rank.class)
				.setParameter("id", id)
				.setParameter("seasonId", seasonId)
				.getResultList();
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
	public void saveMatch(Match match) throws DataIntegrityViolationException {
		em.persist(match);
	}

	
	@Override
	public List<Match> findMatchList(String summonerid, int gametype, String champion, int count) {
		List<Match> matchList;
		String jpql;
		
		if(gametype==-1) {
			if(champion.equals("all")) {
				jpql = "SELECT DISTINCT m FROM Match m JOIN fetch m.members "
						+ "WHERE m.matchid IN "
						+ "(SELECT DISTINCT t.ck.matchid FROM Member t WHERE t.summonerid = :summonerid) "
						+ "ORDER BY m.gameEndTimestamp DESC";
				
				matchList = em.createQuery(jpql, Match.class)
						.setParameter("summonerid", summonerid)
						.setFirstResult(0)
						.setMaxResults(count)
						.getResultList();
			}else {
				jpql = "SELECT DISTINCT m FROM Match m JOIN fetch m.members "
						+ "WHERE m.matchid IN "
						+ "(SELECT DISTINCT t.ck.matchid from Member t "
						+ "WHERE t.summonerid = :summonerid AND t.championid = :championid) "
						+ "ORDER BY m.gameEndTimestamp DESC";
				
				matchList = em.createQuery(jpql, Match.class)
						.setParameter("summonerid", summonerid)
						.setParameter("championid", champion)
						.setFirstResult(0)
						.setMaxResults(count)
						.getResultList();
			}
		}else {
			if(champion.equals("all")) {
				jpql = "SELECT DISTINCT m FROM Match m JOIN fetch m.members "
						+ "WHERE m.queueId = :queueId AND m.matchid IN "
						+ "(SELECT DISTINCT t.ck.matchid from Member t where t.summonerid = :summonerid) "
						+ "ORDER BY m.gameEndTimestamp DESC";
				
				matchList = em.createQuery(jpql, Match.class)
						.setParameter("summonerid", summonerid)
						.setParameter("queueId", gametype)
						.setFirstResult(0)
						.setMaxResults(count)
						.getResultList();
				
			}else {
				jpql = "SELECT DISTINCT m FROM Match m JOIN fetch m.members "
						+ "WHERE m.queueId = :queueId AND m.matchid IN "
						+ "(SELECT DISTINCT t.ck.matchid from Member t "
						+ "WHERE t.championid = :championid AND t.summonerid = :summonerid) "
						+ "ORDER BY m.gameEndTimestamp DESC";
				
				matchList = em.createQuery(jpql, Match.class)
						.setParameter("summonerid", summonerid)
						.setParameter("championid", champion)
						.setParameter("queueId", gametype)
						.setFirstResult(0)
						.setMaxResults(count)
						.getResultList();
			}
		}
		return matchList;
		
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public List<String> findMostchampids(String summonerid, int queue, int season) {
		final int count = 5;
		
		List<String> champids = new ArrayList<>();
		String jpql;
		List results; //집계함수(ex. count(),avg() ...)를 통해 받는 값은 long type임. jpa에서 그렇게 제공해줌
		if(queue==-1) {
			jpql = "select m.championid, COUNT(m.championid) AS c from Member m "
					+ "where m.summonerid = :summonerid and m.match.season = :season "
					+ "GROUP BY m.championid ORDER BY c DESC";
			
			results = em.createQuery(jpql)
					.setParameter("summonerid", summonerid)
					.setParameter("season", season)
					.setFirstResult(0)
					.setMaxResults(count)
					.getResultList();
		}else {
			jpql = "select m.championid, COUNT(m.championid) AS c from Member m "
					+ "where m.summonerid = :summonerid and m.match.queueId = :queue and m.match.season = :season "
					+ "GROUP BY m.championid ORDER BY c DESC";
			
			results = em.createQuery(jpql)
					.setParameter("summonerid", summonerid)
					.setParameter("queue", queue)
					.setParameter("season", season)
					.setFirstResult(0)
					.setMaxResults(count)
					.getResultList();
		}
		
		for(Object result : results) {
			Object[] obj = (Object[])result;
			champids.add((String)obj[0]);
		}
		
		return champids;
	}

	@SuppressWarnings("unchecked")
	@Override
	public MostChampDto findChamp(String summonerid, String champid, int queue, int season) {
		
		String jpql;
		List<Object> results;
		
		if(queue==-1) {
			jpql = "select avg(m.cs), avg(m.kills),avg(m.deaths), avg(m.assists),count(m), m.wins "
					+ "from Member m "
					+ "where m.championid = :championid and m.summonerid = :summonerid and m.match.season = :season "
					+ "group by m.wins";
			
			results = em.createQuery(jpql)
					.setParameter("summonerid", summonerid)
					.setParameter("championid", champid)
					.setParameter("season", season)
					.getResultList();
		}else {
			jpql = "select avg(m.cs), avg(m.kills),avg(m.deaths), avg(m.assists),count(m), m.wins "
					+ "from Member m "
					+ "where m.championid = :championid and m.summonerid = :summonerid and m.match.queueId = :queue and "
					+ "m.match.season = :season "
					+ "group by m.wins";
			
			results = em.createQuery(jpql)
					.setParameter("summonerid", summonerid)
					.setParameter("championid", champid)
					.setParameter("queue", queue)
					.setParameter("season", season)
					.getResultList();
		}
		
		MostChampDto champ = new MostChampDto();
		
		champ.setChampionid(champid);
		
		for(Object result : results) {
			Object[] o = (Object[])result;
			if(((boolean)o[5])==true) {
				champ.setTotalwin((long)o[4]);
			}
			champ.setAvgcs((double)o[0] + champ.getAvgcs());
			champ.setAvgkill((double)o[1] + champ.getAvgkill());
			champ.setAvgdeath((double)o[2] + champ.getAvgdeath());
			champ.setAvgassist((double)o[3] + champ.getAvgassist());
			champ.setTotalgame((long)o[4] + champ.getTotalgame());
		}
		
		
		return champ;
	}
	
}
