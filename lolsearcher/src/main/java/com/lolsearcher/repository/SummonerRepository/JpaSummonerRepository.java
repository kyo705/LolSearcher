package com.lolsearcher.repository.SummonerRepository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import com.lolsearcher.domain.Dto.summoner.MostChampBuilder;
import com.lolsearcher.domain.Dto.summoner.MostChampDto;
import com.lolsearcher.domain.entity.Summoner;
import com.lolsearcher.domain.entity.match.Match;
import com.lolsearcher.domain.entity.rank.Rank;

@Repository
public class JpaSummonerRepository implements SummonerRepository {

	private final EntityManager em;
	
	@Autowired
	public JpaSummonerRepository(EntityManager em) {
		this.em = em;
	}
	
	//-----------------Summoner ���̺� CRUD----------------------------------
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
		String jpql = "SELECT s FROM Summoner s WHERE s.name = :summonerName";
		
		return em.createQuery(jpql, Summoner.class)
				.setParameter("summonerName", summonername)
				.getResultList();
	}

	@Override
	public void deleteSummoner(Summoner summoner) {
		String jpql = "DELETE s FROM Summoner WHERE s.id = :id";
		
		em.createQuery(jpql)
		.setParameter("id", summoner.getId())
		.executeUpdate();
	}

	//-----------------Rank ���̺� CRUD----------------------------------

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

	//-----------------Match,Member ���̺� CRUD----------------------------------
	
	@Override
	public boolean findMatchid(String matchid) {
		if(em.find(Match.class, matchid)==null) {
			return false;
		}
		return true;
	}
	
	@Override
	public void saveMatch(Match match) throws DataIntegrityViolationException {
		//match ��ƼƼ ���Ӽ� ���ؽ�Ʈ�� ����. ������ member ��ƼƼ�鵵 �� �����.
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
						+ "ORDER BY m.matchid DESC";
				
				matchList = em.createQuery(jpql, Match.class)
						.setParameter("summonerid", summonerid)
						.getResultList();
			}else {
				jpql = "SELECT DISTINCT m FROM Match m JOIN fetch m.members "
						+ "WHERE m.matchid IN "
						+ "(SELECT DISTINCT t.ck.matchid from Member t "
						+ "WHERE t.summonerid = :summonerid AND t.championid = :championid) "
						+ "ORDER BY m.matchid DESC";
				
				matchList = em.createQuery(jpql, Match.class)
						.setParameter("summonerid", summonerid)
						.setParameter("championid", champion)
						.getResultList();
			}
		}else {
			if(champion.equals("all")) {
				jpql = "SELECT DISTINCT m FROM Match m JOIN fetch m.members "
						+ "WHERE m.queueId = :queueId AND m.matchid IN "
						+ "(SELECT DISTINCT t.ck.matchid from Member t where t.summonerid = :summonerid) "
						+ "ORDER BY m.matchid DESC";
				
				matchList = em.createQuery(jpql, Match.class)
						.setParameter("summonerid", summonerid)
						.setParameter("queueId", gametype)
						.getResultList();
				
			}else {
				jpql = "SELECT DISTINCT m FROM Match m JOIN fetch m.members "
						+ "WHERE m.queueId = :queueId AND m.matchid IN "
						+ "(SELECT DISTINCT t.ck.matchid from Member t "
						+ "WHERE t.championid = :championid AND t.summonerid = :summonerid) "
						+ "ORDER BY m.matchid DESC";
				
				matchList = em.createQuery(jpql, Match.class)
						.setParameter("summonerid", summonerid)
						.setParameter("championid", champion)
						.setParameter("queueId", gametype)
						.getResultList();
			}
		}
		return matchList;
		
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public List<String> findMostchampids(String summonerid, int queue, int season) {
		List<String> champids = new ArrayList<>();
		String jpql;
		List results; //�����Լ�(ex. count(),avg() ...)�� ���� �޴� ���� long type��. jpa���� �׷��� ��������
		if(queue==-1) {
			jpql = "select m.championid, COUNT(m.championid) AS c from Member m "
					+ "where m.summonerid = :summonerid and m.match.season = :season "
					+ "GROUP BY m.championid ORDER BY c DESC";
			
			results = em.createQuery(jpql).
					setParameter("summonerid", summonerid).setParameter("season", season)
					.getResultList();
		}else {
			jpql = "select m.championid, COUNT(m.championid) AS c from Member m "
					+ "where m.summonerid = :summonerid and m.match.queueId = :queue and m.match.season = :season "
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
					+ "where m.championid = :championid and m.summonerid = :summonerid and m.match.season = :season "
					+ "group by m.championid";
			
			result = em.createQuery(jpql).setParameter("summonerid", summonerid)
					.setParameter("championid", champid).setParameter("season", season).getSingleResult();
		}else {
			jpql = "select avg(m.cs), avg(m.kills),avg(m.deaths), avg(m.assists),count(m.championid)"
					+ " as w from Member m "
					+ "where m.championid = :championid and m.summonerid = :summonerid and m.match.queueId = :queue and "
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
