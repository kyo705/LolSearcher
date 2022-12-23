package com.lolsearcher.model.entity.match;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class PerkStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Short defense;
    private Short flex;
    private Short offense;

}
