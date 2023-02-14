package com.lolsearcher.repository.search.champstats;

import com.lolsearcher.model.entity.champion.ChampEnemyStats;
import com.lolsearcher.model.entity.champion.ChampItemStats;
import com.lolsearcher.model.entity.champion.ChampPositionStats;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class JpaChampionRepository implements ChampionRepository {

	private final EntityManager em;
	
	@Override
	public List<ChampPositionStats> findAllChampPositionStats(int positionId, String version) {

		String jpql = "SELECT c FROM ChampPositionStats c "
				+ "WHERE c.gameVersion = :gameVersion AND c.positionId = :positionId "
				+ "ORDER BY c.wins + c.losses DESC";
		
		return em.createQuery(jpql, ChampPositionStats.class)
				.setParameter("positionId", positionId)
				.setParameter("gameVersion", version)
				.getResultList();
	}

	@Override
	public List<ChampItemStats> findChampItems(int championId, String version) {

		String jpql = "SELECT c FROM ChampItemStats c "
				+ "WHERE c.championId = :championId AND c.gameVersion = :gameVersion "
				+ "ORDER BY c.wins + c.losses DESC";
		
		return em.createQuery(jpql, ChampItemStats.class)
				.setParameter("championId", championId)
				.setParameter("gameVersion", version)
				.getResultList();
	}

	@Override
	public List<ChampEnemyStats> findChampEnemies(int championId, String version) {

		String jpql = "SELECT c FROM ChampEnemyStats c "
				+ "WHERE c.championId = :championId AND c.gameVersion = :gameVersion "
				+ "ORDER BY c.wins + c.losses DESC";
		
		return em.createQuery(jpql, ChampEnemyStats.class)
				.setParameter("championId", championId)
				.setParameter("gameVersion", version)
				.getResultList();
	}

}
