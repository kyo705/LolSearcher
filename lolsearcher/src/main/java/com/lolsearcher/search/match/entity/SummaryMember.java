package com.lolsearcher.search.match.entity;

import com.lolsearcher.search.champion.PositionState;
import com.lolsearcher.search.match.MatchResultState;
import com.lolsearcher.search.match.MatchResultState.MatchResultConverter;
import com.lolsearcher.search.match.TeamState;
import com.lolsearcher.search.match.TeamState.TeamConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;
import org.springframework.cache.CacheManager;

import javax.persistence.*;
import java.io.Serializable;

import static com.google.common.base.Preconditions.checkArgument;
import static com.lolsearcher.search.match.MatchConstant.*;
import static com.lolsearcher.search.summoner.SummonerConstant.SUMMONER_ID_MAX_LENGTH;
import static com.lolsearcher.search.summoner.SummonerConstant.SUMMONER_ID_MIN_LENGTH;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@NoArgsConstructor
@Getter
@ToString(exclude = "match")
@Entity
@Table(name = "match_summary_members", indexes = {@Index(columnList = "summonerId")})
public class SummaryMember implements Serializable {

    @Id
    private Long id;
    @Convert(converter = MatchResultConverter.class)
    private MatchResultState result;
    @Convert(converter = TeamConverter.class)
    private TeamState team;
    private String summonerId;
    private int banChampionId;
    private int pickChampionId;
    private short positionId;
    private short championLevel;    /* level : 1 ~ 18 */
    private short minionKills;      /* lineMinionKills + NeutralMinionKills */
    private short kills;
    private short deaths;
    private short assists;
    private short item0;            /* item 리스트(item0 ~ item6)를 반정규화한 이유 : 아이템의 순서가 중요 */
    private short item1;
    private short item2;
    private short item3;
    private short item4;
    private short item5;
    private short item6;

    @BatchSize(size = 1000)
    @OneToOne(mappedBy = "summaryMember", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Perks perks;  /* 해당 게임의 특정 유저가 선택한 스펠, 룬 특성 */

    @BatchSize(size = 1000)
    @OneToOne(mappedBy = "summaryMember", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private DetailMember detailMember;

    @BatchSize(size = 100)
    @ManyToOne
    @JoinColumn(name = "matchId", referencedColumnName = "matchId")
    private Match match;

    public void setMatch(Match match) throws IllegalAccessException {

        if(match.getMembers().size() >= THE_NUMBER_OF_MEMBER){
            throw new IllegalAccessException("이미 연관관계 설정이 된 Match 객체입니다.");
        }
        if(this.match != null) {
            throw new IllegalAccessException("이미 연관된 Match 객체가 존재합니다.");
        }
        this.match = match;
        match.getMembers().add(this);
    }

    public void setPerks(Perks perks) throws IllegalAccessException {

        if(this.perks != null) {
            throw new IllegalAccessException("이미 연관된 Perks 객체가 존재합니다.");
        }
        this.perks = perks;
    }

    public void setDetailMember(DetailMember detailMember) throws IllegalAccessException {

        if(this.detailMember != null) {
            throw new IllegalAccessException("이미 연관된 DetailMember 객체가 존재합니다.");
        }
        this.detailMember = detailMember;
    }

    public void validate(CacheManager cacheManager) {


        checkArgument(
                isNotEmpty(summonerId) &&
                        summonerId.length() >= SUMMONER_ID_MIN_LENGTH &&
                        summonerId.length() <= SUMMONER_ID_MAX_LENGTH,

                String.format("summonerId must be provided and its length must be between %s and %s",
                        SUMMONER_ID_MIN_LENGTH, SUMMONER_ID_MAX_LENGTH)
        );
        checkArgument(
                cacheManager.getCache(CHAMPION_ID_LIST).get(banChampionId) != null,
                "banChampionId must be in boundary"
        );
        checkArgument(
                cacheManager.getCache(CHAMPION_ID_LIST).get(pickChampionId) != null,
                "pickChampionId must be in boundary"
        );
        checkArgument(
                PositionState.valueOfCode(positionId) != null,
                "positionId must be in boundary"
        );
        checkArgument(
                championLevel >=1 && championLevel <= 18,
                "championLevel must be between 1 and 18"
        );
        checkArgument(minionKills >= 0, "minionKills must be positive");
        checkArgument(kills >= 0, "kills must be positive");
        checkArgument(deaths >= 0, "deaths must be positive");
        checkArgument(assists >= 0, "assists must be positive");

        checkArgument(
                cacheManager.getCache(ITEM_ID_LIST).get(item0) != null,
                "item0 must be in boundary"
        );
        checkArgument(
                cacheManager.getCache(ITEM_ID_LIST).get(item1) != null,
                "item1 must be in boundary"
        );
        checkArgument(
                cacheManager.getCache(ITEM_ID_LIST).get(item2) != null,
                "item2 must be in boundary"
        );
        checkArgument(
                cacheManager.getCache(ITEM_ID_LIST).get(item3) != null,
                "item3 must be in boundary"
        );
        checkArgument(
                cacheManager.getCache(ITEM_ID_LIST).get(item4) != null,
                "item4 must be in boundary"
        );
        checkArgument(
                cacheManager.getCache(ITEM_ID_LIST).get(item5) != null,
                "item5 must be in boundary"
        );
        checkArgument(
                cacheManager.getCache(ITEM_ID_LIST).get(item6) != null,
                "item6 must be in boundary"
        );

        perks.validate(cacheManager);
        detailMember.validate();
    }
}
