package com.lolsearcher.model.request.search.rank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;


@Builder
@AllArgsConstructor
@Data
public class RequestRankDto {

    @NotNull
    private final String summonerId;

    public RequestRankDto(){
        this.summonerId = "";
    }
}
