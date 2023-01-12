package com.lolsearcher.model.entity.match;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@ToString(exclude = "summaryMember")
@Entity
public class DetailMember implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
        this.summaryMember = summaryMember;
        summaryMember.setDetailMember(this);
    }
}
