package com.lolsearcher.repository.ChampionRepository;

import java.util.List;

import com.lolsearcher.domain.entity.championstatic.Champion;
import com.lolsearcher.domain.entity.championstatic.enemy.ChampEnemy;
import com.lolsearcher.domain.entity.championstatic.item.ChampItem;

public interface ChampionReository {

	List<Champion> findChamps(String position);

	List<ChampItem> findChampItems(String champion);

	List<ChampEnemy> findChampEnemys(String champion);

}
