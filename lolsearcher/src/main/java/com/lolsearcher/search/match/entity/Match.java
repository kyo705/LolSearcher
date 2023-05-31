package com.lolsearcher.search.match.entity;

import lombok.Getter;
import org.hibernate.annotations.BatchSize;
import org.springframework.cache.CacheManager;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.lolsearcher.search.match.MatchConstant.QUEUE_ID_LIST;
import static com.lolsearcher.search.match.MatchConstant.THE_NUMBER_OF_MEMBER;
import static com.lolsearcher.search.rank.RankConstant.CURRENT_SEASON_ID;
import static com.lolsearcher.search.rank.RankConstant.INITIAL_SEASON_ID;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Getter
@Entity
@Table(name = "MATCHES", indexes = {@Index(columnList = "matchId", unique = true)})
public class Match implements Serializable {

	@Id
	private Long id;
	@Column(unique = true)
	private String matchId; /* REST API로 받아올 때 필요한 고유한 match id */
	private LocalTime gameDuration;
	private LocalDateTime gameEndTimestamp;
	private int queueId;
	private int seasonId;
	private String version;

	@BatchSize(size = 1000)
	@OneToMany(mappedBy = "match", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<SummaryMember> members = new ArrayList<>(THE_NUMBER_OF_MEMBER);

	public void validate(CacheManager cacheManager) {

		checkArgument(id != null && id > 0, "id must be provided and positive");
		checkArgument(isNotEmpty(matchId), "matchId must be provided");
		checkArgument(gameDuration != null, "gameEndTimestamp must be provided ");
		checkArgument(
				gameEndTimestamp != null && LocalDateTime.now().isAfter(gameEndTimestamp),
				"gameEndTimestamp must be provided and be before current timestamp"
		);

		checkArgument(cacheManager.getCache(QUEUE_ID_LIST).get(queueId) != null,
				"queueId must be positive");

		checkArgument(seasonId <= CURRENT_SEASON_ID && seasonId >= INITIAL_SEASON_ID,
				String.format("seasonId must be provided between %d and %d", INITIAL_SEASON_ID, CURRENT_SEASON_ID));
		checkArgument(isNotEmpty(version), "id must be provided");
		checkArgument(
				members.size() == THE_NUMBER_OF_MEMBER,
				String.format("members must exist %d per match", THE_NUMBER_OF_MEMBER)
		);

		for(SummaryMember member : members) {
			member.validate(cacheManager);
		}
	}
}
