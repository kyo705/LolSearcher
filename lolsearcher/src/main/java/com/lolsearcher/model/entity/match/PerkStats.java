package com.lolsearcher.model.entity.match;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
public class PerkStats implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Short defense;
    private Short flex;
    private Short offense;

}
