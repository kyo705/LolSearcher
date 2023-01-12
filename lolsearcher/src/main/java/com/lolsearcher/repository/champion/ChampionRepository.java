package com.lolsearcher.repository.champion;

import java.util.List;

import com.lolsearcher.model.entity.champion.position.ChampPosition;
import com.lolsearcher.model.entity.champion.enemy.ChampEnemy;
import com.lolsearcher.model.entity.champion.item.ChampItem;

public interface ChampionRepository {

	List<ChampPosition> findChampPositions(int positionId);

	List<ChampItem> findChampItems(int championId);

	List<ChampEnemy> findChampEnemies(int championId);

}
