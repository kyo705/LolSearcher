package com.lolsearcher.repository.ingamerepository;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.lolsearcher.domain.entity.ingame.InGame;

@Repository
public class jpaIngameRepository implements IngameRepository {

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
	public List<InGame> getIngame(String summonerid) {
		//onetomany관계가 2개 존재하기 때문에 패치조인 사용 불가 => @fetch(FetchMode.SUBSELECT) 사용 (WHERE IN 절 사용)
		
		//where절의 in절에 서브쿼리를 넣게 되면 느려짐 => 하지만 인게임 데이터는 주기적으로 데이터를 삭제하기때문에 성능에 큰 차이는 없음.
		String jpql = "SELECT i FROM InGame i WHERE i.gameId IN "
				+ "(SELECT p.ck.gameId FROM CurrentGameParticipant p WHERE p.ck.summonerId = :summonerId) "
				+ "ORDER BY i.gameStartTime DESC";
		
		 List<InGame> ingames = em.createQuery(jpql, InGame.class)
				 .setParameter("summonerId", summonerid)
				 .getResultList();
		 
		 return ingames;
	}
	
	
	@Override
	public void deleteIngameBySummonerId(String summonerid) {
		String jpql = "DELETE FROM InGame i WHERE i.gameId IN "
				+ "(SELECT p.ck.gameId FROM CurrentGameParticipant p WHERE p.ck.summonerId = :summonerId";
		
		int count = 0;
		try {
			count = em.createQuery(jpql, InGame.class)
					.setParameter("summonerId", summonerid)
					.executeUpdate();
		}catch(Exception e) {
			
		}
		
		System.out.println(count + " 개 entity 제거");
	}

	@Override
	public void deleteIngame(InGame ingame) {
		em.remove(ingame);
	}

}
