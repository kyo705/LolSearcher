package com.lolsearcher.repository.ingame;

import java.util.List;

import javax.persistence.EntityManager;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.lolsearcher.model.entity.ingame.InGame;

@RequiredArgsConstructor
@Repository
public class jpaInGameRepository implements InGameRepository {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final EntityManager em;
	
	@Override
	public void saveInGame(InGame inGame) {
		if(em.find(InGame.class, inGame.getGameId())==null) {
			em.persist(inGame);
		}else
			em.merge(inGame);
	}

	@Override
	public InGame getInGame(long gameId) {
		return em.find(InGame.class, gameId);
	}

	
	@Override
	public List<InGame> getInGamesBySummonerId(String summonerId) {
		//onetomany관계가 2개 존재하기 때문에 패치조인 사용 불가 => @fetch(FetchMode.SUBSELECT) 사용 (WHERE IN 절 사용)
		
		String jpql = "SELECT i FROM InGame i WHERE i.gameId IN "
				+ "(SELECT p.ck.gameId FROM CurrentGameParticipant p WHERE p.ck.summonerId = :summonerId) "
				+ "ORDER BY i.gameId DESC";
		
		return em.createQuery(jpql, InGame.class)
				.setParameter("summonerId", summonerId)
				.getResultList();
	}
	
	
	@Override
	public void deleteInGameBySummonerId(String summonerId) {
		String jpql = "DELETE FROM InGame i WHERE i.gameId IN "
				+ "(SELECT p.ck.gameId FROM CurrentGameParticipant p WHERE p.ck.summonerId = :summonerId)";
		
		int count = em.createQuery(jpql, InGame.class)
				.setParameter("summonerId", summonerId)
				.executeUpdate();
		
		
		logger.info("'{}'개 InGame Entity 제거", count);
	}

	@Override
	public void deleteInGame(InGame inGame) {
		em.remove(inGame);
	}
}
