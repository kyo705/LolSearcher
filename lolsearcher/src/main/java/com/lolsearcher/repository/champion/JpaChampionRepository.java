package com.lolsearcher.repository.champion;

import java.util.List;

import javax.persistence.EntityManager;

import com.lolsearcher.model.entity.champion.position.ChampPosition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import com.lolsearcher.model.entity.champion.enemy.ChampEnemy;
import com.lolsearcher.model.entity.champion.item.ChampItem;

@RequiredArgsConstructor
@Repository
public class JpaChampionRepository implements ChampionRepository {
	
	private static final int seasonId = 22;
	private final EntityManager em;
	
	@Override
	public List<ChampPosition> findChampPositions(int positionId) {
		String jpql = "SELECT c FROM ChampPosition c "
				+ "WHERE c.ck.positionId = :positionId AND c.ck.seasonId = :seasonId "
				+ "ORDER BY c.wins + c.losses DESC";
		
		return em.createQuery(jpql, ChampPosition.class)
				.setParameter("positionId", positionId)
				.setParameter("seasonId", seasonId)
				.getResultList();
	}

	@Override
	public List<ChampItem> findChampItems(int championId) {
		String jpql = "SELECT c FROM ChampItem c "
				+ "WHERE c.ck.championId = :championId AND c.ck.seasonId = :seasonId "
				+ "ORDER BY c.wins + c.losses DESC";
		
		return em.createQuery(jpql, ChampItem.class)
				.setParameter("championId", championId)
				.setParameter("seasonId", seasonId)
				.getResultList();
	}

	@Override
	public List<ChampEnemy> findChampEnemies(int championId) {
		String jpql = "SELECT c FROM ChampEnemy c "
				+ "WHERE c.ck.championId = :championId AND c.ck.seasonId = :seasonId "
				+ "ORDER BY c.wins + c.losses DESC";
		
		return em.createQuery(jpql, ChampEnemy.class)
				.setParameter("championId", championId)
				.setParameter("seasonId", seasonId)
				.getResultList();
	}

}
