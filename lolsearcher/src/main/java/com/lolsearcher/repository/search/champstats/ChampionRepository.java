package com.lolsearcher.repository.search.champstats;

import com.lolsearcher.model.entity.champion.ChampEnemyStats;
import com.lolsearcher.model.entity.champion.ChampItemStats;
import com.lolsearcher.model.entity.champion.ChampPositionStats;

import java.util.List;

public interface ChampionRepository {

	List<ChampPositionStats> findAllChampPositionStats(int positionId, String version);

	List<ChampItemStats> findChampItems(int championId, String version);

	List<ChampEnemyStats> findChampEnemies(int championId, String version);

}
