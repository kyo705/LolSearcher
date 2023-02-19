package com.lolsearcher.model.request.search;

import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
public class RequestRankDto {

    @NotNull
    private final String summonerId;

    public RequestRankDto(){
        this.summonerId = "";
    }
}
