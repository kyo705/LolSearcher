package com.lolsearcher.model.request.front;

import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
public class RequestRankDto {

    @NotNull
    private final String summonerId;
    private final boolean renew;

    public RequestRankDto(){
        this.summonerId = "";
        this.renew = false;
    }
}
