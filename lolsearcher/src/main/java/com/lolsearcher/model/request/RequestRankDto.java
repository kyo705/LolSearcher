package com.lolsearcher.model.request;

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
