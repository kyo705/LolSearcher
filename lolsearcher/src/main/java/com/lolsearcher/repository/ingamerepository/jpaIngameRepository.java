package com.lolsearcher.repository.ingamerepository;

import java.util.List;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.lolsearcher.domain.entity.ingame.InGame;

@Repository
public class jpaIngameRepository implements IngameRepository {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final EntityManager em;
	
	
	public jpaIngameRepository(EntityManager em) {
		this.em = em;
	}
	
	@Override
	public void saveIngame(InGame ingame) {
		if(em.find(InGame.class, ingame.getGameId())==null) {
			em.persist(ingame);
		}else
			em.merge(ingame);
	}

	@Override
	public InGame getInGame(long gameId) {
		return em.find(InGame.class, gameId);
	}

	
	@Override
	public List<InGame> getInGamesBySummonerId(String summonerid) {
		//onetomany관계가 2개 존재하기 때문에 패치조인 사용 불가 => @fetch(FetchMode.SUBSELECT) 사용 (WHERE IN 절 사용)
		
		String jpql = "SELECT i FROM InGame i WHERE i.gameId IN "
				+ "(SELECT p.ck.gameId FROM CurrentGameParticipant p WHERE p.ck.summonerId = :summonerId) "
				+ "ORDER BY i.gameId DESC";
		
		 List<InGame> ingames = em.createQuery(jpql, InGame.class)
				 .setParameter("summonerId", summonerid)
				 .getResultList();
		 
		 return ingames;
	}
	
	
	@Override
	public void deleteIngameBySummonerId(String summonerid) {
		String jpql = "DELETE FROM InGame i WHERE i.gameId IN "
				+ "(SELECT p.ck.gameId FROM CurrentGameParticipant p WHERE p.ck.summonerId = :summonerId)";
		
		int count = em.createQuery(jpql, InGame.class)
				.setParameter("summonerId", summonerid)
				.executeUpdate();
		
		
		logger.info("'{}'개 InGame Entity 제거", count);
	}

	@Override
	public void deleteIngame(InGame ingame) {
		em.remove(ingame);
	}
}
