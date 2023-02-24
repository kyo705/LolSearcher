package com.lolsearcher.model.request.search.rank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;


@Builder
@AllArgsConstructor
@Data
public class RequestRankDto {

    @NotEmpty
    private final String summonerId;

    public RequestRankDto(){
        this.summonerId = "";
    }
}
