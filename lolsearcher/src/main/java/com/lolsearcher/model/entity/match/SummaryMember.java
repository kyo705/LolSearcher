package com.lolsearcher.model.entity.match;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@ToString(exclude = "team")
@Entity
@Table(indexes = {@Index(columnList = "summonerId")})
public class SummaryMember implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) /* mariaDB 사용중 */
    private Long id;
    private String summonerId;
    private int banChampionId;
    private int pickChampionId;
    private short positionId;
    private short championLevel; /* level : 1 ~ 18 */
    private short minionKills; /* lineMinionKills + NeutralMinionKills */
    private short kills;
    private short deaths;
    private short assists;
    private short item0;  /* item 리스트(item0 ~ item6)를 반정규화한 이유 : 아이템의 순서가 중요 */
    private short item1;
    private short item2;
    private short item3;
    private short item4;
    private short item5;
    private short item6;

    @BatchSize(size = 1000)
    @OneToOne(mappedBy = "summaryMember", cascade = CascadeType.ALL)
    private Perks perks;  /* 해당 게임의 특정 유저가 선택한 스펠, 룬 특성 */

    @BatchSize(size = 1000)
    @OneToOne(mappedBy = "summaryMember", cascade = CascadeType.ALL)
    private DetailMember detailMember;

    @BatchSize(size = 100)
    @ManyToOne
    @JoinColumn(name = "teamId", referencedColumnName = "id")
    private Team team;

    public void setTeam(Team team) throws IllegalAccessException {

        if(team.getMembers().size() >= 5){
            throw new IllegalAccessException("이미 연관관계 설정이 된 Team 객체입니다.");
        }
        this.team = team;
        team.getMembers().add(this);
    }
}
