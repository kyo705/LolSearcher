package com.lolsearcher.search.champion;

import com.lolsearcher.search.champion.entity.ChampEnemyStats;
import com.lolsearcher.search.champion.entity.ChampItemStats;
import com.lolsearcher.search.champion.entity.ChampPositionStats;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class JpaChampionRepository implements ChampionRepository {

	private final EntityManager em;
	
	@Override
	public List<ChampPositionStats> findAll(int positionId, String version) {

		String jpql = "SELECT c FROM ChampPositionStats c "
				+ "WHERE c.gameVersion = :gameVersion AND c.positionId = :positionId "
				+ "ORDER BY c.wins + c.losses DESC";
		
		return em.createQuery(jpql, ChampPositionStats.class)
				.setParameter("positionId", positionId)
				.setParameter("gameVersion", version)
				.getResultList();
	}

	@Override
	public List<ChampItemStats> findItemStats(int championId, String version) {

		String jpql = "SELECT c FROM ChampItemStats c "
				+ "WHERE c.championId = :championId AND c.gameVersion = :gameVersion "
				+ "ORDER BY c.wins + c.losses DESC";
		
		return em.createQuery(jpql, ChampItemStats.class)
				.setParameter("championId", championId)
				.setParameter("gameVersion", version)
				.getResultList();
	}

	@Override
	public List<ChampEnemyStats> findEnemyStats(int championId, String version) {

		String jpql = "SELECT c FROM ChampEnemyStats c "
				+ "WHERE c.championId = :championId AND c.gameVersion = :gameVersion "
				+ "ORDER BY c.wins + c.losses DESC";
		
		return em.createQuery(jpql, ChampEnemyStats.class)
				.setParameter("championId", championId)
				.setParameter("gameVersion", version)
				.getResultList();
	}

}
