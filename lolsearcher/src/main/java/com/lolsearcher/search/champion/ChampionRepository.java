package com.lolsearcher.search.champion;

import com.lolsearcher.search.champion.entity.ChampEnemyStats;
import com.lolsearcher.search.champion.entity.ChampItemStats;
import com.lolsearcher.search.champion.entity.ChampPositionStats;

import java.util.List;

public interface ChampionRepository {

	List<ChampPositionStats> findAll(int positionId, String version);

	List<ChampItemStats> findItemStats(int championId, String version);

	List<ChampEnemyStats> findEnemyStats(int championId, String version);

}
