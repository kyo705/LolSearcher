package com.lolsearcher.search.summoner;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;
import static com.lolsearcher.search.match.MatchConstant.LAST_MATCH_ID_MAX_LENGTH;
import static com.lolsearcher.search.summoner.SummonerConstant.*;
import static org.apache.commons.lang3.StringUtils.*;


@NoArgsConstructor
@Getter
@Entity
@Table(indexes = {@Index(columnList = "summonerId"), @Index(columnList = "summonerName")})
public class Summoner {

	@Id
	private Long id;
	private String summonerId;
	private String accountId;
	private String puuid;
	private String summonerName;
	private String lastMatchId;
	private Integer profileIconId;
	private long summonerLevel;
	private LocalDateTime lastRenewTimeStamp;

	public void validate() {

		checkArgument(
				isNotBlank(summonerId) &&
						summonerId.length() >= SUMMONER_ID_MIN_LENGTH &&
						summonerId.length() <= SUMMONER_ID_MAX_LENGTH,

				String.format("summonerId must be provided and its length must be between %s and %s",
						SUMMONER_ID_MIN_LENGTH, SUMMONER_ID_MAX_LENGTH)
		);
		checkArgument(
				isNotBlank(accountId) &&
						accountId.length() >= ACCOUNT_ID_MIN_LENGTH &&
						accountId.length() <= ACCOUNT_ID_MAX_LENGTH,

				String.format("summonerId must be provided and its length must be between %s and %s",
						ACCOUNT_ID_MIN_LENGTH, ACCOUNT_ID_MAX_LENGTH)
		);
		checkArgument(
				isNotBlank(puuid) &&
						puuid.length() >= PUUID_MIN_LENGTH &&
						puuid.length() <= PUUID_MAX_LENGTH,

				String.format("puuid must be provided and its length must be between %s and %s",
						PUUID_MIN_LENGTH, PUUID_MAX_LENGTH)
		);
		checkArgument(
				isNotBlank(summonerName) &&
						summonerName.length() >= SUMMONER_NAME_MIN_LENGTH &&
						summonerName.length() <= SUMMONER_NAME_MAX_LENGTH,

				String.format("summonerName must be provided and its length must be between %s and %s",
						SUMMONER_NAME_MIN_LENGTH, SUMMONER_NAME_MAX_LENGTH)
		);
		checkArgument(
				isEmpty(lastMatchId) || lastMatchId.length() <= LAST_MATCH_ID_MAX_LENGTH,
				String.format("lastMatchId length must be less than %s characters", LAST_MATCH_ID_MAX_LENGTH)
		);
		checkArgument(profileIconId == null || profileIconId >= 0, "profileIconId must be positive");
		checkArgument(lastRenewTimeStamp == null || lastRenewTimeStamp.isBefore(LocalDateTime.now()),
				"lastRenewTimestamp must be the past");
		checkArgument(summonerLevel >= 0, "summoner level must be positive");
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setSummonerName(String summonerName) {

		checkArgument(
				isNotEmpty(summonerName) &&
						summonerName.length() >= SUMMONER_NAME_MIN_LENGTH &&
						summonerName.length() <= SUMMONER_NAME_MAX_LENGTH,

				String.format("summonerName must be provided and its length must be between %s and %s",
						SUMMONER_NAME_MIN_LENGTH, SUMMONER_NAME_MAX_LENGTH)
		);

		this.summonerName = summonerName;
	}

	public void setProfileIconId(int profileIconId) {
		this.profileIconId = profileIconId;
	}

	public void setSummonerLevel(long summonerLevel) {

		checkArgument(this.summonerLevel <= summonerLevel && summonerLevel > 0,
				"summoner level must be positive and increase");

		this.summonerLevel = summonerLevel;
	}

	public void setLastMatchId(String lastMatchId) {

		checkArgument(isEmpty(lastMatchId) || lastMatchId.length() <= LAST_MATCH_ID_MAX_LENGTH,
				String.format("lastMatchId length must be less than %s characters", LAST_MATCH_ID_MAX_LENGTH)
		);

		this.lastMatchId = lastMatchId;
	}

	public void setLastRenewTimeStamp(LocalDateTime lastRenewTimeStamp) {

		checkArgument(lastRenewTimeStamp != null && lastRenewTimeStamp.isAfter(this.lastRenewTimeStamp),
				"lastRenewTimeStamp must be provided and increased");
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Summoner summoner = (Summoner) o;
		return Objects.equals(summonerId, summoner.getSummonerId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(summonerId);
	}
}
