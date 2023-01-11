package com.lolsearcher.model.entity.match;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static com.lolsearcher.constant.LolSearcherConstants.THE_NUMBER_OF_TEAM_MEMBERS;

@NoArgsConstructor
@Data
@Entity
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(scale = 3)
    private byte gameResult;    /*  0 : win,  1 : loss,  2 : draw  */
    private short teamPositionId; /* 100 : red, 200 : blue */

    @BatchSize(size = 100)
    @ManyToOne
    @JoinColumn(name = "matchId", referencedColumnName = "id")
    private Match match;

    @BatchSize(size = 1000)
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<SummaryMember> members = new ArrayList<>(THE_NUMBER_OF_TEAM_MEMBERS);

    public void setMatch(Match match) throws IllegalAccessException {

        if(match.getTeams().size() >= 2){
            throw new IllegalAccessException("이미 연관관계 설정이 된 Match 객체입니다.");
        }
        this.match = match;
        match.getTeams().add(this);
    }
}
