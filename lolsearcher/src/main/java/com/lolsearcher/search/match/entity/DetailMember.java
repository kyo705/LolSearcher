package com.lolsearcher.search.match.entity;

import lombok.Getter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

import static com.google.common.base.Preconditions.checkArgument;

@Getter
@ToString(exclude = "summaryMember")
@Entity
@Table(name = "match_detail_members")
public class DetailMember implements Serializable {

    @Id
    private Long id;
    private int goldEarned;
    private int goldSpent;
    private int totalDamageDealt;
    private int totalDamageDealtToChampions;
    private int totalDamageShieldedOnTeammates;
    private int totalDamageTaken;
    private int timeCCingOthers;
    private int totalHeal;
    private int totalHealsOnTeammates;
    private short detectorWardPurchased;
    private short detectorWardsPlaced;
    private short wardKills;
    private short wardsPlaced;

    @OneToOne
    @JoinColumn(name = "summaryMemberId", referencedColumnName = "id")
    private SummaryMember summaryMember;

    public void setSummaryMember(SummaryMember summaryMember) throws IllegalAccessException {

        if(summaryMember.getDetailMember() != null){
            throw new IllegalAccessException("이미 연관관계 설정이 된 SummaryMember 객체입니다.");
        }
        if(this.summaryMember != null) {
            throw new IllegalAccessException("이미 연관된 SummaryMember 객체가 존재합니다.");
        }
        this.summaryMember = summaryMember;
        summaryMember.setDetailMember(this);
    }

    public void validate() {

        checkArgument(goldEarned >= 0, "goldEarned must be positive");
        checkArgument(goldSpent >= 0, "goldSpent must be positive");
        checkArgument(totalDamageDealt >= 0, "totalDamageDealt must be positive");
        checkArgument(totalDamageDealtToChampions >= 0, "totalDamageDealtToChampions must be positive");
        checkArgument(totalDamageShieldedOnTeammates >= 0, "totalDamageShieldedOnTeammates must be positive");
        checkArgument(totalDamageTaken >= 0, "totalDamageTaken must be positive");
        checkArgument(timeCCingOthers >= 0, "timeCCingOthers must be positive");
        checkArgument(totalHeal >= 0, "totalHeal must be positive");
        checkArgument(totalHealsOnTeammates >= 0, "totalHealsOnTeammates must be positive");
        checkArgument(detectorWardPurchased >= 0, "detectorWardPurchased must be positive");
        checkArgument(detectorWardsPlaced >= 0, "detectorWardsPlaced must be positive");
        checkArgument(wardKills >= 0, "wardKills must be positive");
        checkArgument(wardsPlaced >= 0, "wardsPlaced must be positive");
    }
}
