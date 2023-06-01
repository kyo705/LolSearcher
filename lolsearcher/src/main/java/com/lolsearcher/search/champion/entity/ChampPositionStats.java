package com.lolsearcher.search.champion.entity;

import com.lolsearcher.search.champion.PositionState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cache.CacheManager;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import static com.google.common.base.Preconditions.checkArgument;
import static com.lolsearcher.search.match.MatchConstant.CHAMPION_ID_LIST;
import static com.lolsearcher.search.match.MatchConstant.GAME_VERSION_LIST;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(indexes = {@Index(columnList = "gameVersion, positionId, championId")})
public class ChampPositionStats {

	@Id
	private long id;
	private String gameVersion;
	private int championId;
	private int positionId;
	private long wins;
	private long losses;
	private long bans;

    public void validate(CacheManager cacheManager) {

		checkArgument(
				cacheManager.getCache(GAME_VERSION_LIST).get(gameVersion) != null,
				"championId must be in boundary"
		);
		checkArgument(
				cacheManager.getCache(CHAMPION_ID_LIST).get(championId) != null,
				"championId must be in boundary"
		);
		checkArgument(
				PositionState.valueOfCode(positionId) != null,
				"positionId must be in boundary"
		);
		checkArgument(wins >= 0, "wins must be positive");
		checkArgument(losses >= 0, "wins must be positive");
    }
}
