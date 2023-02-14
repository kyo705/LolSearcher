package com.lolsearcher.model.entity.match;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Data
@Entity
public class PerkStats implements Serializable {
    @Id
    private Integer id;

    private Short defense;
    private Short flex;
    private Short offense;

}
