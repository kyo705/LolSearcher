package com.lolsearcher.model.entity.match;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
public class Perks implements Serializable {
    @EmbeddedId
    MemberCompKey memberCompKey;

    @Column(name = "PERK_STATS_ID")
    private int perkStatsId;

    @ManyToOne
    @JoinColumn(name = "PERK_STATS_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private PerkStats perkStats;

    @JsonBackReference
    @OneToOne
    @MapsId
    @JoinColumns({
            @JoinColumn(name = "MATCH_ID", referencedColumnName = "MATCH_ID"),
            @JoinColumn(name = "NUM", referencedColumnName = "NUM")
    })
    private Member member;

    private short mainPerkStyle;
    private short subPerkStyle;
    private short mainPerk1;
    private short mainPerk1Var1;
    private short mainPerk1Var2;
    private short mainPerk1Var3;

    private short mainPerk2;
    private short mainPerk2Var1;
    private short mainPerk2Var2;
    private short mainPerk2Var3;

    private short mainPerk3;
    private short mainPerk3Var1;
    private short mainPerk3Var2;
    private short mainPerk3Var3;

    private short mainPerk4;
    private short mainPerk4Var1;
    private short mainPerk4Var2;
    private short mainPerk4Var3;

    private short subPerk1;
    private short subPerk1Var1;
    private short subPerk1Var2;
    private short subPerk1Var3;

    private short subPerk2;
    private short subPerk2Var1;
    private short subPerk2Var2;
    private short subPerk2Var3;

    public void setMember(Member member){
        this.member = member;
        member.setPerks(this);
    }
}
