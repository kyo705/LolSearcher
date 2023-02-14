package com.lolsearcher.repository.openapi;

import com.lolsearcher.model.entity.match.Match;
import com.lolsearcher.model.entity.rank.Rank;
import com.lolsearcher.model.entity.summoner.Summoner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class JpaRestRepository implements RestRepository {

	private final EntityManager em;
	
	@Override
	public Summoner getSummonerById(String summonerId) {
		
		String jpql = "SELECT s FROM Summoner s WHERE s.summonerId = :summonerId";
		
		return em.createQuery(jpql, Summoner.class)
				.setParameter("summonerId", summonerId)
				.getSingleResult();
	}

	@Override
	public Summoner getSummonerByName(String summonerName) {
		
		String jpql = "SELECT s FROM Summoner s WHERE s.summonerName = :summonerName";
		
		return em.createQuery(jpql, Summoner.class)
				.setParameter("summonerName", summonerName)
				.getSingleResult();
	}

	@Override
	public Rank getRank(String summonerId, String queueType, int seasonId) {

		String jpql = "SELECT r FROM Rank r " +
				"WHERE r.summonerId = :summonerId AND r.queueType = :queueType AND r.seasonId = :seasonId";

		return em.createQuery(jpql, Rank.class)
				.setParameter("summonerId", summonerId)
				.setParameter("queueType", queueType)
				.setParameter("seasonId", seasonId)
				.getSingleResult();
	}

	@Override
	public List<Rank> getRanks(String summonerId, int seasonId) {

		String jpql = "SELECT r FROM Rank r "
				+ "WHERE r.summonerId = :summonerId AND r.seasonId = :seasonId";
		
		return em.createQuery(jpql, Rank.class)
				.setParameter("summonerId", summonerId)
				.setParameter("seasonId", seasonId)
				.getResultList();
	}

	@Override
	public List<String> getMatchIds(String summonerId, int start, int count) {
		
		String jpql = "SELECT m.matchId FROM Match m " +
				"WHERE m.teams.members.summonerId = :summonerId ORDER BY m.matchId DESC";
		
		return em.createQuery(jpql, String.class)
				.setParameter("summonerId", summonerId)
				.setFirstResult(start)
				.setMaxResults(count)
				.getResultList();
	}

	@Override
	public Match getMatch(String matchId) {
		
		String jpql = "SELECT m FROM Match m WHERE m.matchId = matchId";
		
		try{
			return em.createQuery(jpql, Match.class)
				.setParameter("matchId", matchId)
				.getSingleResult();
		}catch(NoResultException e) {
			return null;
		}
	}

}
