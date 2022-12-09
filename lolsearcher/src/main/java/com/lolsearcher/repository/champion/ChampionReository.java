package com.lolsearcher.repository.champion;

import java.util.List;

import com.lolsearcher.model.entity.championstatic.position.ChampPosition;
import com.lolsearcher.model.entity.championstatic.enemy.ChampEnemy;
import com.lolsearcher.model.entity.championstatic.item.ChampItem;

public interface ChampionReository {

	List<ChampPosition> findChampPositions(String position);

	List<ChampItem> findChampItems(String champion);

	List<ChampEnemy> findChampEnemies(String champion);

}
