package com.lolsearcher.model.input.riot.ingame;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RiotGamesInGamePerksDto {

    List<Short> perkIds; /* 1~4: mainPerkIds 5~6: subPerkIds 7~9: statIds  */
    Short perkStyle;
    Short perkSubStyle;
}
