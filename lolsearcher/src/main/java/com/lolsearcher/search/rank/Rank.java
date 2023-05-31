package com.lolsearcher.search.rank;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import java.util.Objects;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.lolsearcher.search.rank.RankConstant.*;
import static com.lolsearcher.search.rank.TierState.*;
import static com.lolsearcher.search.summoner.SummonerConstant.SUMMONER_ID_MAX_LENGTH;
import static com.lolsearcher.search.summoner.SummonerConstant.SUMMONER_ID_MIN_LENGTH;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Builder
@Getter
@Entity
@Table(name = "ranks", indexes = {@Index(columnList = "summonerId, seasonId, queueType", unique = true)})
public class Rank {

	@Id
	private Long id;
	private String summonerId;
	private int seasonId;
	private RankTypeState queueType;
	private String leagueId;
	private TierState tier; /* GOLD, SLIVER, BRONZE ... */
	private RankState rank; /* I, II, III ... */
	private int leaguePoints;
	private long wins;
	private long losses;

	public void validate() {

		//argument valid
		checkArgument(
				isNotEmpty(summonerId) &&
						summonerId.length() >= SUMMONER_ID_MIN_LENGTH &&
						summonerId.length() <= SUMMONER_ID_MAX_LENGTH,
				String.format("summonerId must be provided and its length must be between %s and %s",
						SUMMONER_ID_MIN_LENGTH, SUMMONER_ID_MAX_LENGTH)
		);
		checkArgument(seasonId >= INITIAL_SEASON_ID && seasonId <= CURRENT_SEASON_ID,
				"seasonId must be in boundary seasonId");

		checkArgument(queueType != null, "queueType must be provided");

		checkArgument((leagueId.length() >= LEAGUE_ID_MIN_LENGTH && leagueId.length() <= LEAGUE_ID_MAX_LENGTH)
						|| isEmpty(leagueId),
				String.format("leagueId must be NULL or its length must be between %s and %s",
						LEAGUE_ID_MIN_LENGTH, LEAGUE_ID_MAX_LENGTH)
		);
		checkArgument(leaguePoints >= 0 &&
				(leaguePoints <= 100 || tier == CHALLENGER || tier == GRANDMASTER || tier == MASTER),
				"leaguePoints must be in boundary point");

		checkArgument(wins >= 0, "wins must be positive");

		checkArgument(losses >= 0, "losses must be positive");
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setTier(TierState tier){
		checkArgument(tier != null, "tier must be provided");
		this.tier = tier;
	}

	public void setRank(RankState rank){
		checkArgument(rank != null, "rank must be provided");
		this.rank = rank;
	}

	public void setLeagueId(String leagueId) {

		checkArgument((leagueId.length() >= LEAGUE_ID_MIN_LENGTH && leagueId.length() <= LEAGUE_ID_MAX_LENGTH)
						|| isEmpty(leagueId),
				String.format("leagueId must be NULL or its length must be between %s and %s",
						LEAGUE_ID_MIN_LENGTH, LEAGUE_ID_MAX_LENGTH)
		);
		this.leagueId = leagueId;
	}

	public void setLeaguePoints(int leaguePoints) {

		checkArgument(
				leaguePoints >= 0 &&
						(leaguePoints <= 100 || tier == CHALLENGER || tier == GRANDMASTER || tier == MASTER),
				"leaguePoints must be in boundary point"
		);
		this.leaguePoints = leaguePoints;
	}

	public void setWins(long wins) {

		checkArgument(wins >= 0, "wins must be positive");
		this.wins = wins;
	}

	public void setLosses(long losses) {

		checkArgument(losses >= 0, "losses must be positive");
		this.losses = losses;
	}

	public Optional<String> getLeagueId(){

		return Optional.ofNullable(this.leagueId);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Rank ranks = (Rank) o;
		return Objects.equals(this.summonerId, ranks.summonerId) &&
				Objects.equals(this.seasonId, ranks.seasonId) &&
				Objects.equals(this.queueType, ranks.queueType);
	}

	@Override
	public int hashCode() {
		return Objects.hash(summonerId, seasonId, queueType);
	}

}

