package com.lolsearcher.repository.restapi;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import com.lolsearcher.model.entity.summoner.Summoner;
import com.lolsearcher.model.entity.match.Match;
import com.lolsearcher.model.entity.rank.Rank;
import com.lolsearcher.model.entity.rank.RankCompKey;

@RequiredArgsConstructor
@Repository
public class JpaRestRepository implements RestRepository {

	private final EntityManager em;
	
	@Override
	public Summoner getSummonerById(String id) {
		
		String jpql = "SELECT s FROM Summoner s WHERE s.id = :id";
		
		return em.createQuery(jpql, Summoner.class)
				.setParameter("id", id)
				.getSingleResult();
	}

	@Override
	public Summoner getSummonerByName(String name) {
		
		String jpql = "SELECT s FROM Summoner s WHERE s.name = :name";
		
		return em.createQuery(jpql, Summoner.class)
				.setParameter("name", name)
				.getSingleResult();
	}

	@Override
	public Rank getRank(String id, String type, int season) {
		
		return em.find(Rank.class, new RankCompKey(id, type, season));
	}

	@Override
	public List<Rank> getRanks(String id, int season) {

		String jpql = "SELECT r FROM Rank r "
				+ "WHERE r.ck.summonerId = :summonerId AND r.ck.seasonId = :seasonId";
		
		return em.createQuery(jpql, Rank.class)
				.setParameter("summonerId", id)
				.setParameter("seasonId", season)
				.getResultList();
	}

	@Override
	public List<String> getMatchIds(String summonerId, int start, int count) {
		
		String jpql = "SELECT m.ck.matchid FROM Member m WHERE m.summonerid = :summonerId ORDER BY m.ck.matchid";
		
		return em.createQuery(jpql, String.class)
				.setParameter("summonerId", summonerId)
				.setFirstResult(start)
				.setMaxResults(count)
				.getResultList();
	}

	@Override
	public Match getMatch(String matchId) {
		
		String jpql = "SELECT DISTINCT m FROM Match m JOIN FETCH m.members "
				+ "WHERE m.matchid = :matchId";
		
		try{
			return em.createQuery(jpql, Match.class)
				.setParameter("matchId", matchId)
				.getSingleResult();
		}catch(NoResultException e) {
			return null;
		}
	}

	@Override
	public void setMatch(Match match) {
		em.persist(match);
	}

}
