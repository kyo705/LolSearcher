package com.lolsearcher.repository.search.summoner;

import com.lolsearcher.model.entity.summoner.Summoner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class JpaSummonerRepository implements SummonerRepository {

	private final EntityManager em;
	
	@Override
	public Summoner findSummonerById(String summonerId){

		String jpql = "SELECT s FROM Summoner s WHERE s.summonerId = :summonerId";
		
		return em.createQuery(jpql, Summoner.class)
				.setParameter("summonerId", summonerId)
				.getSingleResult();
	}
	
	@Override
	public List<Summoner> findSummonerByName(String summonerName) {

		String jpql = "SELECT s FROM Summoner s WHERE s.summonerName = :summonerName "
				+ "ORDER BY s.lastRenewTimeStamp DESC";

		return em.createQuery(jpql, Summoner.class)
				.setParameter("summonerName", summonerName)
				.getResultList();
	}

}
