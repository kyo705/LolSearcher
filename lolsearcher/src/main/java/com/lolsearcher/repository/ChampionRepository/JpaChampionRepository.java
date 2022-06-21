package com.lolsearcher.repository.ChampionRepository;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.lolsearcher.domain.entity.championstatic.Champion;
import com.lolsearcher.domain.entity.championstatic.enemy.ChampEnemy;
import com.lolsearcher.domain.entity.championstatic.item.ChampItem;

@Repository
public class JpaChampionRepository implements ChampionReository {
	
	private static final int seasonId = 22;
	private final EntityManager em;
	
	public JpaChampionRepository(EntityManager em) {
		this.em = em;
	}
	
	@Override
	public List<Champion> findChamps(String position) {
		String jpql = "SELECT c FROM Champion c "
				+ "WHERE c.ck.position = :position AND c.ck.seasonId = :seasonId "
				+ "ORDER BY c.wins + c.losses DESC";
		
		return em.createQuery(jpql, Champion.class)
				.setParameter("position", position)
				.setParameter("seasonId", seasonId)
				.getResultList();
	}

	@Override
	public List<ChampItem> findChampItems(String champion) {
		String jpql = "SELECT c FROM ChampItem c "
				+ "WHERE c.ck.championId = :championId AND c.ck.seasonId = :seasonId "
				+ "ORDER BY c.wins + c.losses DESC";
		
		return em.createQuery(jpql, ChampItem.class)
				.setParameter("championId", champion)
				.setParameter("seasonId", seasonId)
				.getResultList();
	}

	@Override
	public List<ChampEnemy> findChampEnemys(String champion) {
		String jpql = "SELECT c FROM ChampEnemy c "
				+ "WHERE c.ck.championId = :championId AND c.ck.seasonId = :seasonId "
				+ "ORDER BY c.wins + c.losses DESC";
		
		return em.createQuery(jpql, ChampEnemy.class)
				.setParameter("championId", champion)
				.setParameter("seasonId", seasonId)
				.getResultList();
	}

}
