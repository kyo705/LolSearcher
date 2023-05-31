package com.lolsearcher.search.match.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.ToString;
import org.springframework.cache.CacheManager;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.io.Serializable;

import static com.google.common.base.Preconditions.checkArgument;
import static com.lolsearcher.search.match.MatchConstant.PERK_ID_LIST;

@Getter
@ToString(exclude = {"summaryMember"})
@Entity
public class Perks implements Serializable {

    @Id
    private Long id;
    private short mainPerkStyle;
    private short subPerkStyle;
    private short mainPerk1;
    private short mainPerk2;
    private short mainPerk3;
    private short mainPerk4;
    private short subPerk1;
    private short subPerk2;
    private short defense;
    private short flex;
    private short offense;

    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "summaryMemberId", referencedColumnName = "id")
    private SummaryMember summaryMember;

    public void setSummaryMember(SummaryMember summaryMember) throws IllegalAccessException {

        if(summaryMember.getPerks() != null){
            throw new IllegalAccessException("이미 연관관계 설정이 된 SummaryMember 객체입니다.");
        }
        if(this.summaryMember != null) {
            throw new IllegalAccessException("이미 연관된 SummaryMember 객체가 존재합니다.");
        }
        this.summaryMember = summaryMember;
        summaryMember.setPerks(this);
    }

    public void validate(CacheManager cacheManager) {

        checkArgument(
                cacheManager.getCache(PERK_ID_LIST).get(mainPerkStyle) != null,
                "mainPerkStyle must be in boundary"
        );
        checkArgument(
                cacheManager.getCache(PERK_ID_LIST).get(subPerkStyle) != null,
                "subPerkStyle must be in boundary"
        );
        checkArgument(
                cacheManager.getCache(PERK_ID_LIST).get(mainPerk1) != null,
                "mainPerk1 must be in boundary"
        );
        checkArgument(
                cacheManager.getCache(PERK_ID_LIST).get(mainPerk2) != null,
                "mainPerk2 must be in boundary"
        );
        checkArgument(
                cacheManager.getCache(PERK_ID_LIST).get(mainPerk3) != null,
                "mainPerk3 must be in boundary"
        );
        checkArgument(
                cacheManager.getCache(PERK_ID_LIST).get(mainPerk4) != null,
                "mainPerk4 must be in boundary"
        );
        checkArgument(
                cacheManager.getCache(PERK_ID_LIST).get(subPerk1) != null,
                "subPerk1 must be in boundary"
        );
        checkArgument(
                cacheManager.getCache(PERK_ID_LIST).get(subPerk2) != null,
                "subPerk2 must be in boundary"
        );
        checkArgument(
                cacheManager.getCache(PERK_ID_LIST).get(defense) != null,
                "defense must be in boundary"
        );
        checkArgument(
                cacheManager.getCache(PERK_ID_LIST).get(flex) != null,
                "flex must be in boundary"
        );
        checkArgument(
                cacheManager.getCache(PERK_ID_LIST).get(offense) != null,
                "offense must be in boundary"
        );
    }
}
