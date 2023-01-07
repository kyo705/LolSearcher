package com.lolsearcher.model.entity.match;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class DetailMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long summaryMemberId; /* Foreign Key */
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
    @JoinColumn(name = "summary_member_id", referencedColumnName = "id")
    private SummaryMember summaryMember;

    public void setSummaryMember(SummaryMember summaryMember) throws IllegalAccessException {

        if(summaryMember.getDetailMember() != null){
            throw new IllegalAccessException("이미 연관관계 설정이 된 SummaryMember 객체입니다.");
        }
        this.summaryMember = summaryMember;
        summaryMember.setDetailMember(this);
    }
}
