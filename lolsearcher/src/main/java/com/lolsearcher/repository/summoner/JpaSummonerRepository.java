package com.lolsearcher.repository.summoner;

import com.lolsearcher.model.entity.summoner.Summoner;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class JpaSummonerRepository implements SummonerRepository {

	private final EntityManager em;

	@Override
	public void saveSummoner(Summoner summoner) throws DataIntegrityViolationException {
		em.persist(summoner);
	}
	
	@Override
	public Summoner findSummonerById(String summonerId){
		String jpql = "SELECT s FROM Summoner s WHERE s.id = :summonerId";
		
		return em.createQuery(jpql, Summoner.class)
				.setParameter("summonerId", summonerId)
				.getSingleResult();
	}
	
	@Override
	public List<Summoner> findSummonerByName(String summonerName) {
		String jpql = "SELECT s FROM Summoner s WHERE s.name = :summonerName "
				+ "ORDER BY s.lastRenewTimeStamp DESC";
		
		return em.createQuery(jpql, Summoner.class)
				.setParameter("summonerName", summonerName)
				.getResultList();
	}

	@Override
	public void deleteSummoner(Summoner summoner) {
		String jpql = "DELETE FROM Summoner s WHERE s.id = :id";
		
		em.createQuery(jpql)
		.setParameter("id", summoner.getSummonerId())
		.executeUpdate();
	}
}
