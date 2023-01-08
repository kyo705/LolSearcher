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
		String jpql = "SELECT s FROM Summoner s WHERE s.summonerName = :summonerName "
				+ "ORDER BY s.lastRenewTimeStamp DESC";
		
		return em.createQuery(jpql, Summoner.class)
				.setParameter("summonerName", summonerName)
				.getResultList();
	}

	@Override
	public void updateSummoner(Summoner oldSummoner, Summoner newSummoner) {

		if(oldSummoner.getRevisionDate() == newSummoner.getRevisionDate()){
			return;
		}
		oldSummoner.setRevisionDate(newSummoner.getRevisionDate());
		oldSummoner.setSummonerName(newSummoner.getSummonerName());
		oldSummoner.setProfileIconId(newSummoner.getProfileIconId());
		oldSummoner.setSummonerLevel(newSummoner.getSummonerLevel());
		oldSummoner.setLastRenewTimeStamp(newSummoner.getLastRenewTimeStamp());
	}

	@Override
	public void updateSummonerLastMatchId(Summoner summoner, String lastMatchId) {

		summoner.setLastMatchId(lastMatchId);
	}

	@Override
	public void deleteSummoner(Summoner summoner) {
		String jpql = "DELETE FROM Summoner s WHERE s.id = :id";
		
		em.createQuery(jpql)
		.setParameter("id", summoner.getSummonerId())
		.executeUpdate();
	}
}
