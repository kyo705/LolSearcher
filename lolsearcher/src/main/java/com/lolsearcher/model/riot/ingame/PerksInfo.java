package com.lolsearcher.model.riot.ingame;

import com.lolsearcher.model.dto.ingame.PerksDto;
import lombok.Data;

import java.util.List;

@Data
public class PerksInfo {

    List<Long> perkIds; /* 1~4: mainPerkIds 5~6: subPerkIds 7~9: statIds  */
    long perkStyle;
    long perkSubStyle;

    public PerksDto changeToDto(){

        return PerksDto.builder()
                .perkStyle(perkStyle)
                .perkSubStyle(perkSubStyle)
                .mainPerk1(perkIds.get(0)).mainPerk2(perkIds.get(1)).mainPerk3(perkIds.get(2)).mainPerk4(perkIds.get(3))
                .subPerk1(perkIds.get(4)).subPerk2(perkIds.get(5))
                .statPerk1(perkIds.get(6)).statPerk2(perkIds.get(7)).statPerk3(perkIds.get(8))
                .build();
    }
}
